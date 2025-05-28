import numpy as np
import matplotlib.pyplot as plt
import csv
import os

# ------------------------
# 1. Узлы и разделённые разности
# ------------------------
def uniform_nodes(a, b, n):
    """Равномерная сетка n+1 узел."""
    return np.linspace(a, b, n+1)

def chebyshev_nodes(a, b, n):
    """Чебышёвская сетка n+1 узел на [a,b]."""
    k = np.arange(n+1)
    x_cheb = np.cos((2*k+1)/(2*(n+1))*np.pi)
    # переводим из [-1,1] в [a,b]
    return 0.5*(a+b) + 0.5*(b-a)*x_cheb

def divided_differences(x, y):
    """Коэффициенты Ньютона (разделённые разности)."""
    n = len(x)
    coef = y.copy()
    for j in range(1, n):
        for i in range(n-1, j-1, -1):
            coef[i] = (coef[i] - coef[i-1])/(x[i] - x[i-j])
    return coef

def newton_eval(x_nodes, coef, x):
    """Оценка P_n(x) через вложенную схему Горнера для многочлена Ньютона."""
    n = len(coef)
    result = coef[-1]
    for k in range(n-2, -1, -1):
        result = result*(x - x_nodes[k]) + coef[k]
    return result

# ------------------------
# 2. Интерполяция кубическим сплайном
# ------------------------
def cubic_spline_coeffs(x, y, m0=0.0, mn=0.0):
    """
    Считаем вторые производные M на узлах для натурального сплайна
    или со значениями m0, mn если заданы.
    """
    n = len(x) - 1
    h = np.diff(x)
    # правая часть:
    d = np.zeros(n-1)
    for i in range(1, n):
        d[i-1] = 6*( (y[i+1]-y[i])/h[i] - (y[i]-y[i-1])/h[i-1] )
    # учёт граничных условий 2-го рода:
    d[0]   -= m0 * h[0]
    d[-1]  -= mn * h[-1]
    # трёхдиагоналка:
    A = np.ones(n-2)
    B = 4*np.ones(n-1)
    C = np.ones(n-2)
    # прямой ход:
    for i in range(1, n-1):
        factor = A[i-1]/B[i-1]
        B[i] -= factor * C[i-1]
        d[i] -= factor * d[i-1]
    # обратный ход:
    M = np.zeros(n+1)
    M_int = np.zeros(n-1)
    M_int[-1] = d[-1]/B[-1]
    for i in range(n-3, -1, -1):
        M_int[i] = (d[i] - C[i]*M_int[i+1]) / B[i]
    M[0]   = m0
    M[1:n] = M_int
    M[n]   = mn
    return M

def cubic_spline_eval(x_nodes, y_nodes, M, x):
    """Вычисление куб. сплайна в точке x."""
    # находим i: x_nodes[i] <= x <= x_nodes[i+1]
    i = np.searchsorted(x_nodes, x) - 1
    if i < 0:    i = 0
    if i > len(x_nodes)-2: i = len(x_nodes)-2
    h = x_nodes[i+1] - x_nodes[i]
    A = (x_nodes[i+1] - x)/h
    B = (x - x_nodes[i])/h
    return (A*y_nodes[i] + B*y_nodes[i+1] +
            ((A**3 - A)*M[i] + (B**3 - B)*M[i+1]) * h*h/6)

# ------------------------
# 3. Основные функции варианта
# ------------------------
def f1(x):
    return np.cosh(x) - 2*x

def f2(x):
    return np.abs(np.abs(x) + 1)

functions = [f1, f2]
names     = ['f1', 'f2']

def plot_compare(x, y, p, title, filename):
    plt.figure(figsize=(6,4))
    plt.plot(x, y, 'b-',   label='f(x)')
    plt.plot(x, p, 'r--',  label='P_n(x)')
    plt.scatter(x[0::len(x)//10], p[0::len(x)//10], c='red', s=20)  # несколько точек
    plt.title(title)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(filename, dpi=150)
    plt.close()

def main():
    a, b = -3.0, 3.0
    degrees = [2,4,8,16]
    x_dense = np.arange(a, b+1e-6, 0.01)

    for func, name in zip(functions, names):
        # 1) Ньютона на равномерных
        for n in degrees:
            # узлы
            x_uni = uniform_nodes(a,b,n)
            y_uni = func(x_uni)
            coef_uni = divided_differences(x_uni.copy(), y_uni.copy())

            P_uni = np.array([ newton_eval(x_uni, coef_uni, xi) for xi in x_dense ])

            plot_compare(
                x_dense, func(x_dense), P_uni,
                f"{name} (uniform), n={n}",
                f'output/{name}_uni_n{n}.png'
            )

            if n==2:
                print(f"{name} uniform P2 Newton form:\n  {newton_poly_str(x_uni, coef_uni)}\n")

        # 2) Ньютона на Чебышёвских
        for n in degrees:
            x_ch = chebyshev_nodes(a,b,n)
            # сортируем
            ix = np.argsort(x_ch)
            x_ch, y_ch = x_ch[ix], func(x_ch[ix])
            coef_ch = divided_differences(x_ch.copy(), y_ch.copy())

            P_ch = np.array([ newton_eval(x_ch, coef_ch, xi) for xi in x_dense ])

            plot_compare(
                x_dense, func(x_dense), P_ch,
                f"{name} (Chebyshev), n={n}",
                f'output/{name}_cheb_n{n}.png'
            )

            if n==2:
                print(f"{name} Chebyshev P2 Newton form:\n  {newton_poly_str(x_ch, coef_ch)}\n")

        # 3) Кубический сплайн
        for n in degrees:
            x_s = uniform_nodes(a,b,n)
            y_s = func(x_s)
            # натуральный сплайн (M0=Mn=0)
            M = cubic_spline_coeffs(x_s, y_s, m0=0.0, mn=0.0)
            P_sp = np.array([ cubic_spline_eval(x_s, y_s, M, xi) for xi in x_dense ])

            plot_compare(
                x_dense, func(x_dense), P_sp,
                f"{name} spline, n={n}",
                f'output/{name}_spline_n{n}.png'
            )

    print("Готово! Файлы сохранены в папке output/")

# Вспомог: строковое представление многочлена Ньютона
def newton_poly_str(x, coef):
    s = f"{coef[0]:.5f}"
    for i in range(1, len(coef)):
        term = f" + ({coef[i]:.5f})"
        for j in range(i):
            term += f"(x-{x[j]:.5f})"
        s += term
    return s

if __name__ == '__main__':
    main()

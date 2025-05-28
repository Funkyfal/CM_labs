import numpy as np
from scipy.integrate import quad
import matplotlib.pyplot as plt

# Целевая функция

def f(x):
    return 2**x / np.sqrt(1 - x**2)

# Метод средних прямоугольников

def midpoint_rule(f, a, b, n):
    h = (b - a) / n
    mids = a + h * (np.arange(n) + 0.5)
    return h * np.sum(f(mids))

# Квадратурная формула НАСТ с внутренними равномерными узлами

def nast_quadrature(f, a, b, n):
    # Упорядоченные внутренние узлы: n точек без концов
    nodes = np.linspace(a, b, n + 2)[1:-1]
    # Формируем СЛАУ для весов
    # Система: sum_i w_i * x_i^k = integral_{a..b} x^k dx, k=0..n-1
    A = np.vander(nodes, N=n, increasing=True)  # shape (n, n)
    b_vec = [quad(lambda x, k=k: x**k, a, b)[0] for k in range(n)]
    weights = np.linalg.solve(A, b_vec)
    return np.sum(weights * f(nodes))

if __name__ == '__main__':
    a, b = -1, 1
    exact, _ = quad(f, a, b)
    print("n | Midpoint       | NAST (inner)   | Exact         | Err_mid    | Err_nast")
    print("----------------------------------------------------------------------")
    for n in [2, 4, 6, 8, 10]:
        m_val = midpoint_rule(f, a, b, n)
        n_val = nast_quadrature(f, a, b, n)
        em, en = abs(m_val - exact), abs(n_val - exact)
        print(f"{n:2d} | {m_val:14.10f} | {n_val:14.10f} | {exact:14.10f} | {em:.2e} | {en:.2e}")

    # Дополнительно: график ошибок
    ns = np.arange(2, 22, 2)
    errs_mid = []
    errs_nast = []
    for n in ns:
        errs_mid.append(abs(midpoint_rule(f, a, b, n) - exact))
        errs_nast.append(abs(nast_quadrature(f, a, b, n) - exact))

    plt.figure()
    plt.loglog(ns, errs_mid, marker='o', label='Midpoint Rule')
    plt.loglog(ns, errs_nast, marker='s', label='NAST (inner)')
    plt.xlabel('Number of nodes n')
    plt.ylabel('Absolute error')
    plt.legend()
    plt.title('Convergence of Quadrature Methods')
    plt.grid(True)
    plt.show()

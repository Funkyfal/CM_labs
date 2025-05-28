import numpy as np

def solve_shoot(N=10):
    h = 1/N
    alpha_opt = secant_method(lambda α: residual(α, h), 1, 0.5)
    x_sol, y_sol = rk4(system, 0, [alpha_opt, -alpha_opt], 1, h)
    return x_sol, y_sol[:,0]

# Система уравнений первого порядка
def system(x, y):
    y1, y2 = y
    dy1dx = y2
    dy2dx = (x**2 + 1) * y1 - x**5 - x**3 + 6*x + 2
    return [dy1dx, dy2dx]

# Метод Рунге-Кутты 4-го порядка
def rk4(f, x0, y0, x_end, h):
    n = int((x_end - x0) / h) + 1
    x = np.linspace(x0, x_end, n)
    y = np.zeros((n, len(y0)))
    y[0] = y0
    for i in range(n - 1):
        k1 = f(x[i], y[i])
        k2 = f(x[i] + h/2, y[i] + h/2 * np.array(k1))
        k3 = f(x[i] + h/2, y[i] + h/2 * np.array(k2))
        k4 = f(x[i] + h, y[i] + h * np.array(k3))
        y[i + 1] = y[i] + h/6 * (np.array(k1) + 2*np.array(k2) + 2*np.array(k3) + np.array(k4))
    return x, y

# Функция для вычисления невязки на правой границе
def residual(alpha, h):
    x, y = rk4(system, x0=0, y0=[alpha, -alpha], x_end=1, h=h)
    y1_end = y[-1, 0]  # u(1)
    y2_end = y[-1, 1]  # u'(1)
    return y2_end - y1_end - 2  # Условие u'(1) - u(1) = 2

# Метод секущих
def secant_method(func, x0, x1, tol=1e-6, max_iter=100):
    f0 = func(x0)
    f1 = func(x1)
    for _ in range(max_iter):
        if abs(f1 - f0) < tol:
            break
        x_new = x1 - f1 * (x1 - x0) / (f1 - f0)
        x0, x1 = x1, x_new
        f0, f1 = f1, func(x1)
    return x1

# ========== НАСТРОЙКА ==========
N = 10000           # Количество шагов
h = 1 / N          # Шаг
# ================================

# Обёртка для передачи h в residual
res_func = lambda alpha: residual(alpha, h)

# Подбор оптимального alpha
alpha_opt = secant_method(res_func, x0=1, x1=0.5)

# Решение с найденным alpha
x_sol, y_sol = rk4(system, x0=0, y0=[alpha_opt, -alpha_opt], x_end=1, h=h)
u_solution = y_sol[:, 0]

# Вывод решения
print("Результат решения методом стрельбы:")
print("----------------------------------")
print("   x         u(x)")
for x, u in zip(x_sol, u_solution):
    print(f"{x:.4f} | {u:.6f}")


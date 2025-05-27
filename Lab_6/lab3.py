import numpy as np
import matplotlib.pyplot as plt

# Правая часть уравнения
def f(x, u):
    return np.exp(-u) * (x**2 + 1)

# Система уравнений для k1 и k2 (метод Рунге-Кутты)
def system_g(k, x, u, h, a, c):
    k1, k2 = k
    g1 = k1 - f(x + c[0]*h, u + h*(a[0,0]*k1 + a[0,1]*k2))
    g2 = k2 - f(x + c[1]*h, u + h*(a[1,0]*k1 + a[1,1]*k2))
    return np.array([g1, g2])

# Частная производная g_i по k_i численно
def partial_derivative_g_i_wrt_k_i(g_func, k, x, u, h, a, c, i, eps=1e-8):
    k_eps = k.copy()
    k_eps[i] += eps
    g_eps = g_func(k_eps, x, u, h, a, c)[i]
    g_0 = g_func(k, x, u, h, a, c)[i]
    return (g_eps - g_0) / eps

# Итерационный метод Ньютона по очереди для k1 и k2
def newton_iterative_solver(f_g, x, u, h, a, c, k_init, tol=1e-10, max_iter=50):
    k = k_init.copy()
    for iteration in range(max_iter):
        k_old = k.copy()

        g = f_g(k, x, u, h, a, c)
        dg_dk1 = partial_derivative_g_i_wrt_k_i(f_g, k, x, u, h, a, c, 0)
        if abs(dg_dk1) < 1e-14:
            raise RuntimeError("Производная близка к нулю при обновлении k1")
        k[0] = k[0] - g[0] / dg_dk1

        g = f_g(k, x, u, h, a, c)
        dg_dk2 = partial_derivative_g_i_wrt_k_i(f_g, k, x, u, h, a, c, 1)
        if abs(dg_dk2) < 1e-14:
            raise RuntimeError("Производная близка к нулю при обновлении k2")
        k[1] = k[1] - g[1] / dg_dk2

        if np.linalg.norm(k - k_old, ord=2) < tol:
            return k

    raise RuntimeError("Итерационный метод Ньютона не сошелся")

# Метод Рунге-Кутты 3-го порядка (неявный)
def runge_kutta_3_butcher_implicit(f, x0, u0, x_end, h):
    sqrt3 = np.sqrt(3)
    a = np.array([[ (3 - sqrt3)/6, 0           ],
                  [ sqrt3/3,       (3 - sqrt3)/6 ]])
    c = np.array([ (3 - sqrt3)/6, (3 + sqrt3)/6 ])
    b = np.array([0.5, 0.5])

    x_values = [x0]
    u_values = [u0]
    x = x0
    u = u0

    while x < x_end - 1e-14:
        h_step = min(h, x_end - x)
        k_init = np.array([f(x + c[0]*h_step, u), f(x + c[1]*h_step, u)])
        k = newton_iterative_solver(system_g, x, u, h_step, a, c, k_init)
        u = u + h_step * np.dot(b, k)
        x += h_step
        x_values.append(x)
        u_values.append(u)

    return np.array(x_values), np.array(u_values)

# Автоматический подбор шага по точности для метода Рунге-Кутты
def estimate_with_precision(f, x0, u0, x_end, eps):
    h = 0.1

    while True:
        x1, u1 = runge_kutta_3_butcher_implicit(f, x0, u0, x_end, h)
        x2, u2 = runge_kutta_3_butcher_implicit(f, x0, u0, x_end, h / 2)
        u2_interp = np.interp(x1, x2, u2)
        diff = np.abs(u1 - u2_interp)
        max_diff = np.max(diff)

        print(f"Пробуем шаг h = {h:.5f}, макс. разность = {max_diff:.2e}")

        if max_diff < eps:
            return x1, u1, h

        h /= 2

# Метод Адамса 3-го порядка (явный)
def adams_3(f, x0, u0, x_end, h, u_init=None):
    if u_init is None:
        x_rk, u_rk = runge_kutta_3_butcher_implicit(f, x0, u0, x0 + 2*h, h)
        u_init = u_rk

    x_values = [x0, x0 + h, x0 + 2*h]
    u_values = list(u_init[:3])
    f_values = [f(x_values[i], u_values[i]) for i in range(3)]

    x = x_values[-1]
    while x < x_end - 1e-14:
        x_next = x + h
        u_next = u_values[-1] + (h / 12) * (23*f_values[-1] - 16*f_values[-2] + 5*f_values[-3])
        x_values.append(x_next)
        u_values.append(u_next)

        f_values.append(f(x_next, u_next))
        f_values.pop(0)

        x = x_next

    return np.array(x_values), np.array(u_values)

# Параметры задачи
x0 = 0
u0 = 0
x_end = 1
eps = 1e-3

# Решение методом Рунге-Кутты с подбором шага
x_rk, u_rk, final_h = estimate_with_precision(f, x0, u0, x_end, eps)

# Решение методом Адамса с этим же шагом и стартовыми значениями из Рунге-Кутты
u_init_adams = u_rk[:3]
x_adams, u_adams = adams_3(f, x0, u0, x_end, final_h, u_init=u_init_adams)

# Интерполяция решений для вычисления разности
u_adams_interp = np.interp(x_rk, x_adams, u_adams)
diff = np.abs(u_rk - u_adams_interp)

# Вывод результатов
print(f"\nШаг h = {final_h}")
print(f"{'x':>6} | {'RK u(x)':>15} | {'Adams u(x)':>15} | {'|разность|':>12}")
print("-"*60)
for xi, urk, uad, d in zip(x_rk, u_rk, u_adams_interp, diff):
    print(f"{xi:6.3f} | {urk:15.8f} | {uad:15.8f} | {d:12.2e}")

# Построение графиков
plt.figure(figsize=(12,5))

# Первый график: решения двумя методами
plt.subplot(1, 2, 1)
plt.plot(x_rk, u_rk, 'o-', label='Рунге-Кутты (неявный)')
plt.plot(x_adams, u_adams, 'x--', label='Адамс 3-го порядка')
plt.title('Решения задачи Коши')
plt.xlabel('x')
plt.ylabel('u(x)')
plt.legend()
plt.grid(True)

# Второй график: модуль разности решений
plt.subplot(1, 2, 2)
plt.plot(x_rk, diff, 'r-', label='|RK - Adams|')
plt.title('Модуль разности решений')
plt.xlabel('x')
plt.ylabel('Модуль разности')
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.show()

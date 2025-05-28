#
# Задание 1
#
#  * Вычисление приближённого значения интеграла:
#  *      ∫₀¹ x * exp(x²) dx
#  * с заданной точностью (eps = 1e-6) с помощью двух составных квадратурных формул:
#  * - Формула левых прямоугольников (порядок ошибки O(h))
#  * - Формула Симпсона (порядок ошибки O(h⁴))
#  *
#  * Для каждой формулы применяется автоматический выбор шага по правилу Рунге:
#  *      I = I(h/2) + (I(h/2) - I(h)) / (2^p - 1),
#  * где p – порядок точности метода.
#  *
#  * Аналитически интеграл можно вычислить, сделав замену u = x²:
#  *      ∫₀¹ x * exp(x²) dx = 1/2 * (exp(1)-1)
#  * для каждой КФ вывести приближенное значение интеграла и уточненное значение, а также величину шага
#  * сравнить приближенное значение интеграла с точным, вычисленным аналитически.
#
import math


def f(x):
    return x * math.exp(x**2)


def left_rectangles(a, b, h):
    n = int((b - a) / h)
    s = 0.0
    for i in range(n):
        s += f(a + i * h)
    return s * h


def simpson(a, b, h):
    n = int((b - a) / h)
    if n % 2 == 1:
        n += 1  # ensure even number of intervals
    h = (b - a) / n
    s = f(a) + f(b)
    for i in range(1, n, 2):
        s += 4 * f(a + i * h)
    for i in range(2, n, 2):
        s += 2 * f(a + i * h)
    return s * h / 3


def runge_rule(method, a, b, eps, p):
    h = b - a
    I_h = method(a, b, h)
    while True:
        h /= 2
        I_h2 = method(a, b, h)
        R = I_h2 + (I_h2 - I_h) / (2**p - 1)
        if abs(R - I_h2) < eps:
            return {
                'h': h,
                'I_h': I_h2,
                'refined': R
            }
        I_h = I_h2


if __name__ == '__main__':
    a, b = 0.0, 1.0
    eps = 1e-8

    # Analytical value
    analytical = 0.5 * (math.exp(1) - 1)

    # Left rectangles (order p=1)
    res_left = runge_rule(left_rectangles, a, b, eps, p=1)
    print("Метод левых прямоугольников:")
    print(f"  Шаг h = {res_left['h']}" )
    print(f"  Приближенное I(h) = {res_left['I_h']:.8f}")
    print(f"  Уточненное по Рунге I = {res_left['refined']:.8f}")
    print(f"  Погрешность уточненного = {abs(res_left['refined'] - analytical):.2e}\n")

    # Simpson (order p=4)
    res_simpson = runge_rule(simpson, a, b, eps, p=4)
    print("Метод Симпсона:")
    print(f"  Шаг h = {res_simpson['h']}" )
    print(f"  Приближенное I(h) = {res_simpson['I_h']:.8f}")
    print(f"  Уточненное по Рунге I = {res_simpson['refined']:.8f}")
    print(f"  Погрешность уточненного = {abs(res_simpson['refined'] - analytical):.2e}\n")

    # Analytical
    print(f"Аналитическое значение I = {analytical:.8f}")

/**
 * Задание 1
 *
 * Вычисление приближённого значения интеграла:
 *      ∫₀¹ x * exp(x²) dx
 * с заданной точностью (eps = 1e-6) с помощью двух составных квадратурных формул:
 * - Формула левых прямоугольников (порядок ошибки O(h))
 * - Формула Симпсона (порядок ошибки O(h⁴))
 *
 * Для каждой формулы применяется автоматический выбор шага по правилу Рунге:
 *      I = I(h/2) + (I(h/2) - I(h)) / (2^p - 1),
 * где p – порядок точности метода.
 *
 * Аналитически интеграл можно вычислить, сделав замену u = x²:
 *      ∫₀¹ x * exp(x²) dx = 1/2 * (exp(1)-1)
 * для каждой КФ вывести приближенное значение интеграла и уточненное значение, а также величину шага
 * сравнить приближенное значение интеграла с точным, вычисленным аналитически.
 */
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow

/** Функция-объект, определяющая интегрируемую функцию f(x) = x * exp(x²) */
val f1: (Double) -> Double = { x -> x * exp(x*x) }

/**
 * Композитная формула левых прямоугольников.
 *
 * @param f Функция под интегралом
 * @param a Левый предел интегрирования
 * @param b Правый предел интегрирования
 * @param n Количество разбиений (шаг h = (b-a)/n)
 * @return Приближённое значение интеграла
 */
fun leftRectangles(f: (Double) -> Double, a: Double, b: Double, n: Int): Double {
    val h = (b - a) / n
    var sum = 0.0
    // Используем левую точку каждого отрезка
    for (i in 0 until n) {
        val x = a + i * h
        sum += f(x)
    }
    return h * sum
}

/**
 * Композитная формула Симпсона.
 *
 * @param f Функция под интегралом
 * @param a Левый предел интегрирования
 * @param b Правый предел интегрирования
 * @param n Количество разбиений (должно быть чётным, h = (b-a)/n)
 * @return Приближённое значение интеграла
 */
fun simpson(f: (Double) -> Double, a: Double, b: Double, n: Int): Double {
    if (n % 2 == 1) {
        throw IllegalArgumentException("Для формулы Симпсона n должно быть чётным!")
    }
    val h = (b - a) / n
    var sum = f(a) + f(b)
    // Чётные узлы (исключая концы)
    for (i in 1 until n) {
        val x = a + i * h
        sum += if (i % 2 == 0) 2 * f(x) else 4 * f(x)
    }
    return (h / 3) * sum
}

/**
 * Универсальная функция адаптивного выбора шага по правилу Рунге.
 *
 * @param integration Метод вычисления интеграла, который принимает число разбиений.
 * @param a Левый предел интегрирования
 * @param b Правый предел интегрирования
 * @param initN Начальное количество разбиений
 * @param eps Заданная точность
 * @param order Порядок аппроксимации метода (1 для левых прямоугольников, 4 для Симпсона)
 * @return Triple(приближённое значение интеграла, уточнённое значение по правилу Рунге, использованный шаг h)
 */
fun adaptiveIntegration(
    integration: (Int) -> Double,
    a: Double,
    b: Double,
    initN: Int,
    eps: Double,
    order: Int
): Triple<Double, Double, Double> {
    var n = initN
    val h: () -> Double = { (b - a) / n } // функция для вычисления шага
    var I1 = integration(n)  // значение интеграла с текущим шагом
    n *= 2
    var I2 = integration(n)  // значение интеграла с вдвое меньшим шагом

    // Поправочный множитель (2^p - 1)
    val factor = (2.0).pow(order) - 1
    // Повторяем итерации до достижения нужной точности
    while (abs(I2 - I1) / factor >= eps) {
        I1 = I2
        n *= 2
        I2 = integration(n)
    }
    // Уточнённое значение по правилу Рунге
    val IRefined = I2 + (I2 - I1) / factor
    return Triple(I2, IRefined, h())
}

/**
 * Главная функция для запуска задания 1.
 */
fun main() {
    // Пределы интегрирования
    val a = 0.0
    val b = 1.0
    // Заданная точность
    val eps = 1e-6

    println("Задание 1: Вычисление интеграла от 0 до 1 для функции f(x) = x*exp(x^2)")
    // Аналитическое значение интеграла: (exp(1)-1)/2
    val exactValue = (exp(1.0) - 1) / 2
    println("Аналитическое значение интеграла = $exactValue\n")

    // Метод левых прямоугольников (порядок 1)
    val leftIntegration: (Int) -> Double = { n -> leftRectangles(f1, a, b, n) }
    val (leftApprox, leftRefined, leftStep) = adaptiveIntegration(leftIntegration, a, b, initN = 4, eps = eps, order = 1)
    println("Метод левых прямоугольников:")
    println("  Приближённое значение интеграла = $leftApprox")
    println("  Уточнённое значение (по правилу Рунге) = $leftRefined")
    println("  Использованный шаг h = $leftStep")
    println("  Погрешность по сравнению с точным значением = ${abs(leftRefined - exactValue)}\n")

    // Метод Симпсона (порядок 4)
    // Для Симпсона n должно быть чётным, поэтому начинаем с n=4
    val simpsonIntegration: (Int) -> Double = { n -> simpson(f1, a, b, n) }
    val (simpsonApprox, simpsonRefined, simpsonStep) = adaptiveIntegration(simpsonIntegration, a, b, initN = 4, eps = eps, order = 4)
    println("Метод Симпсона:")
    println("  Приближённое значение интеграла = $simpsonApprox")
    println("  Уточнённое значение (по правилу Рунге) = $simpsonRefined")
    println("  Использованный шаг h = $simpsonStep")
    println("  Погрешность по сравнению с точным значением = ${abs(simpsonRefined - exactValue)}")
}

@file:Suppress("NAME_SHADOWING")

import kotlin.math.abs

fun main() {
    println("Введите значение n - размер матрицы")
    val n = readln().toInt()

    val A = Array(n) { i ->
        DoubleArray(n) { j ->
            if (i == j) 15.0 * (i + 1) * (i + 1)
            else -1.0 / ((i + 1) * (j + 1))
        }
    }

    val X = DoubleArray(n) { i -> i + 2.0 }
    val b = DoubleArray(n) { i -> A[i].zip(X) { a, x -> a * x }.sum() }

    println("Ваша матрица A:")
    A.forEach { row -> println(row.joinToString(" ") { "%.3f".format(it) }) }

    println("\nВектор b: " + b.joinToString(" ") { "%.3f".format(it) })
    println("\nВектор X (истинное решение): " + X.joinToString(" ") { "%.3f".format(it) })

    val epsilon = 1e-9
    val kMax = 1000

    // Метод Якоби
    val jacobiResult = jacobiMethod(A, b, n, epsilon, kMax, X)
    println(jacobiResult)

    // Метод Гаусса-Зейделя
    val gaussSeidelResult = gaussSeidelMethod(A, b, n, epsilon, kMax, X)
    println(gaussSeidelResult)

    // Метод релаксации (ω = 0.5)
    val relaxationResult1 = relaxationMethod(A, b, n, epsilon, kMax, X, 0.5)
    println(relaxationResult1)

    // Метод релаксации (ω = 1.5)
    val relaxationResult2 = relaxationMethod(A, b, n, epsilon, kMax, X, 1.5)
    println(relaxationResult2)
}

fun jacobiMethod(A: Array<DoubleArray>, b: DoubleArray, n: Int, epsilon: Double, kMax: Int, X: DoubleArray): String {
    var xCurrent = DoubleArray(n) { 0.0 }
    val xNext = xCurrent.copyOf()
    var iterations = 0

    while (true) {
        for (i in 0 until n) {
            val sum = (0 until n).sumOf { j -> if (i != j) A[i][j] * xCurrent[j] else 0.0 }
            xNext[i] = (b[i] - sum) / A[i][i]
        }

        val maxDiff = (0 until n).maxOf { abs(xNext[it] - xCurrent[it]) }
        if (maxDiff < epsilon || iterations >= kMax) break

        xCurrent = xNext.copyOf()
        iterations++
    }

    val maxElement = X.zip(xNext) { x, xNext -> abs(x - xNext) }.maxOrNull() ?: 0.0
    val maxElementX = X.maxOfOrNull { abs(it) } ?: 1.0
    val relativeError = maxElement / maxElementX

    return """
        Метод Якоби:
        Решение за $iterations итераций:
        ${xNext.joinToString(" ") { "%.15f".format(it) }}
        Кубическая норма: $maxElement
        Относительная погрешность: $relativeError
    """.trimIndent()
}

fun gaussSeidelMethod(A: Array<DoubleArray>, b: DoubleArray, n: Int, epsilon: Double, kMax: Int, X: DoubleArray): String {
    val xCurrent = DoubleArray(n) { 0.0 }
    var iterations = 0
    var maxDiff = Double.MAX_VALUE

    while (maxDiff >= epsilon && iterations < kMax) {
        maxDiff = 0.0
        for (i in 0 until n) {
            val sum1 = (0 until i).sumOf { j -> A[i][j] * xCurrent[j] }
            val sum2 = (i + 1 until n).sumOf { j -> A[i][j] * xCurrent[j] }
            val xNew = (b[i] - sum1 - sum2) / A[i][i]
            maxDiff = maxOf(maxDiff, abs(xNew - xCurrent[i]))
            xCurrent[i] = xNew
        }
        iterations++
    }

    val maxElement = X.zip(xCurrent) { x, xCurrent -> abs(x - xCurrent) }.maxOrNull() ?: 0.0
    val maxElementX = X.maxOfOrNull { abs(it) } ?: 1.0
    val relativeError = maxElement / maxElementX

    return """
        Метод Гаусса-Зейделя:
        Решение за $iterations итераций:
        ${xCurrent.joinToString(" ") { "%.15f".format(it) }}
        Кубическая норма: $maxElement
        Относительная погрешность: $relativeError
    """.trimIndent()
}



fun relaxationMethod(A: Array<DoubleArray>, b: DoubleArray, n: Int, epsilon: Double, kMax: Int, X: DoubleArray, omega: Double): String {
    val xCurrent = DoubleArray(n) { 0.0 }
    var iterations = 0

    var maxDiff = Double.MAX_VALUE
    while (maxDiff >= epsilon && iterations < kMax) {
        maxDiff = 0.0
        for (i in 0 until n) {
            val sum1 = (0 until i).sumOf { j -> A[i][j] * xCurrent[j] }
            val sum2 = (i + 1 until n).sumOf { j -> A[i][j] * xCurrent[j] }
            val xNew = (1 - omega) * xCurrent[i] + (omega / A[i][i]) * (b[i] - sum1 - sum2)
            maxDiff = maxOf(maxDiff, abs(xNew - xCurrent[i]))
            xCurrent[i] = xNew
        }
        iterations++
    }


    val maxElement = X.zip(xCurrent) { x, xCurrent -> abs(x - xCurrent) }.maxOrNull() ?: 0.0
    val maxElementX = X.maxOfOrNull { abs(it) } ?: 1.0
    val relativeError = maxElement / maxElementX

    return """
        Метод релаксации (ω = $omega):
        Решение за $iterations итераций:
        ${xCurrent.joinToString(" ") { "%.15f".format(it) }}
        Кубическая норма: $maxElement
        Относительная погрешность: $relativeError
    """.trimIndent()
}

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
    var diagonalPredominance = true
    for (i in 0..<n) {
        val sum = A[i].mapIndexed { j, value -> if (i != j) abs(value) else 0.0 }.sum()
        if (abs(A[i][i]) <= sum) {
            diagonalPredominance = false
            break
        }
    }

    val X = DoubleArray(n) { i -> i + 2.0 }
    val b = DoubleArray(n) { i -> A[i].zip(X) { a, x -> a * x }.sum() }

    println("Ваша матрица A:")
    A.forEach { row -> println(row.joinToString(" ") { "%.3f".format(it) }) }
    println(if (diagonalPredominance) "Строгое диагональное преобладание соблюдено"
    else "Строгое диагональное преобладание не соблюдено")
    println("\nВектор b: " + b.joinToString(" ") { "%.3f".format(it) })
    println("Вектор X (истинное решение): " + X.joinToString(" ") { "%.3f".format(it) })

    val epsilon = 1e-9
    val kMax = 1000

    val jacobiResult = jacobiMethod(A, b, n, epsilon, kMax, X)
    println("\n$jacobiResult")

    val gaussSeidelResult = gaussSeidelMethod(A, b, n, epsilon, kMax, X)
    println("\n$gaussSeidelResult")

    val relaxationResult1 = relaxationMethod(A, b, n, epsilon, kMax, X, 0.5)
    println("\n$relaxationResult1")

    val relaxationResult2 = relaxationMethod(A, b, n, epsilon, kMax, X, 1.5)
    println("\n$relaxationResult2")
    ///////////////////////////////////////////////////////////////////////////////
    val ACopy = generateModifiedMatrix(A,n)
    val bC = DoubleArray(n) { i -> ACopy[i].zip(X) { a, x -> a * x }.sum() }
    println("\nВаша измененная матрица A:")
    ACopy.forEach { row -> println(row.joinToString(" ") { "%.3f".format(it) }) }
    println("\nИзмененный вектор b: " + b.joinToString(" ") { "%.3f".format(it) })

    val jacobiResultC = jacobiMethod(ACopy, bC, n, epsilon, kMax, X)
    println("\n$jacobiResultC")

    val gaussSeidelResultC = gaussSeidelMethod(ACopy, bC, n, epsilon, kMax, X)
    println("\n$gaussSeidelResultC")

    val relaxationResult1C = relaxationMethod(ACopy, bC, n, epsilon, kMax, X, 0.5)
    println("\n$relaxationResult1C")

    val relaxationResult2C = relaxationMethod(ACopy, bC, n, epsilon, kMax, X, 1.5)
    println("\n$relaxationResult2C")
}

fun jacobiMethod(A: Array<DoubleArray>, b: DoubleArray, n: Int, epsilon: Double, kMax: Int, X: DoubleArray): String {
    var xCurrent = DoubleArray(n) { 0.0 }
    val xNext = xCurrent.copyOf()
    var iterations = 0

    while (true) {
        for (i in 0..<n) {
            val sum = (0..<n).sumOf { j -> if (i != j) A[i][j] * xCurrent[j] else 0.0 }
            xNext[i] = (b[i] - sum) / A[i][i]
        }

        val maxDiff = (0..<n).maxOf { abs(xNext[it] - xCurrent[it]) }
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
        for (i in 0..<n) {
            val sum1 = (0..<i).sumOf { j -> A[i][j] * xCurrent[j] }
            val sum2 = (i + 1..<n).sumOf { j -> A[i][j] * xCurrent[j] }
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
        for (i in 0..<n) {
            val sum1 = (0..<i).sumOf { j -> A[i][j] * xCurrent[j] }
            val sum2 = (i + 1..<n).sumOf { j -> A[i][j] * xCurrent[j] }
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

fun generateModifiedMatrix(A: Array<DoubleArray>, n: Int): Array<DoubleArray> {
    val modifiedMatrix = Array(n) { DoubleArray(n) }

    modifiedMatrix[0] = A[0].copyOf()

    for (i in 1..<n) {
        val sumOfNonDiagonalElements = A[i].mapIndexed { j, value -> if (i != j) abs(value) else 0.0 }.sum()
        modifiedMatrix[i] = A[i].copyOf()
        modifiedMatrix[i][i] = sumOfNonDiagonalElements
    }

    return modifiedMatrix
}

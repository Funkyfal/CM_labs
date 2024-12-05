fun main() {
    println("Введите значение n - размер матрицы")
    val n = readln().toInt()

    val A = Array(n) { i -> DoubleArray(n) { j ->
        if (i == j) 15.0 * (i + 1) * (i + 1)
        else -1.0 / ((i + 1) * (j + 1))
    }}

    val X = DoubleArray(n) { i -> i + 2.0 }
    val b = DoubleArray(n) { i -> A[i].zip(X) { a, x -> a * x }.sum() }

    println("Ваша матрица A:")
    A.forEach { row -> println(row.joinToString(" ") { "%.3f".format(it) }) }
    println("\nВектор b: " + b.joinToString(" ") { "%.3f".format(it) })

    val epsilon = 1e-9 // Точность
    val kMax = 1000

    println("\nМетод Якоби:")
    var xJacobi = DoubleArray(n) { 0.0 }
    val xNext = xJacobi.copyOf()
    var kJacobi = 0
    while (true) {
        for (i in 0..<n) {
            val sum = (0..<n).sumOf { j -> if (i != j) A[i][j] * xJacobi[j] else 0.0 }
            xNext[i] = (b[i] - sum) / A[i][i]
        }

        val maxDiff = (0..<n).maxOf { kotlin.math.abs(xNext[it] - xJacobi[it]) }
        if (maxDiff < epsilon || kJacobi >= kMax) break

        xJacobi = xNext.copyOf()
        kJacobi++
    }
    println("Решение методом Якоби за $kJacobi итераций:")
    println(xNext.joinToString(" ") { "%.15f".format(it) })

    println("\nМетод Гаусса-Зейделя (ω = 1):")
    val xGaussSeidel = DoubleArray(n) { 0.0 }
    var kGaussSeidel = 0
    while (true) {
        for (i in 0..<n) {
            val sum1 = (0..<i).sumOf { j -> A[i][j] * xGaussSeidel[j] }
            val sum2 = (i + 1..<n).sumOf { j -> A[i][j] * xGaussSeidel[j] }
            xGaussSeidel[i] = (1 - 1.0) * xGaussSeidel[i] + (b[i] - sum1 - sum2) / A[i][i]
        }

        val maxDiff = (0..<n).maxOf { kotlin.math.abs(xGaussSeidel[it] - xJacobi[it]) }
        if (maxDiff < epsilon || kGaussSeidel >= kMax) break

        kGaussSeidel++
    }
    println("Решение методом Гаусса-Зейделя за $kGaussSeidel итераций:")
    println(xGaussSeidel.joinToString(" ") { "%.15f".format(it) })

    println("\nМетод релаксации (ω = 0.5):")
    val xRelaxation1 = DoubleArray(n) { 0.0 } // Начальное приближение
    var kRelaxation1 = 0
    while (true) {
        for (i in 0..<n) {
            val sum1 = (0..<i).sumOf { j -> A[i][j] * xRelaxation1[j] }
            val sum2 = (i + 1..<n).sumOf { j -> A[i][j] * xRelaxation1[j] }
            xRelaxation1[i] = (1 - 0.5) * xRelaxation1[i] + (0.5 / A[i][i]) * (b[i] - sum1 - sum2)
        }

        val maxDiff = (0..<n).maxOf { kotlin.math.abs(xRelaxation1[it] - xGaussSeidel[it]) }
        if (maxDiff < epsilon || kRelaxation1 >= kMax) break

        kRelaxation1++
    }
    println("Решение методом релаксации (ω = 0.5) за $kRelaxation1 итераций:")
    println(xRelaxation1.joinToString(" ") { "%.15f".format(it) })

    println("\nМетод релаксации (ω = 1.5):")
    val xRelaxation2 = DoubleArray(n) { 0.0 } // Начальное приближение
    var kRelaxation2 = 0
    while (true) {
        for (i in 0..<n) {
            val sum1 = (0..<i).sumOf { j -> A[i][j] * xRelaxation2[j] }
            val sum2 = (i + 1..<n).sumOf { j -> A[i][j] * xRelaxation2[j] }
            xRelaxation2[i] = (1 - 1.5) * xRelaxation2[i] + (1.5 / A[i][i]) * (b[i] - sum1 - sum2)
        }

        // Проверка условия выхода
        val maxDiff = (0..<n).maxOf { kotlin.math.abs(xRelaxation2[it] - xRelaxation1[it]) }
        if (maxDiff < epsilon || kRelaxation2 >= kMax) break

        kRelaxation2++
    }
    println("Решение методом релаксации (ω = 1.5) за $kRelaxation2 итераций:")
    println(xRelaxation2.joinToString(" ") { "%.15f".format(it) })
}

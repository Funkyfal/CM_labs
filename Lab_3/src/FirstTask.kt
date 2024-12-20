import kotlin.math.sqrt
import kotlin.math.abs
import kotlin.random.Random

fun qrDecomposition(A: Array<DoubleArray>): Pair<Array<DoubleArray>, Array<DoubleArray>> {
    val n = A.size
    val Q = Array(n) { DoubleArray(n) { 0.0 } }
    val R = Array(n) { DoubleArray(n) { 0.0 } }

    for (i in 0..<n) {
        Q[i][i] = 1.0
        for (j in 0..<n) {
            R[i][j] = A[i][j]
        }
    }

    for (j in 0..<n - 1) {
        for (i in j + 1..<n) {
            val a = R[j][j]
            val b = R[i][j]
            val r = sqrt(a * a + b * b)
            val c = a / r
            val s = -b / r

            for (k in j..<n) {
                val temp = R[j][k]
                R[j][k] = c * temp - s * R[i][k]
                R[i][k] = s * temp + c * R[i][k]
            }

            for (k in 0..<n) {
                val temp = Q[k][j]
                Q[k][j] = c * temp - s * Q[k][i]
                Q[k][i] = s * temp + c * Q[k][i]
            }
        }
    }

    return Pair(Q, R)
}

fun multiplyMatrices(A: Array<DoubleArray>, B: Array<DoubleArray>): Array<DoubleArray> {
    val n = A.size
    val result = Array(n) { DoubleArray(n) { 0.0 } }

    for (i in 0..<n) {
        for (j in 0..<n) {
            for (k in 0..<n) {
                result[i][j] += A[i][k] * B[k][j]
            }
        }
    }

    return result
}

fun main() {
//    println("Введите значение от 10 до 15:")
//    val n = readln().toInt()
//
//    val matrix = Array(n) { DoubleArray(n) { 0.0 } }
//
//    for (i in 0..<n) {
//        for (j in i..<n) {
//            val value = Random.nextInt(1, 10)
//            matrix[i][j] = value.toDouble()
//            matrix[j][i] = value.toDouble()
//        }
//    }

    val matrix = arrayOf(
        doubleArrayOf(4.0, 4.0, 2.0, 1.0, 7.0, 2.0, 4.0, 3.0, 5.0, 4.0),
        doubleArrayOf(4.0, 7.0, 7.0, 2.0, 6.0, 6.0, 5.0, 7.0, 9.0, 6.0),
        doubleArrayOf(2.0, 7.0, 3.0, 8.0, 9.0, 1.0, 1.0, 7.0, 9.0, 6.0),
        doubleArrayOf(1.0, 2.0, 8.0, 3.0, 5.0, 3.0, 4.0, 3.0, 8.0, 3.0),
        doubleArrayOf(7.0, 6.0, 9.0, 5.0, 3.0, 5.0, 9.0, 9.0, 1.0, 3.0),
        doubleArrayOf(2.0, 6.0, 1.0, 3.0, 5.0, 3.0, 4.0, 8.0, 2.0, 5.0),
        doubleArrayOf(4.0, 5.0, 1.0, 4.0, 9.0, 4.0, 7.0, 3.0, 1.0, 7.0),
        doubleArrayOf(3.0, 7.0, 7.0, 3.0, 9.0, 8.0, 3.0, 8.0, 6.0, 8.0),
        doubleArrayOf(5.0, 9.0, 9.0, 8.0, 1.0, 2.0, 1.0, 6.0, 4.0, 8.0),
        doubleArrayOf(4.0, 6.0, 6.0, 3.0, 3.0, 5.0, 7.0, 8.0, 8.0, 6.0)
    )

    println("Исходная матрица A:")
    matrix.forEach { row -> println(row.joinToString(" ") { "%8.2f".format(it) }) }

    var currentMatrix = matrix
    val maxIterations = 10000
    var iterationCount = 0

    for (iteration in 1..maxIterations) {
        val (Q, R) = qrDecomposition(currentMatrix)
        currentMatrix = multiplyMatrices(R, Q)
        iterationCount = iteration

        if (isConverged(currentMatrix)) break
    }

    println("\nКоличество итераций: $iterationCount")
    println("Собственные значения матрицы:")
    for (i in 0..<10) {
        println("λ${i + 1} = ${"%.5f".format(currentMatrix[i][i])}")
    }
}

fun isConverged(matrix: Array<DoubleArray>, epsilon: Double = 1e-5): Boolean {
    val n = matrix.size
    for (i in 0..<n) {
        for (j in 0..<n) {
            if (i != j && abs(matrix[i][j]) > epsilon) {
                return false
            }
        }
    }
    return true
}

import kotlin.random.Random
import kotlin.math.sqrt

fun main() {
//    println("Введите значение от 10 до 15:")
//    val n = readln().toInt()
//
//    // Генерация симметричной матрицы
//    val matrix = Array(n) { DoubleArray(n) { 0.0 } }
//
//    for (i in 0 until n) {
//        for (j in i until n) {
//            val value = (i + 1) * (j + 2)
//            matrix[i][j] = value.toDouble()
//            matrix[j][i] = value.toDouble()
//        }
//    }
    val n = 10
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

    // Степенной метод
    val epsilon = 1e-6  // Погрешность для выхода из итерационного процесса
    var u = DoubleArray(n) { 1.0 }  // Начальный вектор
    var lambda: Double
    var y: DoubleArray
    var normU: Double

    // Функция для умножения матрицы на вектор
    fun multiplyMatrixVector(A: Array<DoubleArray>, x: DoubleArray): DoubleArray {
        val result = DoubleArray(n)
        for (i in 0 until n) {
            result[i] = 0.0
            for (j in 0 until n) {
                result[i] += A[i][j] * x[j]
            }
        }
        return result
    }

    // Функция для вычисления евклидовой нормы вектора
    fun euclideanNorm(x: DoubleArray): Double {
        return sqrt(x.sumOf { it * it })
    }

    // Функция для вычисления нормы ||Ax - lambda * x||
    fun computeNorm(A: Array<DoubleArray>, x: DoubleArray, lambda: Double): Double {
        val Ax = multiplyMatrixVector(A, x)
        val diff = Ax.zip(x).map { it.first - lambda * it.second }.toDoubleArray()
        return euclideanNorm(diff)
    }

    // Начальная нормировка вектора u
    normU = euclideanNorm(u)
    u = u.map { it / normU }.toDoubleArray()

    var iteration = 0
    while (true) {
        iteration++

        // Вычисляем y^(k+1) = A * u^(k)
        y = multiplyMatrixVector(matrix, u)

        // Вычисляем собственное значение lambda^(k+1)
        val uTy = u.zip(y).sumOf { it.first * it.second }
        val uTu = u.sumOf { it * it }
        lambda = uTy / uTu

        // Вычисляем норму y
        normU = euclideanNorm(y)

        // Нормируем вектор y
        val uNext = y.map { it / normU }.toDoubleArray()

        // Проверка на сходимость: ||Au^(k) - lambda1^(k)*u^(k)|| < epsilon
        val diffNorm = euclideanNorm((y.zip(u).map { it.first - lambda * it.second }).toDoubleArray())
        if (diffNorm < epsilon) {
            println("Метод сошелся после $iteration итераций.")
            break
        }

        // Обновляем вектор u^(k+1)
        u = uNext
    }

    println("Наибольшее собственное значение: $lambda")
    println("Собственный вектор, соответствующий этому значению: ${u.joinToString(", ") { "%.4f".format(it) }}")

    // Вычисляем и выводим норму ||Ax1 - lambda1 * x1||
    val norm = computeNorm(matrix, u, lambda)
    println("Норма ||Ax1 - lambda1 * x1||: $norm")
}

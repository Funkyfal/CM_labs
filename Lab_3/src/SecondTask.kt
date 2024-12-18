import kotlin.random.Random
import kotlin.math.sqrt

fun main() {
    println("Введите значение от 10 до 15:")
    val n = readln().toInt()

    // Генерация симметричной матрицы
    val matrix = Array(n) { DoubleArray(n) { 0.0 } }

    for (i in 0..<n) {
        for (j in i..<n) {
            val value = Random.nextInt(1, 10)
            matrix[i][j] = value.toDouble()
            matrix[j][i] = value.toDouble()
        }
    }

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
        for (i in 0..<n) {
            result[i] = 0.0
            for (j in 0..<n) {
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
        // Умножаем матрицу A на вектор x
        val Ax = multiplyMatrixVector(A, x)

        // Вычитаем lambda * x из результата умножения
        val diff = Ax.zip(x).map { it.first - lambda * it.second }.toDoubleArray()

        // Возвращаем евклидову норму разности
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

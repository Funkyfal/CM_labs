import java.io.File
import kotlin.math.abs
import kotlin.math.cosh

fun main() {
    val a = -3.0
    val b = 3.0
    val functions = listOf<(Double) -> Double>(
        { x -> cosh(x) - 2 * x },         // f1(x)
        { x -> abs(abs(x) + 1) }          // f2(x)
    )
    val functionNames = listOf("f1", "f2")

    val degrees = listOf(2, 4, 8, 16)

    for ((index, f) in functions.withIndex()) {
        val funcName = functionNames[index]
        for (n in degrees) {
            val (nodes, values) = generateNodesAndValues(a, b, n, f)
            val xValues = generateSequence(a) { it + 0.01 }.takeWhile { it <= b }.toList()
            val interpolatedValues = xValues.map { x -> interpolateNewton(nodes, values, x) }
            val coef = dividedDifferences(nodes, values)

            saveToCsv("Lab_4/data/${functionNames[index]}_n$n.csv", xValues, xValues.map(f), interpolatedValues)
            if (n == 2) {
                val polynomialString = newtonPolynomialString(nodes, coef)
                println("Аналитическое представление P2(x) для $funcName :")
                println(polynomialString)
                println()
            }
        }
    }
}

fun generateNodesAndValues(a: Double, b: Double, n: Int, f: (Double) -> Double): Pair<List<Double>, List<Double>> {
    val h = (b - a) / n
    val nodes = List(n + 1) { a + it * h }
    val values = nodes.map(f)
    return nodes to values
}

fun interpolateNewton(nodes: List<Double>, values: List<Double>, x: Double): Double {
    val n = nodes.size
    val coef = dividedDifferences(nodes, values)
    var result = coef[0]
    for (i in 1 until n) {
        var term = coef[i]
        for (j in 0 until i) {
            term *= (x - nodes[j])
        }
        result += term
    }
    return result
}

fun dividedDifferences(x: List<Double>, y: List<Double>): List<Double> {
    val n = x.size
    val coef = y.toMutableList()
    for (j in 1 until n) {
        for (i in n - 1 downTo j) {
            coef[i] = (coef[i] - coef[i - 1]) / (x[i] - x[i - 1])
        }
    }
    return coef
}

fun saveToCsv(filename: String, x: List<Double>, fx: List<Double>, px: List<Double>) {
    val file = File(filename)
    file.printWriter().use { out ->
        out.println("x,f(x),P_n(x)")
        for (i in x.indices) {
            out.println("${x[i]},${fx[i]},${px[i]}")
        }
    }
}

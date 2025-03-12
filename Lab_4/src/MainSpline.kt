
import java.io.File
import kotlin.math.abs
import kotlin.math.cosh

fun main() {
    val a = -3.0
    val b = 3.0

    val functions = listOf<(Double) -> Double>(
        { x -> cosh(x) - 2 * x },
        { x -> abs(abs(x) + 1) }
    )
    val secondDerivFuncs = listOf<(Double) -> Double>(
        { x -> cosh(x) },
        { x -> 0.0 }
    )
    val functionNames = listOf("f1", "f2")
    val degrees = listOf(2, 4, 8, 16)

    for ((index, f) in functions.withIndex()) {
        val f2nd = secondDerivFuncs[index]
        for (n in degrees) {
            val nodeCount = n + 1
            val h = (b - a) / n
            val nodes = List(nodeCount) { a + it * h }
            val values = nodes.map(f)

            val m0 = f2nd(nodes.first())
            val mn = f2nd(nodes.last())

            val M = computeSecondDerivatives(nodes, values, m0, mn)

            val xValues = generateSequence(a) { it + 0.01 }.takeWhile { it <= b }.toList()
            val splineValues = xValues.map { x -> cubicSpline(x, nodes, values, M) }
            val fValues = xValues.map(f)

            saveToCsvSpline("Lab_4/data_spline/${functionNames[index]}_n$n.csv", xValues, fValues, splineValues)
        }
    }
}

fun computeSecondDerivatives(nodes: List<Double>, values: List<Double>, m0: Double, mn: Double): List<Double> {
    val n = nodes.size - 1  // число отрезков
    val h = nodes[1] - nodes[0]
    if (n < 1) return listOf(m0, mn)

    val d = DoubleArray(n - 1)
    for (i in 1 until n) {
        d[i - 1] = 6.0 * (values[i + 1] - 2 * values[i] + values[i - 1]) / h
    }

    val a = DoubleArray(n - 1) { 1.0 }
    val b = DoubleArray(n - 1) { 4.0 }
    val c = DoubleArray(n - 1) { 1.0 }

    d[0] = d[0] - m0
    d[n - 2] = d[n - 2] - mn

    val cp = DoubleArray(n - 1)
    val dp = DoubleArray(n - 1)

    cp[0] = c[0] / b[0]
    dp[0] = d[0] / b[0]
    for (i in 1 until n - 1) {
        val denom = b[i] - a[i] * cp[i - 1]
        cp[i] = c[i] / denom
        dp[i] = (d[i] - a[i] * dp[i - 1]) / denom
    }

    val M_internal = DoubleArray(n - 1)
    M_internal[n - 2] = dp[n - 2]
    for (i in n - 3 downTo 0) {
        M_internal[i] = dp[i] - cp[i] * M_internal[i + 1]
    }

    val M = mutableListOf<Double>()
    M.add(m0)
    for (i in 0 until n - 1) {
        M.add(M_internal[i])
    }
    M.add(mn)
    return M
}

fun cubicSpline(x: Double, nodes: List<Double>, values: List<Double>, M: List<Double>): Double {
    val n = nodes.size - 1
    var i = 0
    while (i < n && x > nodes[i + 1]) {
        i++
    }
    val h = nodes[i + 1] - nodes[i]
    val A = (nodes[i + 1] - x) / h
    val B = (x - nodes[i]) / h
    return A * values[i] + B * values[i + 1] + ((A * A * A - A) * M[i] + (B * B * B - B) * M[i + 1]) * (h * h) / 6.0
}

fun saveToCsvSpline(filename: String, x: List<Double>, fx: List<Double>, spline: List<Double>) {
    val file = File(filename)
    file.parentFile.mkdirs()
    file.printWriter().use { out ->
        out.println("x,f(x),Spline(x)")
        for (i in x.indices) {
            out.println("${x[i]},${fx[i]},${spline[i]}")
        }
    }
}
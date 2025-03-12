
import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.cosh

fun main() {
    val a = -3.0
    val b = 3.0

    val functions = listOf<(Double) -> Double>(
        { x -> cosh(x) - 2 * x },      // f1(x)
        { x -> abs(abs(x) + 1) }       // f2(x)
    )
    val functionNames = listOf("f1", "f2")

    val degrees = listOf(2, 4, 8, 16)

    for ((index, f) in functions.withIndex()) {
        val funcName = functionNames[index]

        for (n in degrees) {
            val (chebNodes, chebValues) = generateChebyshevNodesAndValues(a, b, n, f)

            val xValues = generateSequence(a) { it + 0.01 }.takeWhile { it <= b }.toList()

            val coef = dividedDifferences(chebNodes, chebValues)

            val interpolatedValues = xValues.map { x -> interpolateNewton(chebNodes, coef, x) }

            saveToCsvCheb("Lab_4/data_cheb/${funcName}_n$n.csv", xValues, xValues.map(f), interpolatedValues)

            if (n == 2) {
                val polynomialString = newtonPolynomialString(chebNodes, coef)
                println("Аналитическое представление P2(x) для $funcName (Чебышев):")
                println(polynomialString)
                println()
            }
        }
    }
}

fun generateChebyshevNodesAndValues(a: Double, b: Double, n: Int, f: (Double) -> Double)
        : Pair<List<Double>, List<Double>> {
    val nodeCount = n + 1
    val nodes = List(nodeCount) { k ->
        0.5 * (a + b) + 0.5 * (b - a) * cos((2 * k + 1) * PI / (2 * nodeCount))
    }
    val values = nodes.map(f)
    return nodes to values
}

fun newtonPolynomialString(nodes: List<Double>, coefs: List<Double>): String {
    if (coefs.size != 3) {
        return buildString {
            append("P(x) = %.5f".format(coefs[0]))
            for (i in 1 until coefs.size) {
                append(" + (%.5f)".format(coefs[i]))
                for (j in 0 until i) {
                    append("(x - %.5f)".format(nodes[j]))
                }
            }
        }
    }

    val c0 = coefs[0]
    val c1 = coefs[1]
    val c2 = coefs[2]
    val x0 = nodes[0]
    val x1 = nodes[1]

    return """
        P(x) = $c0 
             + $c1 (x - $x0) 
             + $c2 (x - $x0)(x - $x1)
    """.trimIndent()
}

fun saveToCsvCheb(filename: String, x: List<Double>, fx: List<Double>, px: List<Double>) {
    val file = File(filename)
    file.parentFile.mkdirs()
    file.printWriter().use { out ->
        out.println("x,f(x),P_n(x)")
        for (i in x.indices) {
            out.println("${x[i]},${fx[i]},${px[i]}")
        }
    }
}

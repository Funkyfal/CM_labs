import java.io.File
import kotlin.math.abs
import kotlin.math.cosh

data class Point(val x: Double, val y: Double)

fun f1(x: Double): Double = cosh(x) - 2 * x;

fun f2(x: Double): Double = abs(abs(x) + 1)

fun divideDifferences(points: List<Point>): List<Double>{
    val n = points.size
    val diffs = MutableList(n) {points[it].y}

    for(j in 1 until n){
        for(i in n - 1 downTo j){
            diffs[i] = (diffs[i] - diffs[i - 1]) / (points[i].x - points[i - j].x)
        }
    }

    return diffs
}

fun newtonPolynomial(points: List<Point>, x: Double): Double{
    val diffs = divideDifferences(points)
    var term = 1.0
    var result = diffs[0]

    for(i in 1 until points.size){
        term *= (x - points[i - 1].x)
        result += diffs[i] * term
    }

    return result;
}

fun exportToFile(points: List<Point>, filename: String) {
    val file = File(filename)
    file.writeText("x;f(x);P(x)\n")  // Заголовки столбцов с разделителем ";"
    points.forEach { point ->
        val xFormatted = point.x.toString().replace(".", ",")  // Заменяем точку на запятую
        val yFormatted = point.y.toString().replace(".", ",")
        val pFormatted = newtonPolynomial(points, point.x).toString().replace(".", ",")
        file.appendText("$xFormatted;$yFormatted;$pFormatted\n")  // Разделитель столбцов — ";"
    }
}

fun main() {
    val a = -3.0
    val b = 3.0
    val degrees = listOf(2, 4, 8, 16)

    for(n in degrees){
        val pointsF1 = List(n + 1) { i ->
            val x = a + i * (b - a) / n
            Point(x, f1(x))
        }

        val pointsF2 = List(n + 1) {i ->
            val x = a + i * (b - a) / n
            Point(x, f2(x))
        }

        if(n == 2){
            val diffs = divideDifferences(pointsF1)
            println("------------------------------------------")
            println("Интерполяционный многочлен второй степени для f1(x)" +
                    "\n P(x) = ${diffs[0]} + ${diffs[1]}(x - ${pointsF1[0].x})" +
                    "+ ${diffs[2]}(x - ${pointsF1[0].x})(x - ${pointsF1[1].x})\n")
            println("Интерполяционный многочлен второй степени для f1(x)" +
                    "\n P(x) = ${diffs[0]} + ${diffs[1]}(x - ${pointsF2[0].x})" +
                    "+ ${diffs[2]}(x - ${pointsF1[0].x})(x - ${pointsF1[1].x})")
            println("------------------------------------------")
        }

        println("------------------------------------------")
        exportToFile(pointsF1, "F1data_n${n}.csv")
        println("Данные для n = $n экспортированы в файл F1data_n${n}.csv")

        exportToFile(pointsF2, "F2data_n${n}.csv")
        println("Данные для n = $n экспортированы в файл F2data_n${n}.csv")
        println("------------------------------------------")
    }
}

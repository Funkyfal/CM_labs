import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.sqrt;

public class Main {
    public static void main(String[] args) {
        System.out.println("Выберите размер желаемой матрицы от 1 до 15.");
        Scanner in = new Scanner(System.in);
        Random random = new Random();
        int n = in.nextInt();

        while (n < 1 || n > 15) {
            System.out.println("Введите правильное число.");
            n = in.nextInt();
        }
        double[][] A = new double[n][n];
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            b[i] = random.nextInt(100);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    A[i][j] = 5 * (i + 1);
                } else {
                    A[i][j] = 0.1 * (i + 1) * (j + 1);
                }
            }
        }

        double[][] T = new double[n][n];
        T[0][0] = sqrt(A[0][0]);
        for (int i = 1; i < T.length; i++) {
            T[0][i] = A[0][i] / T[0][0];
        }
        for (int i = 1; i < T.length; i++) {
            for (int j = i; j < T.length; j++) {
                double sum = 0;
                if (j == i) {
                    for (int k = 0; k <= i - 1; k++) {
                        sum += T[k][i] * T[k][i];
                    }
                    T[i][i] = sqrt(A[i][i] - sum);
                } else {
                    for (int k = 0; k <= i - 1; k++) {
                        sum += T[k][i] * T[k][j];
                    }
                    T[i][j] = (A[i][j] - sum) / T[i][i];
                }
            }
        }

        System.out.println("Matrix A:");
        printMatrix(A);
        System.out.println("\nVector b: \n" + Arrays.toString(b) + "\n");
        System.out.println("******************* ЗАДАНИЕ 1 *******************");
        System.out.println("Matrix T:");
        printMatrix(T);

        double[] Y = new double[n];
        Y[0] = b[0] / T[0][0];
        for (int i = 1; i < n; i++) {
            double sum = 0;
            for (int k = 0; k <= i - 1; k++) {
                sum += T[k][i] * Y[k];
            }
            Y[i] = (b[i] - sum) / T[i][i];
        }
        System.out.println("\nVector Y: \n" + formatDoubleArray(Y) + "\n");

        double[] X = new double[n];
        X[n - 1] = Y[n - 1] / T[n - 1][n - 1];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int k = i + 1; k <= n - 1; k++) {
                sum += T[i][k] * X[k];
            }
            X[i] = (Y[i] - sum) / T[i][i];
        }
        System.out.println("Vector X: \n" + formatDoubleArray(X) + "\n");
        System.out.println("Найдем норму ||Ax - b||:");
        System.out.println("Ax:");
        double[][] Ax = matrixMultiply(A, X);
        printMatrix(Ax);
        System.out.println("Ax - b:");
        double[] Ax_b = matrixSubtract(Ax, b);
        System.out.println(Arrays.toString(Ax_b));
        System.out.println("\n||Ax - b||₁ = " + Arrays.stream(Ax_b).sum());

        System.out.println("******************* ЗАДАНИЕ 2 *******************");

        double[][] Q = new double[n][n];
        double[][] R = new double[n][n];
        for (int i = 0; i < n; i++) {
            Q[i][i] = 1.0;
            System.arraycopy(A[i], 0, R[i], 0, n);
        }

        for (int j = 0; j < n - 1; j++) {
            for (int i = j + 1; i < n; i++) {
                double a = R[j][j];
                double bVal = R[i][j];
                double r = sqrt(a * a + bVal * bVal);
                double c = a / r;
                double s = -bVal / r;

                for (int k = j; k < n; k++) {
                    double temp = R[j][k];
                    R[j][k] = c * temp - s * R[i][k];
                    R[i][k] = s * temp + c * R[i][k];
                }

                for (int k = 0; k < n; k++) {
                    double temp = Q[k][j];
                    Q[k][j] = c * temp - s * Q[k][i];
                    Q[k][i] = s * temp + c * Q[k][i];
                }
            }
        }

        System.out.println("Matrix Q:");
        printMatrix(Q);

        System.out.println("Matrix R:");
        printFullMatrix(R);

        double[] QTb = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                QTb[i] += Q[j][i] * b[j];
            }
        }

        double[] X1 = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            X1[i] = QTb[i];
            for (int j = i + 1; j < n; j++) {
                X1[i] -= R[i][j] * X1[j];
            }
            X1[i] /= R[i][i];
        }

        System.out.println("\nVector X (solution): \n" + Arrays.toString(X1) + "\n");
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.print("| ");
            for (double element : row) {
                System.out.printf("%6.2f ", element);
            }
            System.out.print(" |\n");
        }
    }

    public static void printFullMatrix(double[][] matrix) {
        DecimalFormat df = new DecimalFormat("0.0000000000E0");  // Форматируем с точностью до 10 знаков
        for (double[] row : matrix) {
            System.out.print("| ");
            for (double element : row) {
                System.out.print(df.format(element) + " ");
            }
            System.out.println("|");
        }
    }

    public static String formatDoubleArray(double[] array) {
        StringBuilder formattedArray = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            formattedArray.append(String.format("%.2f", array[i]));
            if (i < array.length - 1) {
                formattedArray.append(", ");
            }
        }
        formattedArray.append("]");
        return formattedArray.toString();
    }

    public static double[] matrixSubtract(double[][] first, double[] second) {
        double[] result = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i][0] - second[i];
        }
        return result;
    }

    public static double[][] matrixMultiply(double[][] first, double[] second) {
        double[][] result = new double[first.length][1];
        for (int i = 0; i < first.length; i++) {
            for (int k = 0; k < first[0].length; k++) {
                result[i][0] += first[i][k] * second[k];
            }
        }
        return result;
    }
}
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
        double[][] Tt = new double[n][n];
        T[0][0] = sqrt(A[0][0]);
        for (int i = 1; i < T.length; i++) {
            T[0][i] = A[0][i] / T[0][0];
            Tt[i][0] = T[0][i];
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
                Tt[j][i] = T[i][j];
            }
        }

        System.out.println("Matrix A:");
        printMatrix(A);
        System.out.println("\nVector b: \n" + Arrays.toString(b) + "\n");
        System.out.println("******************* ЗАДАНИЕ 1 *******************");
        System.out.println("Matrix T:");
        printMatrix(T);
        System.out.println("\nMatrix T transposed:");
        printMatrix(Tt);

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

    public static double[][] matrixMultiply(double[][] first, double[][] second) {
        double[][] result = new double[first.length][second[0].length];
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < second[0].length; j++) {
                for (int k = 0; k < first[0].length; k++) {
                    result[i][j] += first[i][k] * second[k][j];
                }
            }
        }
        return result;
    }

    public static double[][] matrixSubtract(double[][] first, double[][] second) {
        double[][] result = new double[first.length][first[0].length];
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < first[0].length; j++) {
                result[i][j] = first[i][j] - second[i][j];
            }
        }
        return result;
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
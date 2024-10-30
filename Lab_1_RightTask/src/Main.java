import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.sqrt;

public class Main {
    public static void main(String[] args) {
        System.out.println("Выберите размер желаемой матрицы от 1 до 15.");
        Scanner in = new Scanner(System.in);
        Random random = new Random(100);
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
        System.out.println("Matrix T:");
        printMatrix(T);
        System.out.println("\nMatrix T transposed:");
        printMatrix(Tt);
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
}
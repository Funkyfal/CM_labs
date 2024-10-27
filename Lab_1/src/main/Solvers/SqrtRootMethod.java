package main.Solvers;

import main.Matrix;

import java.util.Vector;

import static java.lang.Math.sqrt;

public class SqrtRootMethod {
    private Matrix A;
    private Vector<Double> vectorB;

    public SqrtRootMethod() {
    }

    public SqrtRootMethod(Matrix a, Vector<Double> vectorB_) {
        A = a;
        vectorB = vectorB_;
        A.isSymmetric();
        if (!isPositiveDefinite()) {
            throw new IllegalArgumentException("Матрица не является положительно определенной");
        }
    }

    public boolean isPositiveDefinite() {
        for (int size = 1; size <= A.getN(); size++) {
            Matrix minor = new Matrix(size, size);
            for (int i = 0; i < size; i++) {
                System.arraycopy(A.getData()[i], 0, minor.getData()[i], 0, size);
            }
            if (determinant(minor) <= 0) {
                return false;
            }
        }
        return true;
    }

    private double determinant(Matrix matrix) {
        int n = matrix.getN();
        if (n == 1) {
            return matrix.getData()[0][0];
        }
        if (n == 2) {
            return matrix.getData()[0][0] * matrix.getData()[1][1] - matrix.getData()[0][1] * matrix.getData()[1][0];
        }

        double det = 0;
        for (int j = 0; j < n; j++) {
            Matrix subMatrix = new Matrix(n - 1, n - 1);
            for (int row = 1; row < n; row++) {
                for (int col = 0, subCol = 0; col < n; col++) {
                    if (col == j) continue;
                    subMatrix.getData()[row - 1][subCol++] = matrix.getData()[row][col];
                }
            }
            det += (j % 2 == 0 ? 1 : -1) * matrix.getData()[0][j] * determinant(subMatrix);
        }
        return det;
    }

    public double[] solve() {
        Matrix T = new Matrix(A.getN(), A.getM());
        Matrix Tt = new Matrix(A.getN(), A.getM());
        double[][] dataT = new double[T.getN()][T.getM()];
        double[][] dataA = A.getData();

        dataT[0][0] = sqrt(dataA[0][0]);
        for (int i = 1; i < dataT.length; i++) {
            dataT[0][i] = dataA[0][i] / dataT[0][0];
        }
        for (int i = 1; i < dataT.length; i++) {
            for (int j = i; j < dataT.length; j++) {
                if (j == i) {
                    double sum = 0;
                    for (int k = 0; k <= i - 1; k++) {
                        sum += dataT[k][i] * dataT[k][i];
                    }
                    dataT[i][i] = sqrt(dataA[i][i] - sum);
                } else {
                    double sum = 0;
                    for (int k = 0; k <= i - 1; k++) {
                        sum += dataT[k][i] + dataT[k][j];
                    }
                    dataT[i][j] = (dataA[i][j] - sum) / dataT[i][i];
                }
            }
        }

        T.setData(dataT);
        System.out.println("Матрица T для матрицы A: \n" + T);
        Tt.setData(T.transpose().getData());
        System.out.println("Матрица T' для матрицы A: \n" + Tt);

        //Находим решение T'y = b
        double[][] dataTt = Tt.getData();
        Vector<Double> Y = new Vector<>();
        Y.add(vectorB.getFirst() / dataTt[0][0]);
        for(int i = 1; i < dataTt.length; i++){
            double sum = 0;
            for(int k = 0; k <= i - 1; k++){
                sum += dataTt[k][i] * Y.get(k);
            }
            Y.add((vectorB.get(i) - sum) / dataTt[i][i]);
        }
        System.out.println(Y);
        return new double[]{1, 2, 3};
    }
}

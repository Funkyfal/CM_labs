package main;

import java.util.Vector;

public class Matrix {
    private int N;
    private int M;
    private double[][] data;

    public Matrix(int n, int m) {
        N = n;
        M = m;
        setData(new double[getN()][getM()]);
    }

    public Matrix(double[][] data) {
        N = data.length;
        M = data[0].length;
        this.setData(new double[getN()][getN()]);
        for (int i = 0; i < getN(); i++) {
            System.arraycopy(data[i], 0, this.getData()[i], 0, getN());
        }
    }

    public Matrix(double[] data) {
        N = 1;
        M = data.length;
        this.setData(new double[1][getM()]);
        for (int i = 0; i < getM(); i++) {
            this.getData()[0][i] = data[i];
        }
    }

    public Matrix(Vector<Double> data) {
        N = 1;
        M = data.size();
        this.setData(new double[1][getM()]);
        for (int i = 0; i < getM(); i++) {
            this.getData()[0][i] = data.get(i);
        }
    }

    public Matrix add(Matrix other) {
        if (getN() != other.getN() || getM() != other.getM()) {
            throw new IllegalArgumentException("Матрицы должны быть одинакового размера для сложения.");
        }
        Matrix result = new Matrix(getN(), getN());
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
                result.getData()[i][j] = getData()[i][j] + other.getData()[i][j];
            }
        }
        return result;
    }

    public Matrix subtract(Matrix other) {
        if (getN() != other.getN() || getM() != other.getM()) {
            throw new IllegalArgumentException("Матрицы должны быть одинакового размера для вычитания.");
        }
        Matrix result = new Matrix(getN(), getM());
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
                result.getData()[i][j] = getData()[i][j] - other.getData()[i][j];
            }
        }
        return result;
    }

    public Matrix multiply(Matrix other) {
        if (getM() != other.getN()) {
            throw new IllegalArgumentException("Количество столбцов первой матрицы должно быть равно количеству строк второй матрицы.");
        }
        Matrix result = new Matrix(getN(), other.getM());
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < other.getM(); j++) {
                for (int k = 0; k < getM(); k++) {
                    result.getData()[i][j] += getData()[i][k] * other.getData()[k][j];
                }
            }
        }
        return result;
    }

    public Matrix transpose() {
        Matrix transposed = new Matrix(getM(), getN());
        for (int i = 0; i < getN(); i++) {
            for (int j = 0; j < getM(); j++) {
                transposed.getData()[j][i] = this.getData()[i][j];
            }
        }
        return transposed;
    }

    public void isSymmetric() {
        if (getN() != getM()) {
            throw new IllegalArgumentException("Для симметричности матрица должна быть квадратной");
        }
        for (int i = 0; i < getN(); i++) {
            for (int j = i + 1; j < getM(); j++) {
                if (getData()[i][j] != getData()[j][i]) {
                    throw new IllegalArgumentException("Матрица не симметричная");
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < N; i++) {
            result.append("|");
            for (int j = 0; j < M; j++) {
                result.append(String.format(" %6.2f", data[i][j]));
            }
            result.append(" |\n");
        }
        return result.toString();
    }



    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
        N = data.length;
        M = data[0].length;
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }
}

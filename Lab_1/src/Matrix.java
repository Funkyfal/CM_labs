public class Matrix {
    private int N;
    private int M;
    private double[][] data;

    // Конструктор для инициализации матрицы заданного размера
    public Matrix(int n, int m) {
        N = n;
        M = m;
        setData(new double[N][N]);
    }

    // Конструктор для создания матрицы из двумерного массива
    public Matrix(double[][] data) {
        N = data.length;
        M = data[0].length;
        this.setData(new double[N][N]);
        for (int i = 0; i < N; i++) {
            System.arraycopy(data[i], 0, this.getData()[i], 0, N);
        }
    }

    // Метод сложения матриц
    public Matrix add(Matrix other) {
        if (N != other.N || M != other.M) {
            throw new IllegalArgumentException("Матрицы должны быть одинакового размера для сложения.");
        }
        Matrix result = new Matrix(N, N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result.getData()[i][j] = getData()[i][j] + other.getData()[i][j];
            }
        }
        return result;
    }

    // Метод вычитания матриц
    public Matrix subtract(Matrix other) {
        if (N != other.N || M != other.M) {
            throw new IllegalArgumentException("Матрицы должны быть одинакового размера для вычитания.");
        }
        Matrix result = new Matrix(N, N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result.getData()[i][j] = getData()[i][j] - other.getData()[i][j];
            }
        }
        return result;
    }

    // Метод умножения матриц
    public Matrix multiply(Matrix other) {
        if (N != other.M) {
            throw new IllegalArgumentException("Количество столбцов первой матрицы должно быть равно количеству строк второй матрицы.");
        }
        Matrix result = new Matrix(N, other.M);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < other.M; j++) {
                for (int k = 0; k < M; k++) {
                    result.getData()[i][j] += getData()[i][k] * other.getData()[k][j];
                }
            }
        }
        return result;
    }

    public Matrix transpose() {
        Matrix transposed = new Matrix(M, N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                transposed.data[j][i] = this.data[i][j];
            }
        }
        return transposed;
    }

    public void isSymmetric() {
        if (N != M) {
            throw new IllegalArgumentException("Для симметричности матрица должна быть квадратной");
        }
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < M; j++) {
                if (data[i][j] != data[j][i]) {
                    throw new IllegalArgumentException("Матрица не симметричная");
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result.append(getData()[i][j]).append(" ");
            }
            result.append("\n");
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
}

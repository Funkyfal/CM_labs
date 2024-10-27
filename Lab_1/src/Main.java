import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            String filename = "input.txt";
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String[] dimensions = br.readLine().trim().split("\\s+");
            int N = Integer.parseInt(dimensions[0]);

            Matrix matrix = new Matrix(N, N);
            double[][] data = new double[N][N];
            double[] vectorB = new double[N];

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().trim().split("\\s+");
                for (int j = 0; j < N; j++) {
                    data[i][j] = Double.parseDouble(line[j]);
                }
            }
            matrix.setData(data);
            matrix.isSymmetric();

            String[] vectorLine = br.readLine().trim().split("\\s+");
            for (int i = 0; i < N; i++) {
                vectorB[i] = Double.parseDouble(vectorLine[i]);
            }
            br.close();

            System.out.println("Матрица А:\n" + matrix);
            System.out.println("A':\n" + matrix.transpose());

            System.out.println("Вектор b:");
            System.out.println(Arrays.toString(vectorB));

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

    }
}
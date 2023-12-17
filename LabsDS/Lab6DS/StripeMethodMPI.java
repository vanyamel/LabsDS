import mpi.*;

public class StripeMethodMPI {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        final int N = 4; // Розмір матриці (припустимо, що він ділиться на кількість процесів)

        int[] flatA = new int[N * N];  // Одновимірний масив для матриці A
        int[] flatB = new int[N * N];  // Одновимірний масив для матриці B
        int[] flatC = new int[N * N];  // Одновимірний масив для результату
        int[] subFlatA = new int[N * N / size]; // Частина матриці A для кожного процесу

        // Ініціалізація матриць A та B
        if (rank == 0) {
            initializeFlatMatrix(flatA, N);
            initializeFlatMatrix(flatB, N);
        }

        // Розсилка стрічок матриці A
        MPI.COMM_WORLD.Scatter(flatA, 0, N * N / size, MPI.INT, subFlatA, 0, N * N / size, MPI.INT, 0);

        // Розсилка всієї матриці B
        MPI.COMM_WORLD.Bcast(flatB, 0, N * N, MPI.INT, 0);

        // Обчислення частини добутку
        int[] subFlatC = new int[N * N / size];
        for (int i = 0; i < N / size; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    subFlatC[i * N + j] += subFlatA[i * N + k] * flatB[k * N + j];
                }
            }
        }

        // Збір результатів у матрицю C
        MPI.COMM_WORLD.Gather(subFlatC, 0, N * N / size, MPI.INT, flatC, 0, N * N / size, MPI.INT, 0);

        // Вивід результату
        if (rank == 0) {
            printFlatMatrix(flatC, N);
        }

        MPI.Finalize();
    }

    private static void initializeFlatMatrix(int[] matrix, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i * size + j] = i + j;
            }
        }
    }

    private static void printFlatMatrix(int[] matrix, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i * size + j] + " ");
            }
            System.out.println();
        }
    }
}

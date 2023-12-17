import mpi.*;

public class FoxMultiplication {

    public static void main(String[] args) throws MPIException {

            MPI.Init(args);
            int rank = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();

            final int N = 4;

            int[] flatA = new int[N * N];
            int[] flatB = new int[N * N];
            int[] flatC = new int[N * N];

            if (rank == 0) {
                initializeFlatMatrix(flatA, N);
                initializeFlatMatrix(flatB, N);
            }

            double startTime = MPI.Wtime();
            foxMethod(flatA, flatB, flatC, N, rank, size);
            double endTime = MPI.Wtime();

            if (rank == 0) {
                System.out.println("Time taken for Fox method: " + (endTime - startTime) + " seconds");
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

    private static void foxMethod(int[] flatA, int[] flatB, int[] flatC, int N, int rank, int size) throws MPIException {
        if (size == 1) {
            sequentialMultiply(flatA, flatB, flatC, N);
            return;
        }

        int gridSize = (int)Math.sqrt(size);
        if (N % gridSize != 0 || size != gridSize * gridSize) {
            throw new IllegalArgumentException("Розмір матриці та кількість процесів не відповідають вимогам методу Фокса.");
        }


        int blockSize = N / gridSize;
        int[] blockA = new int[blockSize * blockSize];
        int[] blockB = new int[blockSize * blockSize];
        int[] blockC = new int[blockSize * blockSize];

        // Отримання координат процесу у ґріді
        int gridRow = rank / gridSize;
        int gridCol = rank % gridSize;

        distributeBlocks(flatA, blockA, N, blockSize, gridRow, gridCol, gridSize);

        for (int step = 0; step < gridSize; step++) {
            int bRow = (gridRow + step) % gridSize;

            broadcastBlocks(flatB, blockB, N, blockSize, bRow, gridSize, rank);

            multiplyBlocks(blockA, blockB, blockC, blockSize);

            shiftBlockA(blockA, blockSize, gridRow, gridSize);
        }

        gatherBlocks(blockC, flatC, N, blockSize, rank, gridSize);
    }

    private static void distributeBlocks(int[] flatA, int[] blockA, int N, int blockSize, int gridRow, int gridCol, int gridSize) {
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                blockA[i * blockSize + j] = flatA[(gridRow * blockSize + i) * N + (gridCol * blockSize + j)];
            }
        }
    }
    private static void broadcastBlocks(int[] flatB, int[] blockB, int N, int blockSize, int bRow, int gridSize, int rank) throws MPIException {
        int root = bRow * gridSize + (rank % gridSize);
        if (rank / gridSize == bRow) {
            System.arraycopy(flatB, 0, blockB, 0, blockSize * blockSize);
        }
        MPI.COMM_WORLD.Bcast(blockB, 0, blockSize * blockSize, MPI.INT, root);
    }
    private static void multiplyBlocks(int[] blockA, int[] blockB, int[] blockC, int blockSize) {
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                for (int k = 0; k < blockSize; k++) {
                    blockC[i * blockSize + j] += blockA[i * blockSize + k] * blockB[k * blockSize + j];
                }
            }
        }
    }
    private static void shiftBlockA(int[] blockA, int blockSize, int gridRow, int gridSize) throws MPIException {
        int[] temp = new int[blockSize * blockSize];
        System.arraycopy(blockA, 0, temp, 0, blockSize * blockSize);

        int source = (gridRow + 1) % gridSize;
        int dest = (gridRow - 1 + gridSize) % gridSize;

        MPI.COMM_WORLD.Sendrecv_replace(temp, 0, blockSize * blockSize, MPI.INT, dest, 0, source, 0);

        System.arraycopy(temp, 0, blockA, 0, blockSize * blockSize);
    }
    private static void gatherBlocks(int[] blockC, int[] flatC, int N, int blockSize, int rank, int gridSize) throws MPIException {
        int[] recvBuffer = null;
        if (rank == 0) {
            recvBuffer = new int[N * N];
        }
        MPI.COMM_WORLD.Gather(blockC, 0, blockSize * blockSize, MPI.INT, recvBuffer, 0, blockSize * blockSize, MPI.INT, 0);

        if (rank == 0) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    for (int k = 0; k < blockSize; k++) {
                        for (int l = 0; l < blockSize; l++) {
                            flatC[(i * blockSize + k) * N + (j * blockSize + l)] = recvBuffer[(i * gridSize + j) * blockSize * blockSize + k * blockSize + l];
                        }
                    }
                }
            }
        }
    }
    private static void sequentialMultiply(int[] flatA, int[] flatB, int[] flatC, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    flatC[i * N + j] += flatA[i * N + k] * flatB[k * N + j];
                }
            }
        }
    }
}

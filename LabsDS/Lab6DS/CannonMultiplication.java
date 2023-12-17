import mpi.*;

public class CannonMultiplication {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int size = MPI.COMM_WORLD.Size();
        int rank = MPI.COMM_WORLD.Rank();
        int gridSize = (int)Math.sqrt(size);


        final int N = 4;

        int[] flatA = new int[N * N];
        int[] flatB = new int[N * N];
        int[] flatC = new int[N * N];

        if (rank == 0) {
            initializeFlatMatrix(flatA, N);
            initializeFlatMatrix(flatB, N);
        }

        double startTime = MPI.Wtime();
        cannonMethod(flatA, flatB, flatC, N, rank, size);
        double endTime = MPI.Wtime();

        if (rank == 0) {
            System.out.println("Time taken for Cannon method: " + (endTime - startTime) + " seconds");
            printFlatMatrix(flatC, N);
        }

        MPI.Finalize();
    }

    private static void cannonMethod(int[] flatA, int[] flatB, int[] flatC, int N, int rank, int size) throws MPIException {
        int gridSize = (int)Math.sqrt(size);
        int blockSize = N / gridSize;
        int gridRow = rank / gridSize;
        int gridCol = rank % gridSize;

        int srcA = (gridRow * gridSize + (gridCol - 1 + gridSize) % gridSize);
        int dstA = (gridRow * gridSize + (gridCol + 1) % gridSize);

        int srcB = ((gridRow - 1 + gridSize) % gridSize) * gridSize + gridCol;
        int dstB = ((gridRow + 1) % gridSize) * gridSize + gridCol;


        // Ініціалізація блоків матриць
        int[] blockA = new int[blockSize * blockSize];
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                blockA[i * blockSize + j] = flatA[(gridRow * blockSize + i) * N + (gridCol * blockSize + j)];
            }
        }
        int[] blockB = new int[blockSize * blockSize];
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                blockB[i * blockSize + j] = flatB[(gridRow * blockSize + i) * N + (gridCol * blockSize + j)];
            }
        }
        shiftBlockInitial(blockA, blockSize, gridRow, true, gridSize, rank);
        shiftBlockInitial(blockB, blockSize, gridCol, false, gridSize, rank);
        int[] blockC = new int[blockSize * blockSize];



        // Цикл обчислень
        for (int step = 0; step < gridSize; step++) {
            multiplyBlocks(blockA, blockB, blockC, blockSize);
            shiftBlock(blockA, blockSize, srcA, dstA);
            shiftBlock(blockB, blockSize, srcB, dstB);
        }

        gatherBlocks(blockC, flatC, N, blockSize, rank, size);
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

    private static void shiftBlockInitial(int[] block, int blockSize, int shiftCount, boolean isRowShift, int gridSize, int rank) throws MPIException {
        int[] temp = new int[blockSize * blockSize];
        int source, dest;
        int size = MPI.COMM_WORLD.Size();
        if (isRowShift) {
            // Зсув блоків матриці A вліво
            int row = rank / gridSize;
            source = (row * gridSize + (rank - shiftCount + gridSize) % gridSize);
            dest = (row * gridSize + (rank + shiftCount) % gridSize);
        } else {
            // Зсув блоків матриці B вгору
            int col = rank % gridSize;
            source = ((rank - shiftCount * gridSize + size) % size + col);
            dest = ((rank + shiftCount * gridSize) % size + col);
        }

        MPI.COMM_WORLD.Sendrecv(block, 0, blockSize * blockSize, MPI.INT, dest, 0,
                temp, 0, blockSize * blockSize, MPI.INT, source, 0);
        System.arraycopy(temp, 0, block, 0, blockSize * blockSize);
    }


    private static void shiftBlock(int[] block, int blockSize, int source, int dest) throws MPIException {
        int[] temp = new int[blockSize * blockSize];
        MPI.COMM_WORLD.Sendrecv_replace(block, 0, blockSize * blockSize, MPI.INT, dest, 0, source, 0);
    }
    private static void multiplyBlocks(int[] blockA, int[] blockB, int[] blockC, int blockSize) {
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                blockC[i * blockSize + j] = 0;
                for (int k = 0; k < blockSize; k++) {
                    blockC[i * blockSize + j] += blockA[i * blockSize + k] * blockB[k * blockSize + j];
                }
            }
        }
    }
    private static void gatherBlocks(int[] blockC, int[] flatC, int N, int blockSize, int rank, int size) throws MPIException {
        int[] recvBuffer = rank == 0 ? new int[N * N] : null;
        MPI.COMM_WORLD.Gather(blockC, 0, blockSize * blockSize, MPI.INT, recvBuffer, 0, blockSize * blockSize, MPI.INT, 0);

        if (rank == 0) {
            // Заповнення кінцевої матриці зібраними блоками
            for (int i = 0; i < size; i++) {
                int row = (i / blockSize) * blockSize;
                int col = (i % blockSize) * blockSize;
                for (int j = 0; j < blockSize; j++) {
                    for (int k = 0; k < blockSize; k++) {
                        flatC[(row + j) * N + col + k] = recvBuffer[i * blockSize * blockSize + j * blockSize + k];
                    }
                }
            }
        }
    }

}

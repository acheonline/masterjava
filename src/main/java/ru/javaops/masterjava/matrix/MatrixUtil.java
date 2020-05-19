package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // done implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        int matrixSize = matrixA.length;
        int[][] matrixCResult = new int[matrixSize][matrixSize];
        class MultipleResult {
            int[][] matrixC;

            MultipleResult(int[][] mC) {
                matrixC = mC;
            }

            public int[][] getMatrixC() {
                return matrixC;
            }
        }

        class MultipleTask implements Callable<MultipleResult> {
            int[][] matrixA;
            int[][] matrixB;

            MultipleTask(int[][] mA, int[][] mB) {
                this.matrixA = mA;
                this.matrixB = mB;
            }

            @Override
            public MultipleResult call() {
                return new MultipleResult(miltiplyProcessing(matrixA, matrixB));
            }
        }

        CompletionService<MultipleResult> completionService = new ExecutorCompletionService<>(executor);
        Stream<Future<MultipleResult>> futureStream;
            futureStream = Stream.of(matrixA, matrixB)
                    .map(m -> completionService.submit(new MultipleTask(matrixA, matrixB)));

       // List<Future<MultipleResult>> futures = futureStream.collect(Collectors.toList());
        try {
            matrixCResult = futureStream.findFirst().get().get(10, TimeUnit.SECONDS).getMatrixC();
            return matrixCResult;
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return matrixCResult;
    }

    // done optimization by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        // final int[][] matrixC = new int[matrixSize][matrixSize];

//       NB! the last version of optimization, but I kept previous one a bit modified that gave me less time of execution
//        *******

//        int thatColumn[] = new int[matrixSize];
//
//        try {
//            for (int j = 0; ; j++) {
//                for (int k = 0; k < matrixSize; k++) {
//                    thatColumn[k] = matrixB[k][j];
//                }
//
//                for (int i = 0; i < matrixSize; i++) {
//                    int thisRow[] = matrixA[i];
//                    int sum = 0;
//                    for (int k = 0; k < matrixSize; k++) {
//                        sum += thisRow[k] * thatColumn[k];
//                    }
//                    matrixC[i][j] = sum;
//                }
//            }
//        } catch (IndexOutOfBoundsException ignored) { }
        return miltiplyProcessing(matrixA, matrixB);
    }

    private static int[][] miltiplyProcessing(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        int[][] matrixC = new int[matrixSize][matrixSize];
        int thatColumn[] = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }

            //extract initialization of variables out of loop and gain better performance
            int thisRow[];
            int sum;
            for (int i = 0; i < matrixSize; i++) {
                thisRow = matrixA[i];
                sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}

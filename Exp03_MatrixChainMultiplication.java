import java.util.Scanner;

/**
 * Experiment 3: Matrix Chain Multiplication using Dynamic Programming
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp03_MatrixChainMultiplication {

    /**
     * Returns the minimum number of scalar multiplications needed.
     * dims[] holds dimensions: matrix i has dimensions dims[i-1] x dims[i]
     * dp[i][j] = min cost to multiply matrices i..j
     */
    static int matrixChainOrder(int[] dims, int n) {
        int[][] dp = new int[n][n];
        // chain length l = 1 has 0 cost (single matrix, no multiplication)
        for (int l = 2; l < n; l++) {                  // chain length
            for (int i = 1; i < n - l + 1; i++) {      // starting matrix
                int j = i + l - 1;                      // ending matrix
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {      // split position
                    int cost = dp[i][k] + dp[k + 1][j]
                             + dims[i - 1] * dims[k] * dims[j];
                    if (cost < dp[i][j]) dp[i][j] = cost;
                }
            }
        }
        return dp[1][n - 1];
    }

    // Also print the optimal parenthesization
    static int[][] s;   // split table

    static int matrixChainOrderWithSplit(int[] dims, int n) {
        int[][] dp = new int[n][n];
        s = new int[n][n];

        for (int l = 2; l < n; l++) {
            for (int i = 1; i < n - l + 1; i++) {
                int j = i + l - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int cost = dp[i][k] + dp[k + 1][j]
                             + dims[i - 1] * dims[k] * dims[j];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        s[i][j]  = k;
                    }
                }
            }
        }
        return dp[1][n - 1];
    }

    static void printOptimalParens(int i, int j) {
        if (i == j) {
            System.out.print("A" + i);
        } else {
            System.out.print("(");
            printOptimalParens(i, s[i][j]);
            printOptimalParens(s[i][j] + 1, j);
            System.out.print(")");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of matrices: ");
        int numMatrices = sc.nextInt();

        int[] dims = new int[numMatrices + 1];
        System.out.println("Enter " + (numMatrices + 1) + " dimensions (d0 d1 ... dn):");
        System.out.println("  Matrix i has dimensions dims[i-1] x dims[i]");
        for (int i = 0; i <= numMatrices; i++) dims[i] = sc.nextInt();

        System.out.println("\nMatrices and their dimensions:");
        for (int i = 1; i <= numMatrices; i++)
            System.out.printf("  A%d : %d x %d%n", i, dims[i - 1], dims[i]);

        long startTime = System.nanoTime();                          // Start timer
        int minCost = matrixChainOrderWithSplit(dims, numMatrices + 1);
        long endTime = System.nanoTime();                            // Stop timer

        System.out.println("\nMinimum number of scalar multiplications: " + minCost);
        System.out.print("Optimal parenthesization: ");
        printOptimalParens(1, numMatrices);
        System.out.println();

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n^3) time, O(n^2) space");

        sc.close();
    }
}

import java.util.Scanner;

/**
 * Experiment 4: 0/1 Knapsack Problem using Dynamic Programming
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp04_KnapsackDP {

    /**
     * Solves 0/1 Knapsack using bottom-up DP table.
     * dp[i][w] = max value using first i items with capacity w
     */
    static int knapsack(int[] weights, int[] values, int n, int W) {
        int[][] dp = new int[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                // Don't take item i
                dp[i][w] = dp[i - 1][w];
                // Take item i if it fits
                if (weights[i - 1] <= w) {
                    int withItem = dp[i - 1][w - weights[i - 1]] + values[i - 1];
                    if (withItem > dp[i][w]) dp[i][w] = withItem;
                }
            }
        }
        return dp[n][W];
    }

    // Traceback to find which items are included
    static void findItems(int[][] dp, int[] weights, int[] values, int n, int W) {
        System.out.println("\nItems selected (item index | weight | value):");
        int w = W;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                System.out.printf("  Item %2d  |  weight: %3d  |  value: %3d%n",
                        i, weights[i - 1], values[i - 1]);
                w -= weights[i - 1];
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of items: ");
        int n = sc.nextInt();

        int[] weights = new int[n];
        int[] values  = new int[n];

        System.out.println("Enter weight and value for each item:");
        for (int i = 0; i < n; i++) {
            System.out.print("  Item " + (i + 1) + " -> Weight: ");
            weights[i] = sc.nextInt();
            System.out.print("  Item " + (i + 1) + " -> Value : ");
            values[i]  = sc.nextInt();
        }

        System.out.print("Enter knapsack capacity (W): ");
        int W = sc.nextInt();

        // Build DP table for traceback
        int[][] dp = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                dp[i][w] = dp[i - 1][w];
                if (weights[i - 1] <= w) {
                    int withItem = dp[i - 1][w - weights[i - 1]] + values[i - 1];
                    if (withItem > dp[i][w]) dp[i][w] = withItem;
                }
            }
        }

        long startTime = System.nanoTime();          // Start timer
        int maxValue = knapsack(weights, values, n, W);
        long endTime = System.nanoTime();            // Stop timer

        System.out.println("\nMaximum value that can be carried: " + maxValue);
        findItems(dp, weights, values, n, W);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n * W) time and space");

        sc.close();
    }
}

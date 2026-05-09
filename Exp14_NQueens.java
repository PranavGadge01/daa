import java.util.Scanner;

/**
 * Experiment 14: N-Queens Problem using Backtracking
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 *
 * Finds ALL solutions for placing N non-attacking queens on an N x N board.
 */
public class Exp14_NQueens {

    static int   N;
    static int[] board;       // board[row] = column of queen in that row
    static int   solutions = 0;

    // Check if placing a queen at (row, col) is safe
    static boolean isSafe(int row, int col) {
        for (int r = 0; r < row; r++) {
            int c = board[r];
            if (c == col || Math.abs(c - col) == Math.abs(r - row))
                return false;
        }
        return true;
    }

    // Backtracking solver
    static void solve(int row) {
        if (row == N) {
            solutions++;
            printBoard();
            return;
        }
        for (int col = 0; col < N; col++) {
            if (isSafe(row, col)) {
                board[row] = col;
                solve(row + 1);
                // Backtrack (board[row] will be overwritten next iteration)
            }
        }
    }

    static void printBoard() {
        System.out.println("  Solution " + solutions + ":");
        for (int r = 0; r < N; r++) {
            System.out.print("    ");
            for (int c = 0; c < N; c++) {
                System.out.print(board[r] == c ? "Q " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter N (board size, e.g. 8 for 8-Queens): ");
        N     = sc.nextInt();
        board = new int[N];

        System.out.println("\nSolving " + N + "-Queens Problem...\n");

        long startTime = System.nanoTime();     // Start timer
        solve(0);
        long endTime = System.nanoTime();       // Stop timer

        System.out.println("Total solutions for " + N + "-Queens: " + solutions);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(N!) — pruned significantly by backtracking");

        sc.close();
    }
}

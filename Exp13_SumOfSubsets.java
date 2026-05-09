import java.util.Arrays;
import java.util.Scanner;

/**
 * Experiment 13: Sum of Subsets Problem using Backtracking
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 *
 * Finds ALL subsets of the given set that sum to the target value.
 */
public class Exp13_SumOfSubsets {

    static int[] set;
    static int   n;
    static int   target;
    static int   solutionCount = 0;

    /**
     * Backtracking recursive function.
     *
     * @param index   current element index being considered
     * @param current current sum accumulated
     * @param chosen  boolean array tracking included elements
     */
    static void solve(int index, int current, boolean[] chosen) {
        // Pruning: if current sum already exceeds target, backtrack
        if (current > target) return;

        // Solution found
        if (current == target) {
            solutionCount++;
            System.out.print("  Solution " + solutionCount + ": { ");
            for (int i = 0; i < n; i++)
                if (chosen[i]) System.out.print(set[i] + " ");
            System.out.println("}");
            return;
        }

        // No more elements to consider
        if (index == n) return;

        // Pruning: remaining elements can't reach target
        int remaining = 0;
        for (int i = index; i < n; i++) remaining += set[i];
        if (current + remaining < target) return;

        // Include set[index]
        chosen[index] = true;
        solve(index + 1, current + set[index], chosen);

        // Exclude set[index]
        chosen[index] = false;
        solve(index + 1, current, chosen);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of elements in the set: ");
        n = sc.nextInt();
        set = new int[n];

        System.out.println("Enter the elements:");
        for (int i = 0; i < n; i++) set[i] = sc.nextInt();

        System.out.print("Enter target sum: ");
        target = sc.nextInt();

        // Sort for better pruning
        Arrays.sort(set);

        System.out.println("\nSet (sorted): " + Arrays.toString(set));
        System.out.println("Target sum  : " + target);
        System.out.println("\nSubsets that sum to " + target + ":");

        boolean[] chosen = new boolean[n];

        long startTime = System.nanoTime();         // Start timer
        solve(0, 0, chosen);
        long endTime = System.nanoTime();           // Stop timer

        if (solutionCount == 0)
            System.out.println("  No subset found with the given sum.");
        else
            System.out.println("\nTotal solutions found: " + solutionCount);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(2^n) worst case, improved by pruning");

        sc.close();
    }
}

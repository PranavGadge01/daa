import java.util.Arrays;
import java.util.Scanner;

/**
 * Experiment 5: Fractional Knapsack using Greedy Approach
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp05_FractionalKnapsack {

    static class Item implements Comparable<Item> {
        int weight, value;
        double ratio;  // value-to-weight ratio

        Item(int w, int v) {
            weight = w;
            value  = v;
            ratio  = (double) v / w;
        }

        @Override
        public int compareTo(Item other) {
            // Sort descending by ratio
            return Double.compare(other.ratio, this.ratio);
        }
    }

    static double fractionalKnapsack(Item[] items, int W) {
        Arrays.sort(items);    // Greedy: sort by ratio descending
        double totalValue = 0.0;
        int remaining = W;

        System.out.println("\nGreedy selection (item index | weight taken | value gained):");
        for (int i = 0; i < items.length; i++) {
            if (remaining == 0) break;

            int take = Math.min(items[i].weight, remaining);
            double gained = take * items[i].ratio;
            totalValue += gained;
            remaining  -= take;

            System.out.printf("  Item %2d  | weight taken: %5.1f / %-3d | value gained: %8.2f%n",
                    i + 1, (double) take, items[i].weight, gained);
        }
        return totalValue;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of items: ");
        int n = sc.nextInt();
        Item[] items = new Item[n];

        System.out.println("Enter weight and value for each item:");
        for (int i = 0; i < n; i++) {
            System.out.print("  Item " + (i + 1) + " -> Weight: ");
            int w = sc.nextInt();
            System.out.print("  Item " + (i + 1) + " -> Value : ");
            int v = sc.nextInt();
            items[i] = new Item(w, v);
        }

        System.out.print("Enter knapsack capacity (W): ");
        int W = sc.nextInt();

        long startTime = System.nanoTime();                  // Start timer
        double maxValue = fractionalKnapsack(items, W);
        long endTime = System.nanoTime();                    // Stop timer

        System.out.printf("%nMaximum value in knapsack: %.4f%n", maxValue);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n log n) due to sorting");

        sc.close();
    }
}

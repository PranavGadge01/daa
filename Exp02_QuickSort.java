import java.util.Scanner;

/**
 * Experiment 2: Quick Sort using Divide & Conquer
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp02_QuickSort {

    // Partition around pivot (last element)
    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // Divide & Conquer recursive sort
    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    static void printArray(int[] arr) {
        for (int val : arr) System.out.print(val + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of elements: ");
        int n = sc.nextInt();
        int[] arr = new int[n];

        System.out.println("Enter " + n + " elements:");
        for (int i = 0; i < n; i++) arr[i] = sc.nextInt();

        System.out.print("Array before sorting: ");
        printArray(arr);

        long startTime = System.nanoTime();          // Start timer
        quickSort(arr, 0, n - 1);
        long endTime = System.nanoTime();            // Stop timer

        System.out.print("Array after sorting:  ");
        printArray(arr);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n log n) average, O(n^2) worst case");

        sc.close();
    }
}

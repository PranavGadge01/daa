import java.util.Scanner;

/**
 * Experiment 1: Merge Sort using Divide & Conquer
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp01_MergeSort {

    // Merge two sorted halves
    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else               arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Divide & Conquer recursive sort
    static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
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
        mergeSort(arr, 0, n - 1);
        long endTime = System.nanoTime();            // Stop timer

        System.out.print("Array after sorting:  ");
        printArray(arr);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n log n)");

        sc.close();
    }
}

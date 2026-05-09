import java.util.Scanner;

/**
 * Experiment 10: String Matching - KMP (Knuth-Morris-Pratt) Algorithm
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp10_KMP {

    /**
     * Build the Failure Function (partial match table / LPS array).
     * lps[i] = length of the longest proper prefix of pattern[0..i]
     *          that is also a suffix.
     */
    static int[] buildLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        lps[0] = 0;

        int len = 0;   // length of previous longest prefix-suffix
        int i   = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) len = lps[len - 1];
                else          lps[i++] = 0;
            }
        }
        return lps;
    }

    /**
     * KMP search: finds all occurrences of pattern in text in O(n + m).
     */
    static void kmpSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        int[] lps = buildLPS(pattern);

        System.out.println("\nLPS (Failure Function) Array:");
        System.out.print("  Pattern: ");
        for (char c : pattern.toCharArray()) System.out.printf("%-4c", c);
        System.out.println();
        System.out.print("  LPS    : ");
        for (int x : lps) System.out.printf("%-4d", x);
        System.out.println();

        System.out.println("\nSearching for pattern: \"" + pattern + "\"");
        System.out.println("In text            : \"" + text + "\"");
        System.out.println();

        boolean found = false;
        int i = 0;   // index for text
        int j = 0;   // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
            }
            if (j == m) {
                System.out.println("  Pattern found at index: " + (i - j));
                found = true;
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) j = lps[j - 1];
                else        i++;
            }
        }

        if (!found) System.out.println("  Pattern NOT found in the text.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the text   : ");
        String text = sc.nextLine();

        System.out.print("Enter the pattern: ");
        String pattern = sc.nextLine();

        long startTime = System.nanoTime();          // Start timer
        kmpSearch(text, pattern);
        long endTime = System.nanoTime();            // Stop timer

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n + m) — preprocessing O(m), search O(n)");

        sc.close();
    }
}

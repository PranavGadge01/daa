import java.util.Scanner;

/**
 * Experiment 9: String Matching - Rabin-Karp Algorithm
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp09_RabinKarp {

    static final int BASE  = 256;   // number of characters in alphabet
    static final int PRIME = 101;   // a prime number to reduce hash collisions

    /**
     * Rabin-Karp: finds all occurrences of pattern in text.
     * Uses rolling hash to compute substring hashes in O(1).
     */
    static void rabinKarp(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        if (m > n) { System.out.println("Pattern longer than text. No match possible."); return; }

        // h = BASE^(m-1) % PRIME
        int h = 1;
        for (int i = 0; i < m - 1; i++) h = (h * BASE) % PRIME;

        int patHash  = 0;   // hash of pattern
        int textHash = 0;   // hash of current text window

        // Initial hash computation for pattern and first window
        for (int i = 0; i < m; i++) {
            patHash  = (BASE * patHash  + pattern.charAt(i)) % PRIME;
            textHash = (BASE * textHash + text.charAt(i))    % PRIME;
        }

        boolean found = false;
        int comparisons = 0;

        System.out.println("\nSearching for pattern: \"" + pattern + "\"");
        System.out.println("In text            : \"" + text + "\"");
        System.out.println();

        for (int i = 0; i <= n - m; i++) {
            comparisons++;

            // Hash match: verify character by character (spurious hit check)
            if (patHash == textHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    System.out.println("  Pattern found at index: " + i);
                    found = true;
                }
            }

            // Compute rolling hash for next window
            if (i < n - m) {
                textHash = (BASE * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;
                if (textHash < 0) textHash += PRIME;
            }
        }

        if (!found) System.out.println("  Pattern NOT found in the text.");
        System.out.println("\nTotal windows checked: " + comparisons);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the text   : ");
        String text = sc.nextLine();

        System.out.print("Enter the pattern: ");
        String pattern = sc.nextLine();

        long startTime = System.nanoTime();          // Start timer
        rabinKarp(text, pattern);
        long endTime = System.nanoTime();            // Stop timer

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n + m) average, O(n*m) worst case");

        sc.close();
    }
}

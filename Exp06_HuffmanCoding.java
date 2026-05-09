import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Experiment 6: Huffman Coding using Greedy Approach
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp06_HuffmanCoding {

    // Tree node
    static class Node implements Comparable<Node> {
        char   ch;
        int    freq;
        Node   left, right;

        Node(char ch, int freq) {
            this.ch = ch; this.freq = freq;
        }

        Node(int freq, Node left, Node right) {
            this.ch = '\0'; this.freq = freq;
            this.left = left; this.right = right;
        }

        boolean isLeaf() { return left == null && right == null; }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.freq, other.freq);
        }
    }

    // Build Huffman Tree using a min-heap (priority queue)
    static Node buildHuffmanTree(char[] chars, int[] freqs) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (int i = 0; i < chars.length; i++)
            pq.offer(new Node(chars[i], freqs[i]));

        while (pq.size() > 1) {
            Node left  = pq.poll();
            Node right = pq.poll();
            pq.offer(new Node(left.freq + right.freq, left, right));
        }
        return pq.poll();
    }

    // Generate codes via DFS
    static void generateCodes(Node root, String code, Map<Character, String> codeMap) {
        if (root == null) return;
        if (root.isLeaf()) {
            codeMap.put(root.ch, code.isEmpty() ? "0" : code);
            return;
        }
        generateCodes(root.left,  code + "0", codeMap);
        generateCodes(root.right, code + "1", codeMap);
    }

    // Print tree structure (in-order style)
    static void printTree(Node root, String indent, boolean isRight) {
        if (root == null) return;
        System.out.println(indent + (isRight ? "└── " : "├── ")
                + (root.isLeaf() ? ("'" + root.ch + "'") : "INT")
                + " [freq=" + root.freq + "]");
        printTree(root.left,  indent + (isRight ? "    " : "│   "), false);
        printTree(root.right, indent + (isRight ? "    " : "│   "), true);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of distinct characters: ");
        int n = sc.nextInt();
        char[] chars = new char[n];
        int[]  freqs = new int[n];

        System.out.println("Enter each character and its frequency:");
        for (int i = 0; i < n; i++) {
            System.out.print("  Char " + (i + 1) + " -> Character: ");
            chars[i] = sc.next().charAt(0);
            System.out.print("  Char " + (i + 1) + " -> Frequency: ");
            freqs[i] = sc.nextInt();
        }

        long startTime = System.nanoTime();                         // Start timer
        Node root = buildHuffmanTree(chars, freqs);
        Map<Character, String> codeMap = new HashMap<>();
        generateCodes(root, "", codeMap);
        long endTime = System.nanoTime();                           // Stop timer

        System.out.println("\nHuffman Tree (root at top):");
        printTree(root, "", true);

        System.out.println("\nHuffman Codes:");
        System.out.printf("  %-10s %-10s %-12s%n", "Character", "Frequency", "Code");
        System.out.println("  " + "-".repeat(34));
        int totalBits = 0;
        for (int i = 0; i < n; i++) {
            String code = codeMap.get(chars[i]);
            System.out.printf("  %-10c %-10d %-12s%n", chars[i], freqs[i], code);
            totalBits += freqs[i] * code.length();
        }
        System.out.println("\nTotal bits required for encoding: " + totalBits);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(n log n)");

        sc.close();
    }
}

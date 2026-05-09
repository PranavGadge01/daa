import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Experiment 12: Max Flow Application - Bipartite Matching
 * Uses Ford-Fulkerson (BFS) to find maximum bipartite matching.
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 *
 * Graph model:
 *   Super-source (S) --1--> each left node --1--> each right node --1--> Super-sink (T)
 */
public class Exp12_BipartiteMatching {

    static int   totalV;
    static int[][] cap;

    static boolean bfs(int src, int sink, int[] parent) {
        boolean[] visited = new boolean[totalV];
        Queue<Integer> q  = new LinkedList<>();
        q.offer(src);
        visited[src] = true;
        parent[src]  = -1;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < totalV; v++) {
                if (!visited[v] && cap[u][v] > 0) {
                    parent[v]  = u;
                    visited[v] = true;
                    if (v == sink) return true;
                    q.offer(v);
                }
            }
        }
        return false;
    }

    static int maxFlow(int src, int sink) {
        int[] parent = new int[totalV];
        int   flow   = 0;

        while (bfs(src, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != src; v = parent[v])
                pathFlow = Math.min(pathFlow, cap[parent[v]][v]);
            for (int v = sink; v != src; v = parent[v]) {
                cap[parent[v]][v] -= pathFlow;
                cap[v][parent[v]] += pathFlow;
            }
            flow += pathFlow;
        }
        return flow;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of left-side  nodes (L): ");
        int L = sc.nextInt();
        System.out.print("Enter number of right-side nodes (R): ");
        int R = sc.nextInt();

        // Node numbering:
        //   0       = super-source S
        //   1..L    = left nodes
        //   L+1..L+R= right nodes
        //   L+R+1   = super-sink T
        totalV = L + R + 2;
        int S = 0, T = L + R + 1;

        cap = new int[totalV][totalV];

        // S -> left nodes
        for (int i = 1; i <= L; i++) cap[S][i] = 1;
        // right nodes -> T
        for (int j = L + 1; j <= L + R; j++) cap[j][T] = 1;

        System.out.print("Enter number of edges between left and right: ");
        int E = sc.nextInt();
        System.out.println("Enter edges (left_node right_node), 1-indexed:");
        for (int k = 0; k < E; k++) {
            int u = sc.nextInt();         // left  node (1-indexed)
            int v = sc.nextInt();         // right node (1-indexed)
            cap[u][L + v] = 1;           // left_u -> right_v
        }

        long startTime = System.nanoTime();         // Start timer
        int matching = maxFlow(S, T);
        long endTime = System.nanoTime();           // Stop timer

        System.out.println("\nMaximum Bipartite Matching: " + matching);

        // Extract matched pairs from residual graph
        System.out.println("\nMatched Pairs (left -> right):");
        for (int i = 1; i <= L; i++) {
            for (int j = L + 1; j <= L + R; j++) {
                // If cap[j][i] > 0, flow went i->j (original edge is saturated)
                if (cap[j][i] > 0) {
                    System.out.println("  Left " + i + "  <-->  Right " + (j - L));
                }
            }
        }

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(V * E) via BFS-based max-flow");

        sc.close();
    }
}

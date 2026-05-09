import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Experiment 11: Max Flow - Ford-Fulkerson Algorithm (using BFS / Edmonds-Karp)
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp11_FordFulkerson {

    static int V;                 // number of vertices
    static int[][] capacity;      // residual capacity matrix

    /**
     * BFS to find an augmenting path from src to sink.
     * Returns true if a path exists; fills parent[] with the path.
     */
    static boolean bfs(int src, int sink, int[] parent) {
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(src);
        visited[src] = true;
        parent[src]  = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < V; v++) {
                if (!visited[v] && capacity[u][v] > 0) {
                    parent[v]  = u;
                    visited[v] = true;
                    if (v == sink) return true;
                    queue.offer(v);
                }
            }
        }
        return false;
    }

    /**
     * Ford-Fulkerson using BFS (Edmonds-Karp variant).
     * Guarantees O(V * E^2) time complexity.
     */
    static int fordFulkerson(int src, int sink) {
        int[] parent   = new int[V];
        int   maxFlow  = 0;
        int   iteration = 0;

        System.out.println("\nAugmenting Paths:");

        while (bfs(src, sink, parent)) {
            iteration++;

            // Find minimum residual capacity along the path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != src; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v]);
            }

            // Print path
            System.out.print("  Path " + iteration + ": ");
            LinkedList<Integer> path = new LinkedList<>();
            for (int v = sink; v != src; v = parent[v]) path.addFirst(v);
            path.addFirst(src);
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) System.out.print(" -> ");
                System.out.print(path.get(i));
            }
            System.out.println("  |  Flow: " + pathFlow);

            // Update residual capacities
            for (int v = sink; v != src; v = parent[v]) {
                int u = parent[v];
                capacity[u][v] -= pathFlow;
                capacity[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of vertices: ");
        V = sc.nextInt();

        capacity = new int[V][V];   // initialized to 0

        System.out.print("Enter number of edges: ");
        int E = sc.nextInt();

        System.out.println("Enter edges (source destination capacity), 0-indexed:");
        for (int i = 0; i < E; i++) {
            int u = sc.nextInt(), v = sc.nextInt(), c = sc.nextInt();
            capacity[u][v] += c;    // support multiple edges between same pair
        }

        System.out.print("Enter source vertex     : ");
        int src = sc.nextInt();
        System.out.print("Enter sink vertex       : ");
        int sink = sc.nextInt();

        long startTime = System.nanoTime();                    // Start timer
        int maxFlow = fordFulkerson(src, sink);
        long endTime = System.nanoTime();                      // Stop timer

        System.out.println("\nMaximum Flow from " + src + " to " + sink + ": " + maxFlow);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(V * E^2) with BFS (Edmonds-Karp)");

        sc.close();
    }
}

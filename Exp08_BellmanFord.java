import java.util.Arrays;
import java.util.Scanner;

/**
 * Experiment 8: Single-Source Shortest Path - Bellman-Ford Algorithm
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 * Handles graphs with negative weight edges; detects negative-weight cycles.
 */
public class Exp08_BellmanFord {

    static final int INF = Integer.MAX_VALUE / 2;

    static class Edge {
        int src, dest, weight;
        Edge(int s, int d, int w) { src = s; dest = d; weight = w; }
    }

    static int[] bellmanFord(Edge[] edges, int V, int E, int src, int[] parent) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        Arrays.fill(parent, -1);

        // Relax all edges V-1 times
        for (int i = 1; i <= V - 1; i++) {
            for (Edge e : edges) {
                if (dist[e.src] != INF && dist[e.src] + e.weight < dist[e.dest]) {
                    dist[e.dest]   = dist[e.src] + e.weight;
                    parent[e.dest] = e.src;
                }
            }
        }

        // Check for negative-weight cycles (V-th relaxation)
        for (Edge e : edges) {
            if (dist[e.src] != INF && dist[e.src] + e.weight < dist[e.dest]) {
                System.out.println("\n[WARNING] Graph contains a negative-weight cycle!");
                return null;
            }
        }
        return dist;
    }

    static void printPath(int[] parent, int vertex) {
        if (parent[vertex] == -1) { System.out.print(vertex); return; }
        printPath(parent, parent[vertex]);
        System.out.print(" -> " + vertex);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of vertices: ");
        int V = sc.nextInt();
        System.out.print("Enter number of edges: ");
        int E = sc.nextInt();

        Edge[] edges = new Edge[E];
        System.out.println("Enter edges (source destination weight), 0-indexed:");
        for (int i = 0; i < E; i++) {
            int u = sc.nextInt(), v = sc.nextInt(), w = sc.nextInt();
            edges[i] = new Edge(u, v, w);
        }

        System.out.print("Enter source vertex: ");
        int src = sc.nextInt();

        int[] parent = new int[V];

        long startTime = System.nanoTime();                         // Start timer
        int[] dist = bellmanFord(edges, V, E, src, parent);
        long endTime = System.nanoTime();                           // Stop timer

        if (dist != null) {
            System.out.println("\nShortest distances from vertex " + src + ":");
            System.out.printf("  %-10s %-12s %-20s%n", "Vertex", "Distance", "Path");
            System.out.println("  " + "-".repeat(44));
            for (int v = 0; v < V; v++) {
                System.out.printf("  %-10d ", v);
                if (dist[v] == INF) {
                    System.out.printf("%-12s %-20s%n", "UNREACHABLE", "N/A");
                } else {
                    System.out.printf("%-12d ", dist[v]);
                    printPath(parent, v);
                    System.out.println();
                }
            }
        }

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(V * E)");

        sc.close();
    }
}

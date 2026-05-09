import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Experiment 7: Single-Source Shortest Path - Dijkstra's Algorithm
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 */
public class Exp07_Dijkstra {

    static final int INF = Integer.MAX_VALUE / 2;

    // Adjacency list edge
    static class Edge {
        int to, weight;
        Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    // Priority queue entry
    static class PQNode implements Comparable<PQNode> {
        int vertex, dist;
        PQNode(int v, int d) { vertex = v; dist = d; }

        @Override
        public int compareTo(PQNode other) {
            return Integer.compare(this.dist, other.dist);
        }
    }

    @SuppressWarnings("unchecked")
    static int[] dijkstra(java.util.List<Edge>[] graph, int src, int V, int[] parent) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        Arrays.fill(parent, -1);

        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        pq.offer(new PQNode(src, 0));

        while (!pq.isEmpty()) {
            PQNode curr = pq.poll();
            int u = curr.vertex;

            if (curr.dist > dist[u]) continue;   // stale entry

            for (Edge e : graph[u]) {
                int v = e.to, w = e.weight;
                if (dist[u] + w < dist[v]) {
                    dist[v]   = dist[u] + w;
                    parent[v] = u;
                    pq.offer(new PQNode(v, dist[v]));
                }
            }
        }
        return dist;
    }

    static void printPath(int[] parent, int vertex) {
        if (parent[vertex] == -1) { System.out.print(vertex); return; }
        printPath(parent, parent[vertex]);
        System.out.print(" -> " + vertex);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of vertices: ");
        int V = sc.nextInt();
        System.out.print("Enter number of edges: ");
        int E = sc.nextInt();

        java.util.List<Edge>[] graph = new java.util.ArrayList[V];
        for (int i = 0; i < V; i++) graph[i] = new java.util.ArrayList<>();

        System.out.println("Enter edges (source destination weight), 0-indexed:");
        for (int i = 0; i < E; i++) {
            int u = sc.nextInt(), v = sc.nextInt(), w = sc.nextInt();
            graph[u].add(new Edge(v, w));
            graph[v].add(new Edge(u, w));   // undirected; remove this line for directed
        }

        System.out.print("Enter source vertex: ");
        int src = sc.nextInt();

        int[] parent = new int[V];

        long startTime = System.nanoTime();                        // Start timer
        int[] dist = dijkstra(graph, src, V, parent);
        long endTime = System.nanoTime();                          // Stop timer

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

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O((V + E) log V) with binary heap");

        sc.close();
    }
}

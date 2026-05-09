import java.util.*;

/**
 * Experiment 16: Vertex Cover Problem using Approximation Algorithm
 * Graph has at least 20 randomly generated vertices.
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 *
 * Approximation Strategy (2-approximation):
 *   Repeatedly pick an uncovered edge (u, v), add BOTH u and v to the cover,
 *   then remove all edges incident to u or v. Repeat until no edges remain.
 *   Guaranteed to be at most 2 * OPT (optimal vertex cover size).
 */
public class Exp16_VertexCover {

    static final int  MIN_VERTICES = 20;
    static final Random rng        = new Random(42);   // fixed seed for reproducibility

    // Generate a random undirected graph
    static List<int[]> generateGraph(int V, double edgeProbability) {
        List<int[]> edges = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            for (int v = u + 1; v < V; v++) {
                if (rng.nextDouble() < edgeProbability) {
                    edges.add(new int[]{u, v});
                }
            }
        }
        return edges;
    }

    /**
     * 2-Approximation Algorithm for Vertex Cover.
     * Returns the set of vertices in the approximate cover.
     */
    static Set<Integer> approximateVertexCover(int V, List<int[]> edges) {
        Set<Integer> cover    = new HashSet<>();
        boolean[]    covered  = new boolean[edges.size()];

        System.out.println("\nEdges picked for cover (greedy matching):");
        int round = 0;

        for (int idx = 0; idx < edges.size(); idx++) {
            if (covered[idx]) continue;

            int u = edges.get(idx)[0];
            int v = edges.get(idx)[1];

            // Neither endpoint already in cover → pick this edge
            if (!cover.contains(u) || !cover.contains(v)) {
                cover.add(u);
                cover.add(v);
                round++;
                System.out.printf("  Round %2d: picked edge (%d, %d)  → added vertices %d and %d%n",
                        round, u, v, u, v);

                // Mark all edges incident to u or v as covered
                for (int j = 0; j < edges.size(); j++) {
                    int a = edges.get(j)[0], b = edges.get(j)[1];
                    if (a == u || a == v || b == u || b == v) covered[j] = true;
                }
            }
        }
        return cover;
    }

    // Verify: every edge has at least one endpoint in the cover
    static boolean verify(List<int[]> edges, Set<Integer> cover) {
        for (int[] e : edges)
            if (!cover.contains(e[0]) && !cover.contains(e[1])) return false;
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of vertices (>= " + MIN_VERTICES + "): ");
        int V = sc.nextInt();
        if (V < MIN_VERTICES) {
            System.out.println("Overriding to minimum " + MIN_VERTICES + " vertices.");
            V = MIN_VERTICES;
        }

        System.out.print("Enter edge probability (0.0 - 1.0, e.g. 0.3): ");
        double prob = sc.nextDouble();

        List<int[]> edges = generateGraph(V, prob);

        System.out.println("\n=== Graph Summary ===");
        System.out.println("Vertices    : " + V);
        System.out.println("Edges       : " + edges.size());
        System.out.println("\nEdge List:");
        for (int[] e : edges) System.out.print("(" + e[0] + "," + e[1] + ") ");
        System.out.println();

        long startTime = System.nanoTime();                          // Start timer
        Set<Integer> cover = approximateVertexCover(V, edges);
        long endTime = System.nanoTime();                            // Stop timer

        List<Integer> coverList = new ArrayList<>(cover);
        Collections.sort(coverList);

        System.out.println("\n=== Vertex Cover Result ===");
        System.out.println("Approximate Vertex Cover Size: " + cover.size());
        System.out.println("Vertices in Cover            : " + coverList);
        System.out.println("Cover valid (all edges covered): " + verify(edges, cover));
        System.out.printf("Approximation Ratio          : at most 2x optimal%n");

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(V + E)");
        System.out.println("Approximation Guarantee: |Cover| <= 2 * |OPT|");

        sc.close();
    }
}

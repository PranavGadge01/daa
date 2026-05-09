import java.util.*;

/**
 * Experiment 15: 15-Puzzle Problem using Branch and Bound (A* Search)
 * Running time measured using System.nanoTime() (equivalent to time() in C)
 *
 * Uses Manhattan Distance as the heuristic h(n).
 * f(n) = g(n) + h(n), where g = moves so far, h = Manhattan distance to goal.
 *
 * Input: 4x4 grid with tiles 1-15 and 0 representing the blank.
 * Goal : tiles 1-15 in order, blank at bottom-right.
 */
public class Exp15_FifteenPuzzle {

    static final int N = 4;
    // Goal state
    static final int[] GOAL = { 1,2,3,4, 5,6,7,8, 9,10,11,12, 13,14,15,0 };

    // Directions: up, down, left, right (row delta, col delta)
    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = { 0, 0,-1, 1};
    static final String[] DIR_NAME = {"UP","DOWN","LEFT","RIGHT"};

    static class State implements Comparable<State> {
        int[]    tiles;     // flat array of 16
        int      blankPos;  // index of blank (0)
        int      g;         // cost so far (number of moves)
        int      h;         // Manhattan distance heuristic
        String   path;      // moves taken

        State(int[] tiles, int blankPos, int g, String path) {
            this.tiles    = tiles.clone();
            this.blankPos = blankPos;
            this.g        = g;
            this.h        = manhattan(tiles);
            this.path     = path;
        }

        int f() { return g + h; }

        @Override
        public int compareTo(State other) { return Integer.compare(this.f(), other.f()); }

        String key() { return Arrays.toString(tiles); }
    }

    // Manhattan distance of all tiles from their goal position
    static int manhattan(int[] tiles) {
        int dist = 0;
        for (int i = 0; i < 16; i++) {
            int tile = tiles[i];
            if (tile == 0) continue;
            int goalIdx = tile - 1;              // goal index for tile t is t-1
            dist += Math.abs(i / N - goalIdx / N) + Math.abs(i % N - goalIdx % N);
        }
        return dist;
    }

    static boolean isGoal(int[] tiles) {
        return Arrays.equals(tiles, GOAL);
    }

    // Check if a puzzle is solvable (based on inversion count parity)
    static boolean isSolvable(int[] tiles) {
        int inversions = 0;
        int blankRow   = 0;
        for (int i = 0; i < 16; i++) {
            if (tiles[i] == 0) { blankRow = i / N; continue; }
            for (int j = i + 1; j < 16; j++) {
                if (tiles[j] != 0 && tiles[i] > tiles[j]) inversions++;
            }
        }
        // For 4x4: solvable if (blank on even row from bottom && inversions odd)
        //                     OR (blank on odd  row from bottom && inversions even)
        int blankRowFromBottom = N - 1 - blankRow;
        return (blankRowFromBottom % 2 == 0) == (inversions % 2 != 0);
    }

    static String solve(int[] initial) {
        int blankIdx = 0;
        for (int i = 0; i < 16; i++) if (initial[i] == 0) { blankIdx = i; break; }

        PriorityQueue<State> pq = new PriorityQueue<>();
        Map<String, Integer> visited = new HashMap<>();

        State start = new State(initial, blankIdx, 0, "");
        pq.offer(start);

        int nodesExpanded = 0;

        while (!pq.isEmpty()) {
            State curr = pq.poll();
            nodesExpanded++;

            if (isGoal(curr.tiles)) {
                System.out.println("Nodes expanded: " + nodesExpanded);
                System.out.println("Minimum moves : " + curr.g);
                return curr.path;
            }

            String key = curr.key();
            if (visited.containsKey(key) && visited.get(key) <= curr.g) continue;
            visited.put(key, curr.g);

            int br = curr.blankPos / N;
            int bc = curr.blankPos % N;

            for (int d = 0; d < 4; d++) {
                int nr = br + DR[d];
                int nc = bc + DC[d];
                if (nr < 0 || nr >= N || nc < 0 || nc >= N) continue;

                int newBlank = nr * N + nc;
                int[] newTiles = curr.tiles.clone();
                newTiles[curr.blankPos] = newTiles[newBlank];
                newTiles[newBlank]      = 0;

                State next = new State(newTiles, newBlank, curr.g + 1,
                        curr.path + (curr.path.isEmpty() ? "" : " -> ") + DIR_NAME[d]);
                String nKey = next.key();
                if (!visited.containsKey(nKey) || visited.get(nKey) > next.g)
                    pq.offer(next);
            }
        }
        return "UNSOLVABLE";
    }

    static void printGrid(int[] tiles) {
        for (int i = 0; i < 16; i++) {
            if (tiles[i] == 0) System.out.printf("%4s", " _");
            else               System.out.printf("%4d", tiles[i]);
            if ((i + 1) % N == 0) System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] initial = new int[16];

        System.out.println("Enter 16 numbers (0 = blank, tiles 1-15) row by row:");
        for (int i = 0; i < 16; i++) initial[i] = sc.nextInt();

        System.out.println("\nInitial State:");
        printGrid(initial);

        System.out.println("\nGoal State:");
        printGrid(GOAL);

        if (!isSolvable(initial)) {
            System.out.println("\nThis puzzle configuration is NOT SOLVABLE.");
            sc.close();
            return;
        }

        System.out.println("\nSolving using Branch & Bound (A* with Manhattan Distance)...\n");

        long startTime = System.nanoTime();     // Start timer
        String solution = solve(initial);
        long endTime = System.nanoTime();       // Stop timer

        System.out.println("\nSolution moves: " + solution);

        long elapsed = endTime - startTime;
        System.out.println("\n--- Running Time ---");
        System.out.println("Time (nanoseconds) : " + elapsed + " ns");
        System.out.printf("Time (milliseconds): %.4f ms%n", elapsed / 1_000_000.0);
        System.out.printf("Time (seconds)     : %.6f s%n",  elapsed / 1_000_000_000.0);
        System.out.println("\nTheoretical Complexity: O(b^d) — b = branching factor, d = solution depth");
        System.out.println("A* is optimal and complete with an admissible heuristic.");

        sc.close();
    }
}

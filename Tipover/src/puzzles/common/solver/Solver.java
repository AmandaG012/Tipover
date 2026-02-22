package puzzles.common.solver;

import java.util.*;

/**
 * Simple solver class that uses BFS to find a path
 * from a starting configuration to a solution configuration.
 *
 *
 * Total and unique configs generated
 *
 * @author Amanda Guan
 */
public class Solver {
    private long totalConfigsGenerated;
    private long uniqueConfigsGenerated;

    /**
     * Creates a solver with counters reset.
     */
    public Solver() {
        totalConfigsGenerated = 0;
        uniqueConfigsGenerated = 0;
    }

    /**
     * Uses BFS to find a path from the start configuration
     * to a configuration where isSolution() is true.
     *
     * @param start the starting configuration
     * @return the path as a list if found, otherwise null
     */
    public List<Configuration> solve(Configuration start) {
        totalConfigsGenerated = 0;
        uniqueConfigsGenerated = 0;

        // BFS queue
        LinkedList<Configuration> queue = new LinkedList<>();

        // Map each config to its predecessor
        HashMap<Configuration, Configuration> pred = new HashMap<>();

        queue.add(start);
        pred.put(start, null);
        uniqueConfigsGenerated = 1;

        while (!queue.isEmpty()) {
            Configuration current = queue.removeFirst();

            // Check for solution
            if (current.isSolution()) {
                LinkedList<Configuration> path = new LinkedList<>();

                // Walk backward using predecessors to build the path
                Configuration node = current;
                while (node != null) {
                    path.addFirst(node);
                    node = pred.get(node);
                }
                return path;
            }

            // Explore neighbors
            Collection<Configuration> neighbors = current.getNeighbors();
            for (Configuration nbr : neighbors) {
                totalConfigsGenerated++;

                // Only add unseen configurations
                if (!pred.containsKey(nbr)) {
                    pred.put(nbr, current);
                    uniqueConfigsGenerated++;
                    queue.add(nbr);
                }
            }
        }
        // No solution found
        return null;
    }

    /**
     * @return all generated configs (including duplicates)
     */
    public long getTotalConfigsGenerated() {
        return totalConfigsGenerated;
    }

    /**
     * @return number of unique configs generated
     */
    public long getUniqueConfigsGenerated() {
        return uniqueConfigsGenerated;
    }
}
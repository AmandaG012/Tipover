package puzzles.buckets;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

/**
 * Main class for the buckets puzzle.
 *
 * @author Amanda Guan
 */
public class Buckets {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        // Need at least target and one bucket
        if (args.length < 2) {
            System.out.println("Usage: java Buckets amount bucket1 bucket2 ...");
            return;
        }

        // Read target amount
        int target = Integer.parseInt(args[0]);

        // Number of buckets
        int n = args.length - 1;

        // Store bucket capacities
        int[] caps = new int[n];
        for (int i = 0; i < n; i++) {
            caps[i] = Integer.parseInt(args[i + 1]);
        }

        // All buckets start with 0 water
        int[] startAmounts = new int[n];
        for (int i = 0; i < n; i++){
            startAmounts[i] = 0;
        }

        // Create the starting configuration
        BucketsConfig start = new BucketsConfig(caps, startAmounts, target);

        // Create solver and try to solve
        Solver solver = new Solver();
        List<Configuration> path = solver.solve(start);

        // Print info
        System.out.println("Amount: " + target + ", Buckets: " + BucketsConfig.capsToString(caps));
        System.out.println("Total configs: " + solver.getTotalConfigsGenerated());
        System.out.println("Unique configs: " + solver.getUniqueConfigsGenerated());

        // Print solution path
        if (path == null) {
            System.out.println("No solution.");
        }
        else {
            // Print each step
            for (int i = 0; i < path.size(); i++) {
                System.out.println("Step " + i + ": " + path.get(i).toString());
            }
        }
    }
}

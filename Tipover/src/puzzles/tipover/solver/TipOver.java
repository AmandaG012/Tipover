package puzzles.tipover.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.tipover.model.TipOverConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Command-line solver for the Tip Over puzzle.
 * Loads a puzzle file and prints the solution steps.
 *
 * @Author Amanda Guan
 */
public class TipOver {
    /**
     * Program entry point.
     * Solves the Tip Over puzzle given a file name.
     */
    public static void main(String[] args) {
        // Check command line arguments
        if (args.length != 1) {
            System.err.println("Usage: java TipOver filename");
            return;
        }
        String filename = args[0];
        File file = new File(filename);

        // Check that the file exists
        if (!file.exists()) {
            System.err.println("Error: File not found: " + filename);
            return;
        }
        TipOverConfig startConfig;

        // Load the initial configuration
        try {
            startConfig = new TipOverConfig(filename);
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        System.out.println("File: " + filename);
        System.out.print(startConfig);

        // Solve the puzzle
        Solver solver = new Solver();
        List<Configuration> path = solver.solve(startConfig);

        // Print solver statistics
        System.out.println("Total configs: " + solver.getTotalConfigsGenerated());
        System.out.println("Unique configs: " + solver.getUniqueConfigsGenerated());

        // Print solution path
        if (path != null) {
            System.out.println("Step 0:");
            System.out.print(path.get(0));

            for (int i = 1; i < path.size(); i++) {
                System.out.println();
                System.out.println("Step " + i + ":");
                System.out.print(path.get(i));
            }
        }
        else {
            System.out.println("No solution");
        }
    }
}
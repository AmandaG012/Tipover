package puzzles.watch;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;

/**
 * Main class for the watch puzzle.
 *
 * @author Amanda Guan
 */
public class Watch {
    /**
     * Run an instance of the watch puzzle.
     *
     * @param args [0]: the number of hours in the watch;
     *             [1]: the starting hour;
     *             [2]: the starting minute;
     *             [3]: the ending hour;
     *             [4]: the ending minute;
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: java Watch hours start-hour start-minute end-hour end-minute");
            return;
        }

        int hours = Integer.parseInt(args[0]);
        int sh = Integer.parseInt(args[1]);
        int sm = Integer.parseInt(args[2]);
        int eh = Integer.parseInt(args[3]);
        int em = Integer.parseInt(args[4]);

        // Create start configuration
        WatchConfig start = new WatchConfig(hours, sh, sm, eh, em);

        // Solve the puzzle
        Solver solver = new Solver();
        List<Configuration> path = solver.solve(start);

        System.out.println("Hours: " + hours + ", Start: " + start.toString()
                + ", End: " + (new WatchConfig(hours, eh, em, eh, em)).toString());
        System.out.println("Total configs: " + solver.getTotalConfigsGenerated());
        System.out.println("Unique configs: " + solver.getUniqueConfigsGenerated());

        // Print solution path
        if (path == null) {
            System.out.println("No solution.");
        }
        else {
            for (int i = 0; i < path.size(); i++) {
                System.out.println("Step " + i + ": " + path.get(i).toString());
            }
        }
    }
}
package puzzles.tipover.model;

import puzzles.common.Direction;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for the Tip Over puzzle.
 * Manages the current game state and notifies observers of changes.
 *
 * @Author Amanda Guan
 */
public class TipOverModel {
    /** List of observers watching this model */
    private final List<Observer<TipOverModel, String>> observers = new LinkedList<>();

    /** The current configuration of the puzzle */
    private TipOverConfig currentConfig;

    /** The initial configuration (used for reset) */
    private TipOverConfig initialConfig;

    /**
     * Returns the current configuration.
     */
    public TipOverConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Adds an observer to the model.
     */
    public void addObserver(Observer<TipOverModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * Notifies all observers with a message.
     */
    public void notifyObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Creates the model and loads the puzzle from a file.
     *
     * @param filename puzzle file to load
     * @throws IOException if the file cannot be read
     */
    public TipOverModel(String filename) throws IOException {
        this.currentConfig = new TipOverConfig(filename);
        this.initialConfig = new TipOverConfig(filename);
        notifyObservers("Loaded: " + filename);
    }

    /**
     * Attempts to move the tipper in the given direction.
     *
     * @param dirChar direction character (N, S, E, or W)
     */
    public void move(char dirChar) {

        // Stop if puzzle is already solved
        if (currentConfig.isSolution()) {
            notifyObservers("Current board is already solved.");
            return;
        }

        Direction dir;

        // Convert character to direction name
        String dirName = switch (Character.toUpperCase(dirChar)) {
            case 'N' -> "NORTH";
            case 'S' -> "SOUTH";
            case 'E' -> "EAST";
            case 'W' -> "WEST";
            default -> null;
        };

        if (dirName == null) {
            notifyObservers("Invalid direction character.");
            return;
        }
        try {
            dir = Direction.valueOf(dirName);
        }
        catch (IllegalArgumentException e) {
            notifyObservers("Invalid direction character.");
            return;
        }
        TipOverConfig nextConfig = currentConfig.getMoveInDirection(dir);

        // Valid move
        if (nextConfig != null) {
            // Check if a tower was tipped
            boolean wasTip =
                    currentConfig.getCrateHeight(
                            currentConfig.tipperRow,
                            currentConfig.tipperCol) > 1 &&
                            nextConfig.getCrateHeight(
                                    currentConfig.tipperRow,
                                    currentConfig.tipperCol) == 0;

            this.currentConfig = nextConfig;
            String msg;

            if (currentConfig.isSolution()) {
                msg = "I WON!";
            }
            else if (wasTip) {
                msg = "A tower has been tipped over.";
            }
            else {
                msg = "";
            }
            notifyObservers(msg);
        }

        // Invalid move
        else {
            int[] delta = currentConfig.getDelta(dir);
            int dr = delta[0];
            int dc = delta[1];

            int nextR = currentConfig.tipperRow + dr;
            int nextC = currentConfig.tipperCol + dc;

            if (!currentConfig.inBounds(nextR, nextC)) {
                notifyObservers("Move goes off the board.");
            }
            else if (currentConfig.getCrateHeight(nextR, nextC) == 0) {
                notifyObservers("No crate or tower there.");
            }
            else {
                notifyObservers("Tower cannot be tipped over.");
            }
        }
    }

    /**
     * Resets the puzzle to its initial state.
     */
    public void reset() {
        try {
            this.currentConfig = new TipOverConfig(initialConfig.filename);
        }
        catch (IOException e) {
            notifyObservers("Error resetting board: Could not reload initial file.");
            return;
        }
        notifyObservers("Puzzle reset!");
    }

    /**
     * Loads a new puzzle file.
     */
    public void load(String filename) throws IOException {
        TipOverConfig newConfig = new TipOverConfig(filename);
        this.currentConfig = newConfig;
        this.initialConfig = new TipOverConfig(filename);
        notifyObservers("Loaded: " + filename);
    }

    /**
     * Uses the solver to make one step toward a solution.
     */
    public void hint() {
        if (currentConfig.isSolution()) {
            notifyObservers("Current board is already solved.");
            return;
        }
        Solver solver = new Solver();
        List<Configuration> path = solver.solve(currentConfig);

        if (path != null && path.size() > 1) {
            this.currentConfig = (TipOverConfig) path.get(1);
            String msg;
            if (currentConfig.isSolution()) {
                msg = "I WON!";
            }
            else {
                msg = "Next step!";
            }
            notifyObservers(msg);
        }
        else {
            notifyObservers("No solution found from current position.");
        }
    }
}
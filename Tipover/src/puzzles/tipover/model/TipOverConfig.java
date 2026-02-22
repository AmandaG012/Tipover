package puzzles.tipover.model;

import puzzles.common.Direction;
import puzzles.common.solver.Configuration;

import java.io.*;
import java.util.*;

/**
 * Represents a single configuration of the Tip Over puzzle.
 * Stores the board, tipper position, and goal position.
 *
 * @Author Amanda Guan
 */
public class TipOverConfig implements Configuration {
    /** The game board containing crate heights */
    private final int[][] board;

    /** Number of rows and columns */
    private final int rows, cols;

    /** Current position of the tipper */
    public final int tipperRow, tipperCol;

    /** Goal position */
    private final int goalRow, goalCol;

    /** Name of the puzzle file */
    public final String filename;

    /**
     * Creates an initial configuration by reading a puzzle file.
     *
     * @param filename name of the puzzle file
     * @throws IOException if the file cannot be read or is invalid
     */
    public TipOverConfig(String filename) throws IOException {
        this.filename = filename;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IOException("File is empty.");
            }

            Scanner scanner = new Scanner(firstLine);
            rows = scanner.nextInt();
            cols = scanner.nextInt();
            tipperRow = scanner.nextInt();
            tipperCol = scanner.nextInt();
            goalRow = scanner.nextInt();
            goalCol = scanner.nextInt();

            board = new int[rows][cols];

            // Read the board values
            for (int r = 0; r < rows; r++) {
                String line = br.readLine();
                if (line == null){
                    break;
                }
                Scanner rowScanner = new Scanner(line);
                for (int c = 0; c < cols; c++) {
                    if (rowScanner.hasNextInt()) {
                        board[r][c] = rowScanner.nextInt();
                    } else {
                        throw new IOException("Missing integer at row " + r + ", col " + c);
                    }
                }
            }
        }
    }

    /**
     * Private constructor used to create a new configuration
     * from an existing one after a move.
     */
    private TipOverConfig(TipOverConfig oldConfig, int[][] newBoard, int newTipperRow, int newTipperCol) {
        this.rows = oldConfig.rows;
        this.cols = oldConfig.cols;
        this.goalRow = oldConfig.goalRow;
        this.goalCol = oldConfig.goalCol;
        this.filename = oldConfig.filename;

        // Copy the board so configurations stay independent
        this.board = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(newBoard[r], 0, this.board[r], 0, cols);
        }
        this.tipperRow = newTipperRow;
        this.tipperCol = newTipperCol;
    }

    /**
     * Checks if a position is inside the board.
     */
    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Returns the row and column change for a direction.
     */
    public int[] getDelta(Direction dir) {
        return switch (dir) {
            case NORTH -> new int[]{-1, 0};
            case SOUTH -> new int[]{1, 0};
            case EAST -> new int[]{0, 1};
            case WEST -> new int[]{0, -1};
        };
    }

    /**
     * Gets the height of the crate at a position.
     * Returns 0 if out of bounds.
     */
    public int getCrateHeight(int r, int c) {
        if (inBounds(r, c)) {
            return board[r][c];
        }
        return 0;
    }

    /**
     * Checks if the tipper is on the goal.
     */
    @Override
    public boolean isSolution() {
        return tipperRow == goalRow && tipperCol == goalCol;
    }

    /**
     * Generates all valid neighboring configurations.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            TipOverConfig neighbor = getMoveInDirection(dir);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Attempts to move or tip in the given direction.
     * Returns a new configuration if valid, otherwise null.
     */
    public TipOverConfig getMoveInDirection(Direction dir) {
        int[] delta = getDelta(dir);
        int dr = delta[0];
        int dc = delta[1];

        int nextR = tipperRow + dr;
        int nextC = tipperCol + dc;

        // Simple move onto a standing crate
        if (inBounds(nextR, nextC) && board[nextR][nextC] > 0) {
            return new TipOverConfig(this, board, nextR, nextC);
        }

        int currentCrateHeight = board[tipperRow][tipperCol];

        // Try tipping the crate
        if (currentCrateHeight > 1) {
            boolean canTip = true;

            // Check if space is clear
            for (int h = 1; h <= currentCrateHeight; h++) {
                int fallR = tipperRow + dr * h;
                int fallC = tipperCol + dc * h;

                if (!inBounds(fallR, fallC) || board[fallR][fallC] != 0) {
                    canTip = false;
                    break;
                }
            }

            if (canTip) {
                int[][] newBoard = new int[rows][cols];
                for (int r = 0; r < rows; r++) {
                    System.arraycopy(board[r], 0, newBoard[r], 0, cols);
                }

                // Remove standing crate
                newBoard[tipperRow][tipperCol] = 0;

                // Lay crate flat
                for (int h = 1; h <= currentCrateHeight; h++) {
                    int fallR = tipperRow + dr * h;
                    int fallC = tipperCol + dc * h;
                    newBoard[fallR][fallC] = 1;
                }

                return new TipOverConfig(
                        this,
                        newBoard,
                        tipperRow + dr,
                        tipperCol + dc
                );
            }
        }
        return null;
    }

    /**
     * Checks if two configurations are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TipOverConfig o)){
            return false;
        }
        if (tipperRow != o.tipperRow || tipperCol != o.tipperCol){
            return false;
        }

        for (int r = 0; r < rows; r++) {
            if (!Arrays.equals(board[r], o.board[r])){
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a hash code for this configuration.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(tipperRow, tipperCol);
        for (int[] row : board) {
            result = 31 * result + Arrays.hashCode(row);
        }
        return result;
    }

    /**
     * Returns a formatted string of the board.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("      ");

        for (int c = 0; c < cols; c++) {
            sb.append(String.format("%2d ", c));
        }
        sb.append('\n');
        sb.append("    ");

        for (int c = 0; c < cols; c++) {
            sb.append("___");
        }
        sb.append('\n');

        for (int r = 0; r < rows; r++) {
            sb.append(String.format(" %2d |", r));
            for (int c = 0; c < cols; c++) {
                String content;

                if (r == tipperRow && c == tipperCol) {
                    content = "*" + (board[r][c] == 0 ? "_" : board[r][c]);
                }
                else if (r == goalRow && c == goalCol) {
                    content = "!" + (board[r][c] == 0 ? "_" : board[r][c]);
                }
                else if (board[r][c] == 0) {
                    content = " _";
                }
                else {
                    content = String.format("%2d", board[r][c]);
                }
                sb.append(content).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Returns number of rows */
    public int getRows() {
        return rows;
    }

    /** Returns number of columns */
    public int getCols() {
        return cols;
    }

    /** Checks if a position is the goal */
    public boolean isGoal(int r, int c) {
        return r == goalRow && c == goalCol;
    }

    /** Checks if a position is the tipper */
    public boolean isTipper(int r, int c) {
        return r == tipperRow && c == tipperCol;
    }
}
package puzzles.tipover.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.tipover.model.TipOverModel;

import java.io.File;
import java.io.IOException;

/**
 * GUI for the Tip Over puzzle.
 * Displays the game board, status messages, and control buttons.
 * Observes the TipOverModel and updates the display when the model changes.
 *
 * @Author Amanda Guan
 */
public class TipOverGUI extends Application implements puzzles.common.Observer<TipOverModel, String> {
    /** Color used for the goal */
    private final static String RED = "#DF0101";

    /** Color used for the tipper */
    private final static String PINK = "#FFC0CB";

    /** Style string for arrow buttons */
    private final static String ARROW_BUTTON_STYLE = "-fx-font-size: 14; -fx-padding: 3 6;";

    /** Font size for board cell text */
    private final static int BUTTON_FONT_SIZE = 20;

    /** Size (width and height) of each board cell */
    private final static int ICON_SIZE = 45;

    /** Vertical spacing between control elements */
    private final static int CONTROL_SPACING = 5;

    /** The game model that holds the puzzle state */
    private TipOverModel model;

    /** Label showing status messages from the model */
    private Label statusLabel;

    /** Container that holds the current board grid */
    private VBox boardContainer;

    /** Name of the initially loaded puzzle file */
    private String initialFilename;

    /** Primary stage reference (used to resize after updates) */
    private Stage mainStage;

    /**
     * Initializes the GUI before the window is shown.
     * Loads the initial puzzle file and creates the TipOver model.
     *
     * @throws Exception if the puzzle file cannot be loaded
     */
    @Override
    public void init() throws Exception {
        // Check for a filename passed on the command line
        if (!getParameters().getRaw().isEmpty()) {
            initialFilename = getParameters().getRaw().get(0);
        } else {
            // Default puzzle file if none is provided
            initialFilename = "data/tipover/tipover-4.txt";
        }

        try {
            // Create the model using the initial puzzle file
            this.model = new TipOverModel(initialFilename);
        } catch (IOException e) {
            // Report loading errors and rethrow
            System.err.println("Error loading initial file: " + e.getMessage());
            throw e;
        }

        // Register this GUI as an observer of the model
        this.model.addObserver(this);
    }

    /**
     * Sets up and displays the main application window.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the GUI cannot be started
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Save the stage reference for later resizing
        this.mainStage = stage;

        // Root layout for the entire window
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Status label initially shows the loaded file name
        statusLabel = new Label("Loaded: " +
                new File(initialFilename).getName());
        statusLabel.setStyle("-fx-font-weight: bold;");

        // Main content holds the board and the controls side-by-side
        HBox mainContent = new HBox(10);
        mainContent.setAlignment(Pos.CENTER);

        // Container for the board so it can be replaced on updates
        boardContainer = new VBox();
        boardContainer.getChildren().add(createBoardGrid());

        // Create the control panel (buttons)
        VBox controls = createControls(stage);

        // Assemble the layout
        mainContent.getChildren().addAll(boardContainer, controls);
        root.getChildren().addAll(statusLabel, mainContent);

        // Create and show the scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Tip Over");
        stage.show();
    }

    /**
     * Creates the visual grid representing the current game board.
     * Each cell shows the height of a crate and highlights special cells.
     *
     * @return a GridPane containing the game board
     */
    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.CENTER);

        // Get the current configuration from the model
        puzzles.tipover.model.TipOverConfig current =
                model.getCurrentConfig();

        int rows = current.getRows();
        int cols = current.getCols();

        // Loop through each row and column to build the grid
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Height of the crate at this position
                int height = current.getCrateHeight(r, c);

                // Label used to display the crate height
                Label cell = new Label(String.valueOf(height));
                cell.setMinSize(ICON_SIZE, ICON_SIZE);
                cell.setMaxSize(ICON_SIZE, ICON_SIZE);
                cell.setAlignment(Pos.CENTER);

                // Base style for all cells
                String style = "-fx-font-size: " + BUTTON_FONT_SIZE +
                        "; -fx-font-weight: bold;" +
                        "-fx-background-color: white;";

                // Highlight the goal cell in red
                if (current.isGoal(r, c)) {
                    style += "-fx-background-color: " + RED + ";";
                }

                // Highlight the tipper (player) cell in pink
                if (current.isTipper(r, c)) {
                    style += "-fx-background-color: " + PINK + ";";
                }

                // Apply the final style and add the cell to the grid
                cell.setStyle(style);
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    /**
     * Creates the control panel with movement buttons and game actions.
     *
     * @param stage the main application stage
     * @return a VBox containing the controls
     */
    private VBox createControls(Stage stage) {
        VBox controls = new VBox(CONTROL_SPACING);
        controls.setPadding(new Insets(5, 0, 0, 15));
        controls.setAlignment(Pos.TOP_CENTER);

        // Directional pad laid out like arrow keys
        GridPane directionalPad = new GridPane();
        directionalPad.setHgap(2);
        directionalPad.setVgap(2);

        // Arrow buttons for movement
        Button up = new Button("⇧");
        Button down = new Button("⇩");
        Button left = new Button("⇦");
        Button right = new Button("⇨");

        // Apply consistent styling to arrow buttons
        up.setStyle(ARROW_BUTTON_STYLE);
        down.setStyle(ARROW_BUTTON_STYLE);
        left.setStyle(ARROW_BUTTON_STYLE);
        right.setStyle(ARROW_BUTTON_STYLE);

        // Connect arrow buttons to model movement commands
        up.setOnAction(e -> model.move('N'));
        down.setOnAction(e -> model.move('S'));
        left.setOnAction(e -> model.move('W'));
        right.setOnAction(e -> model.move('E'));

        // Arrange arrow buttons in a directional layout
        directionalPad.add(up, 1, 0);
        directionalPad.add(down, 1, 2);
        directionalPad.add(left, 0, 1);
        directionalPad.add(right, 2, 1);

        // Button to load a new puzzle file
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> handleLoad(stage));

        // Button to reset the puzzle to its initial state
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> model.reset());

        // Button to request a hint from the model
        Button hintButton = new Button("Hint");
        hintButton.setOnAction(e -> model.hint());

        // Add all controls to the control panel
        controls.getChildren().addAll(
                directionalPad, loadButton, resetButton, hintButton
        );
        return controls;
    }

    /**
     * Handles loading a new puzzle file selected by the user.
     *
     * @param stage the main application stage
     */
    private void handleLoad(Stage stage) {
        // File chooser dialog for selecting a puzzle file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Tip Over Puzzle File");

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                // Ask the model to load the selected file
                model.load(file.getAbsolutePath());
            }
            catch (IOException ex) {
                // Notify observers if loading fails
                model.notifyObservers(
                        "Error loading file: " + ex.getMessage());
            }
        }
        else {
            // User cancelled the load dialog
            model.notifyObservers("Load cancelled.");
        }
    }

    /**
     * Updates the GUI when the model changes.
     * Refreshes the board and updates the status label.
     *
     * @param tipOverModel the updated model
     * @param msg a message from the model
     */
    @Override
    public void update(TipOverModel tipOverModel, String msg) {
        // Update the status message
        statusLabel.setText(msg);

        // Rebuild the board to reflect the new configuration
        boardContainer.getChildren().clear();
        boardContainer.getChildren().add(createBoardGrid());

        // Resize the window to fit updated content
        if (mainStage != null) {
            mainStage.sizeToScene();
        }
    }

    /**
     * Launches the Tip Over GUI application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Expect exactly one argument
        if (args.length != 1) {
            System.out.println("Usage: java TipOverGUI filename");
        }
        else {
            Application.launch(args);
        }
    }
}
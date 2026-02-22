package puzzles.tipover.ptui;

import puzzles.common.Observer;
import puzzles.tipover.model.TipOverModel;

import java.io.IOException;
import java.util.Scanner;

/**
 * A plain text user interface (PTUI) for the Tip Over puzzle.
 * Reads user commands and displays puzzle updates.
 *
 * @Author Amanda Guan
 */
public class TipOverPTUI implements Observer<TipOverModel, String> {
    /** The game model */
    private TipOverModel model;

    /**
     * Initializes the PTUI and loads the puzzle file.
     *
     * @param filename puzzle file name
     * @throws IOException if the file cannot be loaded
     */
    public void init(String filename) throws IOException {
        this.model = new TipOverModel(filename);
        this.model.addObserver(this);
    }

    /**
     * Receives updates from the model.
     * Prints messages and the current board.
     */
    @Override
    public void update(TipOverModel model, String data) {
        System.out.println(data);

        if (data.startsWith("Failed to load:")
                || data.startsWith("Error loading initial file:")) {
        }
        else {
            System.out.println(model.getCurrentConfig().toString());
        }
    }

    /**
     * Displays available user commands.
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "m(ove) {N|S|E|W}    -- move the tipper in the given direction" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * Runs the main command loop.
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        displayHelp();

        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );

            if (words.length > 0) {
                String cmd = words[0].toLowerCase();

                if (cmd.startsWith("q")) {
                    break;
                }
                else if (cmd.startsWith("h")) {
                    model.hint();
                }
                else if (cmd.startsWith("r")) {
                    model.reset();
                }
                else if (cmd.startsWith("m")) {
                    if (words.length > 1 && words[1].length() == 1) {
                        model.move(words[1].toUpperCase().charAt(0));
                    }
                    else {
                        System.out.println("Invalid move command. Use m {N|S|E|W}");
                    }
                }
                else if (cmd.startsWith("l")) {
                    if (words.length > 1) {
                        try {
                            model.load(words[1]);
                        }
                        catch (IOException ioe) {
                            model.notifyObservers("Failed to load: " + words[1]);
                        }
                    }
                    else {
                        System.out.println("Invalid load command. Use l filename");
                    }
                }
                else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * Program entry point.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TipOverPTUI filename");
        }
        else {
            try {
                TipOverPTUI ptui = new TipOverPTUI();
                ptui.init(args[0]);
                ptui.run();
            }
            catch (IOException ioe) {
                System.out.println("Error loading initial file: " + ioe.getMessage());
            }
        }
    }
}
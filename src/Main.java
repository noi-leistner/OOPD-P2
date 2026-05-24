import Presentation.views.MainWindow;

import javax.swing.*;

/**
 * The main entry point that kicks off the whole app.
 */
public class  Main {

    /**
     * Starts the application.
     * We wrap the window initialization in invokeLater to make sure
     * Swing handles the GUI setup safely on its own event thread.
     *
     * @param args command-line arguments (not used).
     */
    static void main(String[] args) {SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));}
}
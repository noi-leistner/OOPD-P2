package Presentation.views;

import Presentation.controllers.AuthController;

import javax.swing.*;
import java.awt.*;

/**
 * A switcher panel that acts as a parent container for authentication views.
 * It holds a dedicated content area that easily swaps back and forth between
 * the LoginForm and RegisterForm panels without disrupting the main layout.
 */
public class AuthPanel extends JPanel {
    /** The dedicated container layout used for swapping out the forms. */
    private final JPanel contentArea;

    /**
     * Prepares the main layout shell, initializes the immutable content zone,
     * and sets up the login view as the default starting screen.
     */
    public AuthPanel(MainWindow app, AuthController authController) {
        setLayout(new BorderLayout());

        contentArea = new JPanel(new BorderLayout());
        add(contentArea, BorderLayout.CENTER);

        showLogin(app, authController);
    }

    /**
     * Clears out whatever form is currently on screen and drops in a fresh
     * login form instance, forcing the panel to redraw its layout elements.
     */
    public void showLogin(MainWindow app, AuthController authController) {
        contentArea.removeAll();
        contentArea.add(new LoginForm(app, authController, this));
        contentArea.revalidate();
        contentArea.repaint();
   }

    /**
     * Clears out whatever form is currently on screen and drops in a fresh
     * registration form instance, forcing the panel to redraw its layout elements.
     */
    public void showRegister(MainWindow app, AuthController authController) {
        contentArea.removeAll();
        contentArea.add(new RegisterForm(app, authController, this));
        contentArea.revalidate();
        contentArea.repaint();
    }
}

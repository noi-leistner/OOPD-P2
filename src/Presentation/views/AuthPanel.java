package Presentation.views;

import Presentation.controllers.AuthController;

import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private JPanel contentArea;

    public AuthPanel(MainWindow app, AuthController authController) {
        setLayout(new BorderLayout());

        contentArea = new JPanel(new BorderLayout());
        add(contentArea, BorderLayout.CENTER);

        showLogin(app, authController);
    }

    public void showLogin(MainWindow app, AuthController authController) {
        contentArea.removeAll();
        contentArea.add(new LoginForm(app, authController, this));
        contentArea.revalidate();
        contentArea.repaint();
   }

    public void showRegister(MainWindow app, AuthController authController) {
        contentArea.removeAll();
        contentArea.add(new RegisterForm(app, authController, this));
        contentArea.revalidate();
        contentArea.repaint();
    }
}

package Presentation;

import Business.AuthManager;
import Business.Entities.User;

import javax.swing.*;

public class AuthController {

    private AuthManager authManager;
    private MainWindow app;

    public boolean logIn(String email, String password) {
        User user = authManager.login(email, password);
        if (user != null) {
            app.switchTo(MainWindow.DASHBOARD_SCREEN);
        } else {
            JOptionPane.showMessageDialog(app,
                    "Incorrect username or password",
                    "Login failed",
                    JOptionPane.ERROR_MESSAGE);
        }
        return true; //Change this logic later
    }

    boolean register(String name, String surname, String email, String password) {
        //TODO: Implement
        return true;
    }

    void logOut() {
        //TODO: Implement
    }

    void deleteAccount(){
        //TODO: Implement
    }

    public AuthController(MainWindow mainWindow, AuthManager authManager, AuthPanel authPanel) {
        this.app = mainWindow;
        this.authManager = authManager;
    }

}

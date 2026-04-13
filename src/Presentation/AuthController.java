package Presentation;

import Business.AuthManager;
import Business.AuthResult;
import Business.Entities.User;
import Business.SessionManager;

import javax.swing.*;

public class AuthController {

    private AuthManager authManager;
    private SessionManager sessionManager;

    public AuthController(AuthManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    void logIn(String user_name, String email, String password) {
        //TODO: Implement
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;

        AuthResult result = authManager.signUp(user);
        if (result == AuthResult.SUCCESS) sessionManager.login(user);
        return result;
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

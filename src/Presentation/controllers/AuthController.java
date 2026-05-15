package Presentation.controllers;

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

    public AuthResult logIn(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {return AuthResult.EMPTY_FIELDS;}

        User user = authManager.login(email, password);

        if (user != null) {
            SessionManager.getInstance().login(user);
            return AuthResult.SUCCESS;
        }

        return AuthResult.INVALID_CREDENTIALS;
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;

        AuthResult result = authManager.signUp(user);
        if (result == AuthResult.SUCCESS) sessionManager.login(user);
        return result;
    }

    // TODO: Delete everything!!!
    public void logOut() {
        System.out.println("\nUser logged out.");
        sessionManager.logout();
    }

    public boolean deleteAccount(){
        Business.Entities.User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        boolean deleted = authManager.deleteAccount(currentUser.getId());
        if (deleted) {
            logOut();
        }
        System.out.println("\nAccount deleted!\n");
        return deleted;
    }

    // When implementing the button for logging out / delete account, just call these two functions.
}
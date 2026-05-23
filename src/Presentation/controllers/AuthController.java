package Presentation.controllers;

import Business.AuthManager;
import Business.AuthResult;
import Business.Entities.User;
import Business.SessionManager;

import javax.swing.*;

/**
 * Controller responsible for authentication-related operations such as
 * login, signup, logout, and account deletion.
 */
public class AuthController {

    private final AuthManager authManager;
    private final SessionManager sessionManager;

    /**
     * Creates a new authentication controller.
     *
     * @param authManager manager responsible for authentication logic
     * @param sessionManager manager responsible for user session handling
     */
    public AuthController(AuthManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param email user email
     * @param password user password
     * @return the result of the authentication attempt
     */
    public AuthResult logIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) { return AuthResult.EMPTY_FIELDS; }

        User[] outUser = new User[1];
        AuthResult result = authManager.login(email, password, outUser);

        if (result == AuthResult.SUCCESS && outUser[0] != null) {
            SessionManager.getInstance().login(outUser[0]);
        }
        return result;
    }


    /**
     * Registers a new user account.
     *
     * @param user user to register
     * @return the result of the signup operation
     */
    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;

        AuthResult result = authManager.signUp(user);
        if (result == AuthResult.SUCCESS) sessionManager.login(user);
        return result;
    }


    /**
     * Logs out the currently authenticated user.
     */
    public void logOut() {
        System.out.println("\nUser logged out.");
        sessionManager.logout();
    }

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return current user ID
     */
    public int getUserID() {return sessionManager.getCurrentUser().getId();}

    /**
     * Deletes the currently logged-in user's account.
     *
     * @return true if the account was successfully deleted, false otherwise
     */
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

    /**
     * Retrieves a user by their ID.
     *
     * @param id user identifier
     * @return the user if found, otherwise null
     */
    public User getUserById(int id) {
        return authManager.getUserById(id);
    }
}
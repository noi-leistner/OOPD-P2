package Business;

import Business.Entities.User;
import Persistance.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Manages authentication and account opperations for parking system.
 * --- Responsabilities ---
 * - Validate credentials via BCrypt and resolve users by email or username
 * - Enforce email format and password strength on sign-up
 * - Delegate user persistance to UserDAO
 */
public class AuthManager {

    /** Connection to UserDAO */
    private final UserDAO userDAO;
    /** Constructor */
    public AuthManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    /**
     * Attempts login and returns a detailed result code. Populates outUser[0] on success.
     * @param outUser single-element array used as an output parameter; receives the User on SUCCESS
     * @return SUCCESS, EMPTY_FIELDS, USER_NOT_FOUND, or INVALID_CREDENTIALS
     */
    public AuthResult loginWithResult(String emailOrUsername, String password, User[] outUser) {
        if (emailOrUsername == null || emailOrUsername.isEmpty() || password == null || password.isEmpty()) {
            return AuthResult.EMPTY_FIELDS;
        }
        // 1. Try to find the user
        User user = userDAO.getUserByEmail(emailOrUsername.trim());
        // 2. If not found by email, try username
        if (user == null) {
            user = userDAO.getUserByUsername(emailOrUsername.trim());
        }
        // 3. SAFE CHECK: If user is still null, they don't exist
        if (user == null) {
            return AuthResult.USER_NOT_FOUND;
        }
        // 4. Now that we know user is not null, it is safe to check credentials
        if (BCrypt.checkpw(password, user.getPassword())) {
            if (outUser != null && outUser.length > 0) outUser[0] = user;
            return AuthResult.SUCCESS;
        }

        return AuthResult.INVALID_CREDENTIALS;
    }
    /**
     * Registers a new user after validating email format, password strength, and email uniqueness.
     * Hashes the password with BCrypt before persisting.
     * @return SUCCESS, INVALID_EMAIL, WEAK_PASSWORD, EMAIL_ALREADY_EXISTS, or DATABASE_ERROR
     */
    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;
        // Check for email requirements:
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!user.getEmail().matches(emailPattern)) {return AuthResult.INVALID_EMAIL;}

        //Check for password requirements:
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        if (!user.getPassword().matches(passwordPattern)) {return AuthResult.WEAK_PASSWORD;}

        if (userDAO.existsByEmail(user.getEmail())) return AuthResult.EMAIL_ALREADY_EXISTS;
        String hashedPass = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPass);
        return userDAO.addUser(user);
    }
    /**
     * Permanently deletes a user account.
     * @return true if deleted successfully, false if a DB error occurred
     */
    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }
    /** Get user by id */
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
}

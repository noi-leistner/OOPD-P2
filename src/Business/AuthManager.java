package Business;

import Business.Entities.User;
import Persistance.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthManager {

    private final UserDAO userDAO;

    public AuthManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String emailOrUsername, String password) {
        if (emailOrUsername == null || emailOrUsername.isBlank() || password == null || password.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        // Check admin credentials from config first
        if (emailOrUsername.equalsIgnoreCase(ConfigDAO.getAdminEmail()) && password.equals(ConfigDAO.getAdminPassword())) {
            User adminUser = new User("Admin", "User", emailOrUsername, "admin", password, "admin");
            SessionManager.getInstance().login(adminUser);
            return AuthResult.SUCCESS;
        }

        // Try email then username
        User user = userDAO.getUserByEmail(emailOrUsername.trim());
        if (user == null) {
            user = userDAO.getUserByUsername(emailOrUsername.trim());
        }
        if (user == null) return AuthResult.USER_NOT_FOUND;

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return AuthResult.INVALID_CREDENTIALS;
        }

        SessionManager.getInstance().login(user);
        return AuthResult.SUCCESS;
    }

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


    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

}

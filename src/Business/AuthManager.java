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

    public User login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDAO.getUserByEmail(email.trim());

        if (user == null) {
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
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

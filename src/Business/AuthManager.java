package Business;

import Business.Entities.User;
import Persistance.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

public class AuthManager {

    private final UserDAO userDAO;

    public AuthManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return AuthResult.EMPTY_FIELDS;
        }
        User user = userDAO.getUserByEmail(email.trim());

        if (user == null) {
            return AuthResult.INVALID_CREDENTIALS;
        }

        if (BCrypt.checkpw(password, user.getPassword())) {
            SessionManager.getInstance().login(user);
            return AuthResult.SUCCESS;
        }
        return AuthResult.INVALID_CREDENTIALS;
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

}

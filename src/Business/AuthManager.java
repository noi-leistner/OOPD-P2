package Business;

import Business.Entities.User;
import Persistance.UserDAOSql;
import org.mindrot.jbcrypt.BCrypt;

public class AuthManager {

    private UserDAOSql userDAO;

    public AuthManager(UserDAOSql userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return AuthResult.EMPTY_FIELDS;
        }
        User user = userDAO.getUserByEmail(email.trim());

        if (user == null) {
            System.out.println("DEBUG: EMAIL NOT FOUND");
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
        String hashedPass = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPass);
        return userDAO.addUser(user);
    }


    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

}

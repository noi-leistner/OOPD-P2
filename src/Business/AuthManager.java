package Business;

import Business.Entities.User;
import Persistance.UserDAOSql;

public class AuthManager {

    private UserDAOSql userDAO;

    public AuthManager(UserDAOSql userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {return AuthResult.EMPTY_FIELDS;}

        User user = userDAO.getUserByEmail(email);
        if (user == null) {return AuthResult.INVALID_CREDENTIALS;}
        if (!password.equals(user.getPassword())) {
            return AuthResult.INVALID_CREDENTIALS;
        } else {
            SessionManager.getInstance().login(user);
            return AuthResult.SUCCESS;
        }
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;
        return userDAO.addUser(user);
    }


    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

}

package Business;

import Business.Entities.User;
import Persistance.UserDAOSql;

public class AuthManager {

    private UserDAOSql userDAO;

    public AuthManager(UserDAOSql userDAO) {
        this.userDAO = userDAO;
    }

    User login(User user) {
        //TODO: Implement
        return null;
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;
        return userDAO.addUser(user);
    }


    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

}

package Business;

import Business.Entities.User;
import Persistance.UserDAO;

public class AuthManager {

    private UserDAO userDAO;

    public AuthManager(UserDAO userDAO) {
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

    void logout(int userId){
        //TODO: Implement
    }

    public boolean deleteAccount(int userId) {
        return userDAO.deleteUser(userId);
    }

}

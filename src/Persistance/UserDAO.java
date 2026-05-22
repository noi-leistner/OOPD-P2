package Persistance;

import Business.AuthResult;
import Business.Entities.User;

public interface UserDAO {
    AuthResult addUser(User user);
    boolean deleteUser(int id);
    User getUserById(int id);
    User getUserByEmail(String email);
    boolean existsByEmail(String email);
    User getUserByUsername(String trim);
}

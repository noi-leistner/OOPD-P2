package Persistance;

import Business.AuthResult;
import Business.Entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public AuthResult addUser(User user) {
        if (existsByEmail(user.getEmail())) return AuthResult.EMAIL_ALREADY_EXISTS;

        String sql = "INSERT INTO users (name, surname, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.executeUpdate();
            return AuthResult.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.DATABASE_ERROR;
        }
    }

    void deleteUSer(int id) {
        //TODO: Implement
    }

    User getUserById(int id) {
        //TODO: Implement
        return null;
    }

    public boolean existsByEmail(String email) {
        return true;
    }
}

package Persistance;

import Business.AuthResult;
import Business.Entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAOSql implements UserDAO {

    @Override
    public AuthResult addUser(User user) {
        if (existsByEmail(user.getEmail())) return AuthResult.EMAIL_ALREADY_EXISTS;

        String sql = "INSERT INTO users (name, surname, email, password, role) VALUES (?, ?, ?, ?,?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.executeUpdate();
            return AuthResult.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.DATABASE_ERROR;
        }
    }

    // TODO: Choose the order of deleting, either with database knowladge (cascade), or oopd knowladge (delete information reversing how it was created).
    // TODO: Delete ALL information, from RAM and all else.
    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT id, name, surname, email, password, role FROM users WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                User user =  new User (
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT id, name, surname, email, password, role FROM users WHERE email = ?";
        User user = null;
        try (Connection conn = ConfigDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);){

            if (conn == null) return null;
            stmt.setString(1, email);
            try(var rs = stmt.executeQuery();) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            var rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

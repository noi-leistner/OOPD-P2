package Persistance;

import Business.AuthResult;
import Business.Entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL implementation of UserDAO.
 * Handles all database operations for users.
 */
public class UserDAOSql implements UserDAO {

    /** Current user log */
    private static final Logger log = Logger.getLogger(ParkingSpaceDAOSql.class.getName());

    /**
     * Inserts a new user into the database.
     *
     * @param user the user to add
     *
     * @return SUCCESS, EMAIL_ALREADY_EXISTS if the email is taken, or DATABASE_ERROR
     */
    @Override
    public AuthResult addUser(User user) {
        if (existsByEmail(user.getEmail())) return AuthResult.EMAIL_ALREADY_EXISTS;

        String sql = "INSERT INTO users (name, surname, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getUsername());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole());
            stmt.executeUpdate();
            return AuthResult.SUCCESS;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return AuthResult.DATABASE_ERROR;
        }
    }


    /**
     * Permanently deletes a user by ID.
     *
     * @param id the user's ID
     *
     * @return true if a row was deleted, false otherwise
     */
    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns a user by their ID.
     *
     * @param id the user's ID
     *
     * @return the matching User, or null if not found
     */
    @Override
    public User getUserById(int id) {
        String sql = "SELECT id, name, surname, email, username, password, role FROM users WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new User (
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns a user by their email address.
     *
     * @param email the email to look up
     *
     * @return the matching User, or null if not found
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT id, name, surname, email, username, password, role FROM users WHERE email = ?";
        User user = null;
        try (Connection conn = ConfigDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            if (conn == null) return null;
            stmt.setString(1, email);
            try(var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     *
     * @return true if it exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            var rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns a user by their username.
     *
     * @param username the username to look up
     *
     * @return the matching User, or null if not found
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT id, name, surname, email, username, password, role " +
                "FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try(var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return user;
    }
}

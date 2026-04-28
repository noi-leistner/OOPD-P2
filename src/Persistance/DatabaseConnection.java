package Persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "user";
    private static final String PASSWORD = "userpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static PreparedStatement getPreparedStatement(Connection connection, String query) throws SQLException {
        return connection.prepareStatement(query);
    }
}
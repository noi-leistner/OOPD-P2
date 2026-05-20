package Persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class charged with the configuration to stablish connection with the MySQL inside Docker:
 */
public class ConfigDAO {
    /** URL from .env (inside .gitignore) from docker */
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?connectionTimeZone=Europe/Madrid";
    private static final String USER = "root";
    private static final String PASS = "";

    /**
     * Starts and returns an active MySQL connection:
     * @returns a connection.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Charge the Driver dynamically
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connection try:
            connection = DriverManager.getConnection(URL, USER, PASS);

        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found:" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Error:" + e.getMessage());
        }
        return connection;
    }




}
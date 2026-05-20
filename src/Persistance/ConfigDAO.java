package Persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class charged with the configuration to stablish connection with the MySQL inside Docker:
 */
public class ConfigDAO {
    /** URL from .env (inside .gitignore) from docker */
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC";
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

    public static int getVehicleEntryTime() {
        try {
            String json = new String(ConfigDAO.class.getResourceAsStream("/config.json").readAllBytes());
            String after = json.substring(json.indexOf("\"vehicle_entry_time\"") + 20).replaceAll("[^0-9]", " ").trim();
            return Integer.parseInt(after.split("\\s+")[0]);
        } catch (Exception e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Error reading config.json", e);
            return 30;
        }
    }



}
package Persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;
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
        String path = "config.json";
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) sb.append(line);

            String json = sb.toString();
            String key = "\"vehicle_entry_time\"";
            int index = json.indexOf(key);
            if (index == -1) return 30;

            String after = json.substring(index + key.length());
            after = after.replaceAll("[^0-9]", " ").trim();
            return Integer.parseInt(after.split("\\s+")[0]);
        } catch (Exception e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Error reading config.json", e);
            return 30; // fallback
        }
    }



}
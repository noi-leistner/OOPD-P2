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
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASS;

    static {
        String host = "localhost", port = "3306", name = "mydb", user = "root", pass = "";
        try {
            String json = new String(ConfigDAO.class.getResourceAsStream("/config.json").readAllBytes());
            host = extractString(json, "db_host");
            port = extractNumber(json, "db_port");
            name = extractString(json, "db_name");
            user = extractString(json, "db_username");
            pass = extractString(json, "db_password");
        } catch (Exception e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Failed to load config.json at start", e);
        }
        DB_URL  = "jdbc:mysql://" + host + ":" + port + "/" + name + "?connectionTimeZone=Europe/Madrid";
        DB_USER = user;
        DB_PASS = pass;
    }

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
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

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
            return Integer.parseInt(extractNumber(json, "vehicle_entry_time"));
        } catch (Exception e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, null, e);
            return 30;
        }
    }


    // Helpers:
    private static String extractString(String json, String key) {
        int keyId = json.indexOf("\"" + key + "\"");
        int colonId = json.indexOf(":", keyId);
        int open = json.indexOf('"', colonId);
        int close = json.indexOf('"', open + 1);
        return json.substring(open + 1, close);
    }

    private static String extractNumber(String json, String key) {
        int keyId = json.indexOf("\"" + key + "\"");
        String after = json.substring(json.indexOf(':', keyId) + 1).trim();
        return after.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
    }

}
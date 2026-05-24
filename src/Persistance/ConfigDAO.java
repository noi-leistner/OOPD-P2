package Persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the configuration and creation of MySQL database connections.
 */
public class ConfigDAO {

    /** The JDBC database connection URL. */
    private static final String DB_URL;

    /** The database username. */
    private static final String DB_USER;

    /** The database password. */
    private static final String DB_PASS;

    /** The base time interval configuration for vehicle entry simulations. */
    private static final int    VEHICLE_ENTRY_TIME;

    /** The maximum allowed stay duration in minutes for a parked vehicle. */
    private static final int    MAX_STAY_MINUTES;

    /** The default master admin password loaded from configuration. */
    private static final String ADMIN_PASSWORD;

    /** The default master admin email address loaded from configuration. */
    private static final String ADMIN_EMAIL;

    static {
        // defaults
        String host = "localhost", port = "3306", name = "mydb",
                user = "root", pass = "", adminPass = "admin",
                adminEmail = "admin@lsparking.com";
        int entryTime = 30, maxStay = 3;

        try {
            String json = new String(ConfigDAO.class.getResourceAsStream("/config.json").readAllBytes());
            host       = extractString(json, "db_host");
            port       = extractNumber(json, "db_port");
            name       = extractString(json, "db_name");
            user       = extractString(json, "db_username");
            pass       = extractString(json, "db_password");
            adminPass  = extractString(json, "admin_password");
            adminEmail = extractString(json, "admin_email");
            entryTime  = Integer.parseInt(extractNumber(json, "vehicle_entry_time"));
            maxStay    = Integer.parseInt(extractNumber(json, "max_stay_minutes"));
        } catch (Exception e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Failed to load config.json at start", e);
        }
        DB_URL             = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?connectionTimeZone=Europe/Madrid";
        DB_USER            = user;
        DB_PASS            = pass;
        ADMIN_PASSWORD     = adminPass;
        ADMIN_EMAIL        = adminEmail;
        VEHICLE_ENTRY_TIME = entryTime;
        MAX_STAY_MINUTES   = maxStay;
    }

    /**
     * Starts and returns an active MySQL connection:
     * @return a connection.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets the configured time interval pacing for vehicle entries.
     *
     * @return the vehicle entry time configuration value.
     */
    public static int    getVehicleEntryTime() { return VEHICLE_ENTRY_TIME; }

    /**
     * Gets the maximum time limit a vehicle is permitted to remain in the parking facilities.
     *
     * @return the maximum stay limit in minutes.
     */
    public static int    getMaxStayMinutes()   { return MAX_STAY_MINUTES; }

    /**
     * Gets the root administrator password designated by system configurations.
     *
     * @return the administrator password string.
     */
    public static String getAdminPassword()    { return ADMIN_PASSWORD; }

    /**
     * Gets the primary contact/system email address for the root administrator.
     *
     * @return the administrator email string.
     */
    public static String getAdminEmail()       { return ADMIN_EMAIL; }

    /**
     * A string parser designed to extract text values paired with a specific key
     * from a standard JSON structural string.
     *
     * @param json the raw string representation of the JSON payload.
     * @param key  the exact name of the target key property to find.
     * @return the isolated string text value associated with the specified key.
     * @throws IndexOutOfBoundsException if the key formatting or layout patterns mismatch assumptions.
     */
    private static String extractString(String json, String key) {
        int keyId = json.indexOf("\"" + key + "\"");
        int colonId = json.indexOf(":", keyId);
        int open = json.indexOf('"', colonId);
        int close = json.indexOf('"', open + 1);
        return json.substring(open + 1, close);
    }

    /**
     * A digit extractor designed to parse purely numeric sequence strings
     * matching a target property key within a JSON payload string.
     *
     * @param json the raw string representation of the JSON payload.
     * @param key  the exact name of the target numeric property to find.
     * @return a clean digit string.
     */
    private static String extractNumber(String json, String key) {
        int keyId = json.indexOf("\"" + key + "\"");
        String after = json.substring(json.indexOf(':', keyId) + 1).trim();
        return after.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
    }

}
package Business.Entities;

public class Config {
    private int db_port;
    private String db_host;
    private String db_name;
    private String db_username;
    private String db_password;
    private String admin_password;
    private int vehicle_delay;

    public int getDb_port() { return db_port; }
    public String getDb_host() { return db_host; }
    public String getDb_name() { return db_name; }
    public String getDb_username() { return db_username; }
    public String getDb_password() { return db_password; }
    public String getAdmin_password() { return admin_password; }
    public int getVehicle_delay() { return vehicle_delay; }
}

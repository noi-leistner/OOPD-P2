package Business.Entities;

public class Vehicle {
    private String license_plate;
    private int userId;
    private String type;

    public Vehicle(String license_plate, int userId, String type) {
        this.license_plate = license_plate;
        this.userId = userId;
        this.type = type;
    }

    public String getLicense_plate() { return license_plate; }
    public int getUserId()           { return userId; }
    public String getType()          { return type; }

    public void setLicense_plate(String license_plate) { this.license_plate = license_plate; }
    public void setType(String type)                   { this.type = type; }
}
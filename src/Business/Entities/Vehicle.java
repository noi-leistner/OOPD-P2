package Business.Entities;

/** Represents a vehicle registered in the parking system.
 * --- Responsabilities: ---
 * - Hold vehicle identity (plate, type) and owner reference
 */
public class Vehicle {
    private String license_plate;
    private int userId;
    /** Vehicle type: "car" or "motorcycle". */
    private String type;
    /** Constructor */
    public Vehicle(String license_plate, int userId, String type) {
        this.license_plate = license_plate;
        this.userId = userId;
        this.type = type;
    }

    public String getLicensePlate() { return license_plate; }
    public int getUserId()           { return userId; }
    public String getType()          { return type; }

    public void setLicensePlate(String license_plate) { this.license_plate = license_plate; }
    public void setType(String type)                   { this.type = type; }
}
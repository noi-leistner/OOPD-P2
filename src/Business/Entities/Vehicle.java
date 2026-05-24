package Business.Entities;

/**
 * Represents a vehicle registered in the parking system.
 * <p>
 * This class stores the basic details of a vehicle, including its unique
 * license plate, its type (such as "car" or "motorcycle"), and a reference
 * to the user who owns it.
 */
public class Vehicle {

    /** The unique license plate number of the vehicle. */
    private String license_plate;

    /** The ID of the user who owns this vehicle. */
    private final int userId;

    /** The classification type of the vehicle (e.g., "car", "motorcycle"). */
    private String type;

    /**
     * Creates a new Vehicle with its required registration information.
     *
     * @param license_plate the unique license plate string
     * @param userId        the ID of the owning user
     * @param type          the vehicle type category
     */
    public Vehicle(String license_plate, int userId, String type) {
        this.license_plate = license_plate;
        this.userId = userId;
        this.type = type;
    }

    /**
     * Gets the vehicle's unique license plate number.
     *
     * @return the license plate string
     */
    public String getLicensePlate() { return license_plate; }

    /**
     * Gets the ID of the user who owns this vehicle.
     *
     * @return the owner's user ID
     */
    public int getUserId() { return userId; }

    /**
     * Gets the type category of the vehicle.
     *
     * @return the type string (like "car" or "motorcycle")
     */
    public String getType() { return type; }

    /**
     * Updates the vehicle's license plate number.
     *
     * @param license_plate the new license plate string to assign
     */
    public void setLicensePlate(String license_plate) { this.license_plate = license_plate; }

    /**
     * Updates the vehicle's type classification.
     *
     * @param type the new vehicle type string to set
     */
    public void setType(String type) { this.type = type; }
}
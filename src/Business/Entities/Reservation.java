package Business.Entities;

import java.util.Date;

/**
 * Represents a parking space reservation made by a user.
 * <p>
 * This class stores all the details about a booking, including who made it,
 * which vehicle and spot it belongs to, when it starts and ends, and whether
 * it was cancelled.
 */
public class Reservation {

    /** The unique ID number for this reservation in the database. */
    private int id;

    /** The ID of the user who made the reservation. */
    private final int user_id;

    /** The license plate of the vehicle tied to this reservation. */
    private final String vehicle_license_plate;

    /** The ID of the parking space that has been booked. */
    private int parking_slot_id;

    /** The date and time when the reservation starts. */
    private final Date startDateTime;

    /** The date and time when the reservation ends. */
    private final Date endDateTime;

    /** Tracks if the reservation has been cancelled (true if cancelled, false if active). */
    private final boolean isCancelled;

    /**
     * Creates a new Reservation with all its required information.
     *
     * @param id                    the unique database ID
     * @param user_id               the ID of the user booking the spot
     * @param vehicle_license_plate the license plate of the vehicle
     * @param parking_slot_id       the ID of the booked parking spot
     * @param startDateTime         the start date and time
     * @param endDateTime           the end date and time
     * @param isCancelled           true if the reservation is cancelled, false otherwise
     */
    public Reservation (int id, int user_id, String vehicle_license_plate, int parking_slot_id, Date startDateTime, Date endDateTime, boolean isCancelled) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.parking_slot_id = parking_slot_id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCancelled = isCancelled;
    }

    /**
     * Gets the unique ID of this reservation.
     *
     * @return the reservation ID
     */
    public int getId() {return id;}

    /**
     * Gets the ID of the user who owns this reservation.
     *
     * @return the user ID
     */
    public int getUserId() {return user_id;}

    /**
     * Gets the license plate number of the reserved vehicle.
     *
     * @return the license plate string
     */
    public String getVehiclePlate() {return vehicle_license_plate;}

    /**
     * Gets the ID of the reserved parking space.
     *
     * @return the parking space ID
     */
    public int getParkingSlotId() {return parking_slot_id;}

    /**
     * Gets the start date and time of the reservation.
     *
     * @return the start date and time
     */
    public Date getStartDateTime() { return startDateTime; }

    /**
     * Gets the end date and time of the reservation.
     *
     * @return the end date and time
     */
    public Date getEndDateTime() { return endDateTime; }

    /**
     * Sets or updates the reservation ID (usually used after saving to the database).
     *
     * @param id the new reservation ID
     */
    public void setId(int id) {this.id = id;}

    /**
     * Changes the parking space assigned to this reservation.
     *
     * @param parking_slot_id the new parking space ID
     */
    public void setParkingSlotId(int parking_slot_id) {this.parking_slot_id = parking_slot_id;}

    /**
     * Checks if this reservation has been cancelled.
     *
     * @return true if it was cancelled, false if it is still active
     */
    public boolean isCancelled() {
        return isCancelled;
    }
}
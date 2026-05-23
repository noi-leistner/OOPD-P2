package Business.Entities;

import java.util.Date;

/** Represents a parking space reservation made by a user.
 * -- Responsabilities: --
 * - Hold reservation identity and ownership (user, vehicle, solt)
 * - Define the active time window (start/end)
 * - Track cancellation state
 */
public class Reservation {
    private int id;                         // DB: id (auto-increment)
    private int user_id;
    private String vehicle_license_plate;
    private int parking_slot_id;
    private Date startDateTime;
    private Date endDateTime;
    private boolean isCancelled;

    public Reservation (int id, int user_id, String vehicle_license_plate, int parking_slot_id, Date startDateTime, Date endDateTime, boolean isCancelled) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.parking_slot_id = parking_slot_id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCancelled = isCancelled;
    }

    public int getId() {return id;}

    public int getUserId() {return user_id;}

    public String getVehiclePlate() {return vehicle_license_plate;}

    public int getParkingSlotId() {return parking_slot_id;}

    public Date getStartDateTime() { return startDateTime; }

    public Date getEndDateTime() { return endDateTime; }

    public void setId(int id) {this.id = id;}

    public void setParkingSlotId(int parking_slot_id) {this.parking_slot_id = parking_slot_id;}

    public boolean isCancelled() {
        return isCancelled;
    }
}

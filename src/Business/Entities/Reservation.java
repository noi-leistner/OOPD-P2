package Business.Entities;

import java.util.Date;

public class Reservation {
    int id;                         // DB: id (auto-increment)
    int user_id;
    String vehicle_license_plate;
    int parking_slot_id;
    Date startDateTime;
    Date endDateTime;
    boolean isCancelled;

    public Reservation (int id, int user_id, String vehicle_license_plate, int parking_slot_id, Date startDateTime, Date endDateTime, boolean isCancelled) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.parking_slot_id = parking_slot_id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCancelled = isCancelled;
    }

    public Reservation (int user_id, String vehicle_license_plate, int parking_slot_id, Date startDateTime, Date endDateTime, boolean isCancelled) {
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.parking_slot_id = parking_slot_id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCancelled = isCancelled;
    }

    public int getId() {return id;}

    public int getUser_id() {return user_id;}

    public String getVehiclePlate() {return vehicle_license_plate;}

    public int getParking_slot_id() {return parking_slot_id;}

    public Date getStartDateTime() { return startDateTime; }

    public Date getEndDateTime()   { return endDateTime; }

    public void setId(int id) {this.id = id;}

    public void setUser_id(int user_id) {this.user_id = user_id;}

    public void setVehicle_license_plate(String vehicle_license_plate) {this.vehicle_license_plate = vehicle_license_plate;}

    public void setParking_slot_id(int parking_slot_id) {this.parking_slot_id = parking_slot_id;}

    public boolean isCancelled() {
        return isCancelled;
    }
}

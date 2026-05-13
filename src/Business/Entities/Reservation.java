package Business.Entities;

import java.util.Date;

public class Reservation {
    int id;                         // DB: id (auto-increment)
    int user_id;
    String vehicle_license_plate;
    int parking_slot_id;
    Date date;

    public Reservation (int id, int user_id, String vehicle_license_plate, int parking_slot_id, Date date) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.parking_slot_id = parking_slot_id;
        this.date = date;
    }

    public int getId() {return id;}

    public int getUser_id() {return user_id;}

    public String getVehiclePlate() {return vehicle_license_plate;}

    public int getParking_slot_id() {return parking_slot_id;}

    public Date getDate() {return date;}

    public void setId(int id) {this.id = id;}

    public void setUser_id(int user_id) {this.user_id = user_id;}

    public void setVehicle_license_plate(String vehicle_license_plate) {this.vehicle_license_plate = vehicle_license_plate;}

    public void setParking_slot_id(int parking_slot_id) {this.parking_slot_id = parking_slot_id;}

    public void setDate(Date date) {this.date = date;}
}

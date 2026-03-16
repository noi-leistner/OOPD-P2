package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import java.util.List;

public class ParkingLotManager {

    ParkingSpace addSpace(int code, int floor, String vehicleType) {
        //TODO: Implement
        return null;
    }

    void editSpace(int spaceId) {
        //TODO: Implement
    }

    void deleteSpace(int spaceId) {
        //TODO: Implement
    }


    ParkingSpace getSpaceDetails(int spaceId) {
        //TODO: Implement
        return null;
    }

    List<ParkingSpace> getAllSpaces() {
        //TODO: Implement
        return null;
    }

    List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        //TODO: Implement
        return null;
    }

    ParkingSpace enterWithReservation(String licensePlate) {
        //TODO: Implement
        return null;
    }

    ParkingSpace enterWithoutReservation(String licensePlate, String vehicleType) {
        //TODO: Implement
        return null;
    }

    void exit(String licensePlate){
        //TODO: Implement
    }

    Reservation reserve(String licensePlate, String vehicleType, int spaceId) {
        //TODO: Implement
        return null;
    }

    void cancelReservation(int reservationId, String licensePlate){
        //TODO: Implement
    }

    void cancelReservationByAdmin(int spaceId) {
        //TODO: Implement
    }

    List<Reservation> getUserReservations(int userId) {
        //TODO: Implement
        return null;
    }

    void getOccupancyLastHour() {
        //TODO: Implement
    }
}

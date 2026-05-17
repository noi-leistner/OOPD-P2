package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.ParkingLotManager;

import java.util.List;

public class EntryExitController {

    private final ParkingLotManager manager;

    public EntryExitController(ParkingLotManager manager) {
        this.manager = manager;
    }

    public boolean hasReservation(String plate) {
        return manager.reservationExistsForPlate(plate);
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        return manager.vehicleBelongsToUser(licensePlate, userId);
    }

    public String getVehicleType(String licensePlate) {
        return manager.getVehicleType(licensePlate);
    }

    public ParkingSpace enterWithReservation(String plate, int userId) {
        return manager.enterWithReservation(plate, userId);
    }

    public ParkingSpace enterWithoutReservation(String plate, int spaceId, int userId) {
        return manager.enterWithoutReservation(plate, spaceId, userId);
    }

    public ParkingSpace exitParking(String plate, int userId) {
        return manager.exit(plate, userId);
    }

    public Reservation getReservationForSpace(int spaceId) {
        return manager.getReservationForSpace(spaceId);
    }

    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return manager.getAvailableSpacesForType(vehicleType);
    }
}
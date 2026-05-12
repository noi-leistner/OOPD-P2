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

    public ParkingSpace enterWithReservation(String plate) {
        return manager.enterWithReservation(plate);
    }

    public ParkingSpace enterWithoutReservation(String plate, int spaceId) {
        return manager.enterWithoutReservation(plate, spaceId);
    }

    public ParkingSpace exitParking(String plate) {
        return manager.exit(plate);
    }

    public Reservation getReservationForSpace(int spaceId) {
        return manager.getReservationForSpace(spaceId);
    }

    public boolean vehiclePlateExists(String licensePlate) {
        return manager.vehiclePlateExists(licensePlate);
    }



    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return manager.getAvailableSpacesForType(vehicleType);
    }
}
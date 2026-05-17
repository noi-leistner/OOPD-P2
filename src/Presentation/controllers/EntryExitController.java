package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.ParkingLotManager;
import Business.ReservationManager;

import java.util.List;

public class EntryExitController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;

    public EntryExitController(ParkingLotManager parkingLotManager, ReservationController reservationController) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
    }

    public boolean hasReservation(String plate) {
        return reservationController.reservationExistsForPlate(plate);
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        return parkingLotManager.vehicleBelongsToUser(licensePlate, userId);
    }

    public String getVehicleType(String licensePlate) {
        return parkingLotManager.getVehicleType(licensePlate);
    }

    public ParkingSpace enterWithReservation(String plate, int userId) {
        return parkingLotManager.enterWithReservation(plate, userId);
    }

    public ParkingSpace enterWithoutReservation(String plate, int spaceId, int userId) {
        return parkingLotManager.enterWithoutReservation(plate, spaceId, userId);
    }

    public ParkingSpace exitParking(String plate, int userId) {
        return parkingLotManager.exit(plate, userId);
    }

    public Reservation getReservationForSpace(int spaceId) {
        return reservationController.getReservationBySlotId(spaceId);
    }

    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return parkingLotManager.getAvailableSpacesForType(vehicleType);
    }
}
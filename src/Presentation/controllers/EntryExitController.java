package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.ParkingLotManager;
import Business.ReservationManager;
import Persistance.VehicleDAO;

import java.util.List;

public class EntryExitController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;
    private final VehicleDAO vehicleDAO;

    public EntryExitController(ParkingLotManager parkingLotManager, ReservationController reservationController, VehicleDAO vehicleDAO) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
        this.vehicleDAO = vehicleDAO;
    }

    public boolean hasReservation(String plate) {
        return reservationController.reservationExistsForPlate(plate);
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        return parkingLotManager.vehicleBelongsToUser(licensePlate, userId);
    }

    public void deleteByUserId(int id) {vehicleDAO.deleteByUserId(id);}

    public boolean registerVehicle(String licensePlate, int userId, String vehicleType) {
        return parkingLotManager.registerVehicle(licensePlate, userId, vehicleType);
    }

    public boolean vehiclePlateExistsInSystem(String licensePlate) {
        return parkingLotManager.vehiclePlateExistsInSystem(licensePlate);
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
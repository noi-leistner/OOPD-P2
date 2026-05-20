package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.Entities.Vehicle;
import Business.ParkingLogManager;
import Business.ParkingLotManager;
import Business.ReservationManager;
import Persistance.VehicleDAO;
import Business.VehicleManager;

import java.util.Comparator;
import java.util.List;

public class EntryExitController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;
    private final VehicleManager vehicleManager;
    private final ParkingLogManager parkingLogManager;

    public EntryExitController(ParkingLotManager parkingLotManager, ReservationController reservationController, VehicleManager vehicleManager, ParkingLogManager parkingLogManager) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
        this.vehicleManager = vehicleManager;
        this.parkingLogManager = parkingLogManager;
    }

    public boolean hasReservation(String plate) {
        return reservationController.reservationExistsForPlate(plate);
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        return vehicleManager.vehicleBelongsToUser(licensePlate, userId);
    }

    public void deleteByUserId(int id) {vehicleManager.deleteByUserID(id);}

    public boolean registerVehicle(String licensePlate, int userId, String vehicleType) {
        return vehicleManager.registerVehicle(licensePlate, userId, vehicleType);
    }

    public boolean vehiclePlateExistsInSystem(String licensePlate) {
        return vehicleManager.vehiclePlateExistsInSystem(licensePlate);
    }

    public String getVehicleType(String licensePlate) {
        return vehicleManager.getVehicleType(licensePlate);
    }

    public ParkingSpace enterWithReservation(String plate, int userId) {
        ParkingSpace space = parkingLotManager.enterWithReservation(plate, userId);
        if (space != null) {
            parkingLogManager.logEntry(space.getId(), plate, userId);
        }
        return space;
    }

    public ParkingSpace enterWithoutReservation(String plate, int spaceId, int userId) {
        ParkingSpace space = parkingLotManager.enterWithoutReservation(plate, spaceId, userId);
        if (space != null) {
            parkingLogManager.logEntry(space.getId(), plate, userId);
        }
        return space;
    }

    public ParkingSpace exitParking(String plate, int userId) {
        ParkingSpace space = parkingLotManager.exit(plate, userId);
        if (space != null) {
            parkingLogManager.logExit(space.getId(), plate, userId);
        }
        return space;
    }

    public Reservation getFirstReservationForSpace(int spaceId) {
        List<Reservation> reservations = reservationController.getReservationsBySlotId(spaceId);
        if (reservations.isEmpty()) return null;

        return reservations.stream().min(Comparator.comparing(Reservation::getStartDateTime)).orElse(null);
    }

    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return parkingLotManager.getAvailableSpacesForType(vehicleType);
    }

    public boolean vehicleExistsForOtherUser(String plate, int userId) {
        return vehicleManager.existsForOtherUser(plate, userId);
    }

    public Vehicle ensureVehicleExists(String plate, String type, int userId) {
        return vehicleManager.ensureVehicleExists(plate, type, userId);
    }
}
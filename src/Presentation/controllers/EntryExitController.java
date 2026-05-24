package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.Entities.Vehicle;
import Business.ParkingLogManager;
import Business.ParkingLotManager;
import Business.VehicleManager;

import java.util.Comparator;
import java.util.List;

/**
 * Controller for vehicle entry and exit operations.
 * Coordinates ParkingLotManager, VehicleManager, ParkingLogManager and ReservationController
 * since entry/exit is a cross-domain operation touching spaces, vehicles, logs and reservations.
 */
public class EntryExitController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;
    private final VehicleManager vehicleManager;
    private final ParkingLogManager parkingLogManager;

    /**
     * Creates a new EntryExitController
     *
     * @param parkingLotManager     manages parking space state
     * @param reservationController handles reservation lookups
     * @param vehicleManager        manages vehicle data
     * @param parkingLogManager     handles entry/exit logging
     */
    public EntryExitController(ParkingLotManager parkingLotManager, ReservationController reservationController, VehicleManager vehicleManager, ParkingLogManager parkingLogManager) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
        this.vehicleManager = vehicleManager;
        this.parkingLogManager = parkingLogManager;
    }

    /** Returns true if the given plate has an active reservation right now. */
    public boolean hasReservationNow(String plate) {
        return reservationController.hasActiveReservationForPlate(plate);
    }

    /** Returns true if the given vehicle is currently parked in the lot. */
    public boolean isVehicleCurrentlyParked(String plate) {
        return parkingLogManager.isVehicleCurrentlyParked(plate);
    }

    /** Returns true if the given license plate belongs to the given user. */
    public boolean vehicleBelongsToUser(String licensePlate, int userId) {return vehicleManager.vehicleBelongsToUser(licensePlate, userId);}

    /** Deletes all vehicles belonging to the given user. */
    public void deleteByUserId(int id) {vehicleManager.deleteByUserID(id);}

    /**
     * Registers a new vehicle under the given user.
     *
     * @return true if successful, false otherwise
     */
    public boolean registerVehicle(String licensePlate, int userId, String vehicleType) {
        return vehicleManager.registerVehicle(licensePlate, userId, vehicleType);
    }

    /** Returns true if a vehicle with the given plate exists in the system. */
    public boolean vehiclePlateExistsInSystem(String licensePlate) {
        return vehicleManager.vehiclePlateExists(licensePlate);
    }

    /** Returns the vehicle type for the given license plate, or null if not found. */
    public String getVehicleType(String licensePlate) {
        return vehicleManager.getVehicleType(licensePlate);
    }

    /**
     * Occupies the reserved space for the given plate and logs the entry.
     *
     * @return the occupied ParkingSpace, or null if no reservation was found
     */
    public ParkingSpace enterWithReservation(String plate, int userId) {
        ParkingSpace space = parkingLotManager.enterWithReservation(plate);
        if (space != null) {
            parkingLogManager.logEntry(space.getId(), plate, userId);
        }
        return space;
    }

    /**
     * Returns the parking space reserved for the given plate, or null if none.
     */
    public ParkingSpace getReservedSpaceForPlate(String plate) {
        Reservation reservation = reservationController.getActiveReservationForPlate(plate);
        if (reservation == null) return null;
        return parkingLotManager.getSpaceDetails(reservation.getParkingSlotId());
    }

    /**
     * Occupies a specific space for a vehicle without a reservation and logs the entry.
     *
     * @return the occupied ParkingSpace, or null if the operation failed
     */
    public ParkingSpace enterWithoutReservation(String plate, int spaceId, int userId) {
        ParkingSpace space = parkingLotManager.enterWithoutReservation(plate, spaceId);
        if (space != null) {
            parkingLogManager.logEntry(space.getId(), plate, userId);
        }
        return space;
    }

    /**
     * Frees the space occupied by the given plate and logs the exit.
     * @return the vacated ParkingSpace, or null if the vehicle was not found
     */
    public ParkingSpace exitParking(String plate, int userId) {
        ParkingSpace space = parkingLotManager.exit(plate);
        if (space != null) {
            parkingLogManager.logExit(space.getId(), plate, userId);
        }
        return space;
    }

    /**
     * Returns the earliest upcoming reservation for a given space, or null if none.
     */
    public Reservation getFirstReservationForSpace(int spaceId) {
        List<Reservation> reservations = reservationController.getReservationsBySlotId(spaceId);
        if (reservations.isEmpty()) return null;

        return reservations.stream().min(Comparator.comparing(Reservation::getStartDateTime)).orElse(null);
    }

    /** Returns all unoccupied spaces that match the given vehicle type. */
    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return parkingLotManager.getAvailableSpacesForType(vehicleType);
    }

    /** Returns true if the plate exists in the system but belongs to a different user. */
    public boolean vehicleExistsForOtherUser(String plate, int userId) {
        return vehicleManager.existsForOtherUser(plate, userId);
    }

    /** Returns the vehicle for the given plate, creating it if it doesn't exist yet. */
    public Vehicle ensureVehicleExists(String plate, String type, int userId) {
        return vehicleManager.ensureVehicleExists(plate, type, userId);
    }

    /** Returns the license plate of the vehicle currently parked at the given space, or null if empty. */
    public String getParkedPlateAtSpace(int spaceId) {
        return parkingLotManager.getParkedPlateAtSpace(spaceId);
    }
}
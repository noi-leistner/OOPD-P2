package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.ParkingLotManager;
import Business.DaoResult;
import Business.ReservationManager;

import java.util.List;

/**
 * Controller for managing parking spaces.
 * Bridges the admin UI with ParkingLotManager and ReservationController.
 */
public class ParkingSpaceController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;

    /**
     * Creates a new ParkingSpaceController.
     *
     * @param parkingLotManager     manages parking space state and operations
     * @param reservationController handles reservation lookups for affected spaces
     */
    public ParkingSpaceController(ParkingLotManager parkingLotManager, ReservationController reservationController) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
    }

    /**
     * Creates and adds a new parking space.
     *
     * @param code        the space identifier
     * @param floor       the floor it is on
     * @param vehicleType the type of vehicle it accepts
     * @param occStatus   initial occupation status
     *
     * @return SUCCESS or a relevant DaoResult error
     */
    public DaoResult addSpace(int code, int floor, String vehicleType, boolean occStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, null, vehicleType);
        return parkingLotManager.addSpace(space);
    }

    /** Vacates all spaces occupied by vehicles belonging to the given user. */
    public void vacateSpacesByUserId(int id) {parkingLotManager.vacateSpacesByUserId(id);}

    /**
     * Updates the floor and vehicle type of existing space.
     *
     * @param code        the space identifier
     * @param floor       the new floor
     * @param vehicleType the new vehicle type
     *
     * @return SUCCESS or a relevant DaoResult error
     */
    public DaoResult editSpace(int code, int floor, String vehicleType) {
        boolean occupied = getSpaceDetails(code).isOccupied();
        ParkingSpace space = new ParkingSpace(code, floor, occupied, vehicleType, null);

        return parkingLotManager.editSpace(space);
    }

    /**
     * Removes a parking space. If occupied, attempts to move the vehicle to an
     * alternative space of the same type before deleting.
     *
     * @param spaceId the space to remove
     *
     * @return SUCCESS, NOT_FOUND, CANNOT_REMOVE_OCCUPIED if no alternative exists, or DATABASE_ERROR
     */
    public DaoResult removeSpace(int spaceId) {
        ParkingSpace space = parkingLotManager.getSpaceDetails(spaceId);
        if (space == null) return DaoResult.NOT_FOUND;

        if (space.isOccupied()) {
            ParkingSpace alternative = parkingLotManager.findAlternativeSpace(space.getType(), spaceId);
            if (alternative == null) return DaoResult.CANNOT_REMOVE_OCCUPIED;
            parkingLotManager.moveVehicle(space, alternative);
        }

        return parkingLotManager.deleteSpace(spaceId);
    }

    /**
     * Returns an available space of the given type, excluding a specific space ID.
     * Used to find alternatives before deleting an occupied space.
     */
    public ParkingSpace findAlternativeSpace(String type, int excludeId) {
        return parkingLotManager.findAlternativeSpace(type, excludeId);
    }

    /** Returns the details of a single parking space by ID, or null if not found. */
    public ParkingSpace getSpaceDetails(int spaceId) {
        return parkingLotManager.getSpaceDetails(spaceId);
    }

    /** Returns all parking spaces in the system. */
    public List<ParkingSpace> getAllSpaces() {
        return parkingLotManager.getAllSpaces();
    }

    /** Returns all floor numbers that are available, excluding the given floor. */
    public List<Integer> getAvailableFloors(int currentFloor) {
        return parkingLotManager.getAvailableFloors(currentFloor);
    }

    /** Returns all spaces that accept the given vehicle type. */
    public List<ParkingSpace> getSpotsByType(String type) {
        return parkingLotManager.getSpotsByType(type);
    }
}
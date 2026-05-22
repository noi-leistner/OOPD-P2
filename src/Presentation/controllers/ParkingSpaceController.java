package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.ParkingLotManager;
import Business.DaoResult;
import Business.ReservationManager;

import java.util.List;

public class ParkingSpaceController {

    private final ParkingLotManager parkingLotManager;
    private final ReservationController reservationController;

    public ParkingSpaceController(ParkingLotManager parkingLotManager, ReservationController reservationController) {
        this.parkingLotManager = parkingLotManager;
        this.reservationController = reservationController;
    }

    public DaoResult addSpace(int code, int floor, String vehicleType, boolean occStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, null, vehicleType);
        return parkingLotManager.addSpace(space);
    }

    public void vacateSpacesByUserId(int id) {parkingLotManager.vacateSpacesByUserId(id);}

    public DaoResult editSpace(int code, int floor, String vehicleType) {
        boolean occupied = getSpaceDetails(code).isOccupied();
        ParkingSpace space = new ParkingSpace(code, floor, occupied, vehicleType, null);

        return parkingLotManager.editSpace(space);
    }

    public String getParkedPlateAtSpace(int spaceId) {
        return parkingLotManager.getParkedPlateAtSpace(spaceId);
    }

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

    public ParkingSpace findAlternativeSpace(String type, int excludeId) {
        return parkingLotManager.findAlternativeSpace(type, excludeId);
    }

    public ParkingSpace getSpaceDetails(int spaceId) {
        return parkingLotManager.getSpaceDetails(spaceId);
    }

    // TODO: maybe not need this function
    public boolean slotExists(int id) {
        return parkingLotManager.slotExistsById(id);
    }

    public List<ParkingSpace> getAllSpaces() {
        return parkingLotManager.getAllSpaces();
    }

    public List<Integer> getAvailableFloors(int currentFloor) {
        return parkingLotManager.getAvailableFloors(currentFloor);
    }

    public List<ParkingSpace> getAvailableSpotsByType(String type) {
        return parkingLotManager.getAvailableSpacesForType(type);
    }

    public List<ParkingSpace> getSpotsByType(String type) {
        return parkingLotManager.getSpotsByType(type);
    }
}
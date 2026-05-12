package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.ParkingLotManager;
import Business.DaoResult;

import java.util.List;

public class ParkingSpaceController {

    private final ParkingLotManager manager;

    public ParkingSpaceController(ParkingLotManager manager) {
        this.manager = manager;
    }

    public DaoResult addSpace(int code, int floor, String vehicleType, boolean occStatus, boolean resStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, resStatus, vehicleType);
        return manager.addSpace(space);
    }

    public DaoResult editSpace(int code, int floor, String vehicleType, boolean resStatus) {
        boolean occupied = getSpaceDetails(code).isOccupied();
        ParkingSpace space = new ParkingSpace(code, floor, occupied, resStatus, vehicleType);

        return manager.editSpace(space);
    }

    public DaoResult removeSpace(int spaceId) {
        return manager.deleteSpace(spaceId);
    }

    public ParkingSpace getSpaceDetails(int spaceId) {
        //TODO: Implement
        return manager.getSpaceDetails(spaceId);
    }

    public void cancelReservationFromAdmin(int spaceId) {
        //TODO: implement
    }

    // TODO: maybe not need this function
    public boolean slotExists(int id) {
        return manager.slotExistsById(id);
    }

    public List<ParkingSpace> getAllSpaces() {
        return manager.getAllSpaces();
    }
}

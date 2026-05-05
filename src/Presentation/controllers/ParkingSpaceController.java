package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.ParkingLotManager;
import Business.SpaceResult;

public class ParkingSpaceController {

    private final ParkingLotManager manager;

    public ParkingSpaceController(ParkingLotManager manager) {
        this.manager = manager;
    }

    public SpaceResult addSpace(int code, int floor, String vehicleType, String occStatus, String resStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, resStatus, vehicleType);
        return manager.addSpace(space);
    }

    public SpaceResult editSpace(int code, int floor, String vehicleType, String occStatus, String resStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, resStatus, vehicleType);

        return manager.editSpace(space);
    }

    public SpaceResult removeSpace(int spaceId) {
        return manager.deleteSpace(spaceId);
    }

    public void getSpaceDetails(int spaceId) {
        //TODO: Implement
    }

    public void cancelReservationFromAdmin(int spaceId) {
        //TODO: implement
    }

    public boolean slotExists(int id) {
        return manager.slotExistsById(id);
    }
}

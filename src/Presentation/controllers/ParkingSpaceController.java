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

    public DaoResult addSpace(int code, int floor, String vehicleType, boolean occStatus, boolean resStatus) {
        ParkingSpace space = new ParkingSpace(code, floor, occStatus, resStatus, vehicleType);
        return parkingLotManager.addSpace(space);
    }

    public DaoResult editSpace(int code, int floor, String vehicleType) {
        boolean occupied = getSpaceDetails(code).isOccupied();
        boolean reserved = getSpaceDetails(code).isReserved();
        ParkingSpace space = new ParkingSpace(code, floor, occupied, reserved, vehicleType);

        return parkingLotManager.editSpace(space);
    }

    public DaoResult removeSpace(int spaceId) {
        ParkingSpace space = parkingLotManager.getSpaceDetails(spaceId);
        if (space == null) return DaoResult.NOT_FOUND;

        if (space.isOccupied()) {
            ParkingSpace alternative = parkingLotManager.findAlternativeSpace(space.getType(), spaceId);
            if (alternative == null) return DaoResult.CANNOT_REMOVE_OCCUPIED;
            parkingLotManager.moveVehicle(space, alternative);
        }

        if (space.isReserved()) {
            ParkingSpace alternative = parkingLotManager.findAlternativeSpace(space.getType(), spaceId);
            if (alternative != null) {
                reservationController.moveReservation(spaceId, alternative.getId());
            } else {
                reservationController.cancelReservationBySlot(spaceId);
            }
        }

        return parkingLotManager.deleteSpace(spaceId);
    }

    public ParkingSpace getSpaceDetails(int spaceId) {
        //TODO: Implement
        return parkingLotManager.getSpaceDetails(spaceId);
    }

    //TODO: this should be in reservation controller
//    public void cancelReservationFromAdmin(int spaceId) {
//        parkingLotManager.cancelReservationByAdmin(spaceId);
//    }

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
}
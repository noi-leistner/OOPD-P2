package Presentation.controllers;

import Business.DaoResult;
import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.ReservationManager;

public class ReservationController {

    private final ReservationManager manager;

    public ReservationController(ReservationManager manager) {
        this.manager = manager;
    }

    public void makeReservation(String vehicle_type, String license_plate) {
        //TODO: Implement
    }

    public DaoResult cancelReservation(int reservation_id) {
        //TODO: Implement
        return DaoResult.SUCCESS;
    }

    public void getUserReservations() {
        //TODO: Implement
    }

    public Reservation getReservationBySlotId(int id) {
        return manager.getReservationBySlot(id);
    }

    public void cancelReservationFromAdmin(int spaceId) {
        manager.cancelReservationByAdmin(spaceId);
    }
}

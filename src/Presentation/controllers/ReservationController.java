package Presentation.controllers;

import Business.DaoResult;
import Business.Entities.Reservation;
import Business.ReservationManager;

import java.util.List;

public class ReservationController {

    private final ReservationManager manager;

    public ReservationController(ReservationManager manager) {
        this.manager = manager;
    }

    public void makeReservation(String vehicle_type, String license_plate) {
        //TODO: Implement
    }

    //normal user cancel
    public DaoResult cancelReservation(int reservation_id) {
        //TODO: Implement
        return DaoResult.SUCCESS;
    }

    public List<Reservation> getAllReservations() {
        return manager.getAllReservations();
    }

    public void getUserReservations() {
        //TODO: Implement
    }

    public void deleteReservationByUserId(int id) {manager.deleteReservationByUserId(id);}

    public Reservation getReservationBySlotId(int id) {
        return manager.getReservationBySlot(id);
    }

    public void cancelReservationFromAdmin(int reservationId) {
        manager.cancelReservationByAdmin(reservationId);
    }

    public DaoResult editReservation(Reservation reservation) {
        return manager.editReservation(reservation);
    }

    public List<Reservation> getCancelledReservations(int id) {
        return manager.getCancelledReservations(id);
    }

    public void deleteCancelledReservations(int id) {
        manager.deleteCancelledReservations(id);
    }

    public void moveReservation(int fromSlotId, int toSlotId) {
        manager.moveReservation(fromSlotId, toSlotId);
    }

    public void cancelReservationBySlot(int slotId) {
        manager.cancelReservationBySlot(slotId);
    }

    public boolean reservationExistsForPlate(String plate) {
        return manager.reservationExistsForPlate(plate);
    }
}

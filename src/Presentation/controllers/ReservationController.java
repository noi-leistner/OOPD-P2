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

    public DaoResult makeReservation(Reservation reservation) {
        return manager.makeReservation(reservation);
    }

    //normal user cancel
    public DaoResult cancelReservation(int reservationId) {
        return manager.deleteReservation(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return manager.getAllReservations();
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        return manager.getReservationsByUserId(userId);
    }

    public void deleteReservationByUserId(int id) {manager.deleteReservationByUserId(id);}

    public Reservation getReservationBySlotId(int id) {
        return manager.getReservationBySlot(id);
    }
      
    public List<Reservation> getReservationsBySlotId(int id) {
        return manager.getReservationsBySlot(id);
    }

    public DaoResult cancelReservationFromAdmin(int reservationId) {
        return manager.cancelReservationByAdmin(reservationId);
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

    public void deleteExpiredReservations() {
        manager.deleteExpiredReservations();
    }
}

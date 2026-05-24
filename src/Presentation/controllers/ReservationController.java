package Presentation.controllers;

import Business.DaoResult;
import Business.Entities.Reservation;
import Business.ReservationManager;

import java.util.List;

/**
 * Controller for reservation operations.
 * Bridges the UI with ReservationManager for all reservation-related actions.
 */
public class ReservationController {

    private final ReservationManager manager;

    /**
     *  Creates a new ReservationController.
     *
     * @param manager the ReservationManager handling all reservation business logic
     * */
    public ReservationController(ReservationManager manager) {
        this.manager = manager;
    }

    /**
     * Creates a new reservation.
     *
     * @return SUCCESS, ALREADY_EXISTS if the slot is taken, or DATABASE_ERROR
     */
    public DaoResult makeReservation(Reservation reservation) {
        return manager.makeReservation(reservation);
    }

    /**
     * Permanently deletes a reservation (user-initiated cancellation).
     *
     * @param reservationId the reservation to delete
     *
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult cancelReservation(int reservationId) {
        return manager.deleteReservation(reservationId);
    }

    /** Returns all non-canceled reservations in the system. */
    public List<Reservation> getAllReservations() {
        return manager.getAllReservations();
    }

    /** Returns all reservations (including cancelled) for the given user. */
    public List<Reservation> getReservationsByUserId(int userId) {
        return manager.getReservationsByUserId(userId);
    }

    /** Deletes all reservations belonging to the given user. Used when deleting an account. */
    public void deleteReservationByUserId(int id) {manager.deleteReservationByUserId(id);}

    /** Returns all non-canceled reservations for the given parking slot. */
    public List<Reservation> getReservationsBySlotId(int id) {
        return manager.getReservationsBySlot(id);
    }

    /**
     * Cancels a reservation and flags it so the user is notified on next login (admin-initiated).
     *
     * @param reservationId the reservation to cancel
     *
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult cancelReservationWithNotification(int reservationId) {
        return manager.cancelReservationByAdmin(reservationId);
    }

    /**
     * Updates an existing reservation's slot and dates.
     *
     * @return SUCCESS, ALREADY_EXISTS if the slot is taken, NOT_FOUND, or DATABASE_ERROR
     */
    public DaoResult editReservation(Reservation reservation) {
        return manager.editReservation(reservation);
    }

    /** Returns all canceled reservations for the given user. */
    public List<Reservation> getCancelledReservations(int id) {
        return manager.getCancelledReservations(id);
    }

    /** Permanently deletes all canceled reservations for the given user. */
    public void deleteCancelledReservations(int id) {
        manager.deleteCancelledReservations(id);
    }

    /** Moves all reservations from one slot to another. Used when a space is deleted. */
    public void moveReservation(int fromSlotId, int toSlotId) {
        manager.moveReservation(fromSlotId, toSlotId);
    }

    /** Cancels all active reservations for the given slot. */
    public void cancelReservationBySlot(int slotId) {
        manager.cancelReservationBySlot(slotId);
    }

    /** Returns all reservations that are currently active (within their time window, not canceled). */
    public List<Reservation> getAllActiveReservations() {
        return manager.getAllActiveReservations();
    }

    /** Deletes all reservations whose end date has passed. */
    public void deleteExpiredReservations() {
        manager.deleteExpiredReservations();
    }

    /** Returns true if the given plate has an active reservation right now. */
    public boolean hasActiveReservationForPlate(String plate) {
        return manager.hasActiveReservationForPlate(plate);
    }

    /** Returns the currently active reservation for the given plate, or null if none. */
    public Reservation getActiveReservationForPlate(String plate) {
        return manager.getActiveReservationForPlate(plate);
    }
}

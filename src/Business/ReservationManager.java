package Business;

import Business.Entities.Reservation;
import Persistance.ReservationDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages reservation lifecycle for the parking syste,
 * --- Responsabilities: ---
 * - Create, edit, cancel, and delete reservations
 * - Filter reservations by user, slot, or cancellation status
 * - Validate whether a plate has an active reservation at entry time
 */
public class ReservationManager {
    /** DAO for reservation database operations */
    private final ReservationDAO reservationDao;
    /** Constructor */
    public ReservationManager (ReservationDAO reservationDao) {
        this.reservationDao = reservationDao;
    }
    /** Get a List<Reservation> from a user id*/
    public List<Reservation> getReservationsBySlot(int id) {
        return reservationDao.findReservationsBySlotId(id);
    }
    /**
     * Persists a new reservation.
     * @return SUCCESS, ALREADY_EXISTS, or DATABASE_ERROR
     */
    public DaoResult makeReservation(Reservation reservation) {
        return reservationDao.createReservation(reservation);
    }
    /**
     * Permanently deletes a reservation by ID.
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult deleteReservation(int reservationId) {
        return reservationDao.deleteReservation(reservationId);
    }
    /** Cancels and deletes all reservations associated with the given slot. */
    public void cancelReservationBySlot(int slotId) {
        List<Reservation> reservations = reservationDao.findReservationsBySlotId(slotId);
        for (Reservation reservation : reservations) {
            reservationDao.deleteReservation(reservation.getId());
        }
    }
    /**
     * Moves all reservations from one slot to another (e.g. after a slot edit).
     */
    public void moveReservation(int fromSlotId, int toSlotId) {
        List<Reservation> reservations = reservationDao.findReservationsBySlotId(fromSlotId);
        if (reservations.isEmpty()) return;

        for (Reservation reservation : reservations) {
            reservation.setParkingSlotId(toSlotId);
            reservationDao.editReservation(reservation);
        }
    }
    /** Returns all reservations in the system (admin use). */
    public List<Reservation> getAllReservations() {
        return reservationDao.getAllReservations();
    }
    /** Returns all reservations (active and cancelled) for the given user. */
    public List<Reservation> getReservationsByUserId(int userId) {
        return reservationDao.getReservationsByUserId(userId);
    }
    /**
     * Returns a user's reservations filtered by cancellation state.
     * @param cancelled true to return only cancelled; false for active
     */
    public List<Reservation> getUserReservations(int userId, boolean cancelled) {
        List<Reservation> reservations = reservationDao.getReservationsByUserId(userId);
        List<Reservation> list = new ArrayList<>();

        for (Reservation reservation : reservations) {
            if (reservation.isCancelled() && cancelled) {
                list.add(reservation);
            } else if (!reservation.isCancelled() && !cancelled) {
                list.add(reservation);
            }
        }
        return list;
    }
    /** Returns only cancelled reservations for the given user. */
    public List<Reservation> getCancelledReservations(int userId) {
        List<Reservation> cancelled = new ArrayList<>();
        List<Reservation> reservations = getUserReservations(userId, true);

        for (Reservation reservation : reservations) {
            if (reservation.isCancelled()) {
                cancelled.add(reservation);
            }
        }
        return cancelled;
    }
    /** Permanently deletes all cancelled reservations for the given user. */
    public void deleteCancelledReservations(int userId) {
        List<Reservation> cancelled = getCancelledReservations(userId);
        for (Reservation r : cancelled) {
            reservationDao.deleteReservation(r.getId());
        }
    }
    /**
     * Marks a reservation as cancelled (soft delete). Always returns SUCCESS.
     * @param reservationId the reservation to cancel
     */
    public DaoResult cancelReservationByAdmin(int reservationId) {
        reservationDao.cancelReservation(reservationId);
        return DaoResult.SUCCESS;
    }
    /**
     * Updates an existing reservation's fields.
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult editReservation(Reservation reservation) {
        return reservationDao.editReservation(reservation);
    }
    /** Returns true if any reservation (active or cancelled) exists for the plate. */
    public boolean reservationExistsForPlate(String licensePlate) {
        return reservationDao.findReservationByPlate(licensePlate) != null;
    }
    /** Deletes all reservations belonging to the given user (e.g. on account deletion). */
    public void deleteReservationByUserId(int id) {
        reservationDao.deleteReservationsByUserId(id);
    }
    /** Removes all reservations whose end time is in the past. */
    public void deleteExpiredReservations() {
        reservationDao.deleteExpiredReservations();
    }
    /**
     * Checks if a plate has a reservation whose time window includes right now.
     * @return true only if start ≤ now ≤ end; false if reservation not found
     */
    public boolean hasActiveReservationForPlate(String plate) {
        Reservation reservation = reservationDao.findReservationByPlate(plate);
        if (reservation == null) {
            return false;
        }
        Date now = new Date();

        return reservation.getStartDateTime().before(now) && reservation.getEndDateTime().after(now);
    }
    /**
     * Returns the active (currently ongoing) reservation for the given plate.
     * @return the reservation, or null if none is currently active
     */
    public Reservation getActiveReservationForPlate(String plate) {
        return reservationDao.getActiveReservationForPlate(plate);
    }
}

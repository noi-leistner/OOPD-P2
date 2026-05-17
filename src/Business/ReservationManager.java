package Business;

import Business.Entities.Reservation;
import Persistance.ReservationDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationManager {
    private final ReservationDAO reservationDao;

    public ReservationManager (ReservationDAO reservationDao) {
        this.reservationDao = reservationDao;
    }

    public Reservation getReservationBySlot(int id) {
        return reservationDao.findReservationBySlotId(id);
    }

    public void cancelReservationBySlot(int slotId) {
        Reservation reservation = reservationDao.findReservationBySlotId(slotId);
        if (reservation != null) {
            reservationDao.deleteReservation(reservation.getId());
        }
    }

    public void moveReservation(int fromSlotId, int toSlotId) {
        Reservation reservation = reservationDao.findReservationBySlotId(fromSlotId);
        if (reservation == null) return;

        reservation.setParking_slot_id(toSlotId);
        //TODO: make this in reservationDAO
        //reservationDao.updateReservation(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationDao.getAllReservations();
    }

    public Map<Integer,Integer> getOccupancyLastHour() {
        return reservationDao.getOccupancyLastHour();
    }

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

    public void deleteCancelledReservations(int userId) {
        List<Reservation> cancelled = getCancelledReservations(userId);
        for (Reservation r : cancelled) {
            reservationDao.deleteReservation(r.getId());
        }
    }

    public void cancelReservationByAdmin(int reservationId) {
        reservationDao.cancelReservation(reservationId);
    }

    public DaoResult editReservation(Reservation reservation) {
        return reservationDao.editReservation(reservation);
    }

    public boolean reservationExistsForPlate(String licensePlate) {
        return reservationDao.findReservationByPlate(licensePlate) != null;
    }
}

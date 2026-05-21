package Business;

import Business.Entities.Reservation;
import Persistance.ReservationDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationManager {
    private final ReservationDAO reservationDao;

    public ReservationManager (ReservationDAO reservationDao) {
        this.reservationDao = reservationDao;
    }

    public List<Reservation> getReservationsBySlot(int id) {
        return reservationDao.findReservationsBySlotId(id);
    }

    public DaoResult makeReservation(Reservation reservation) {
        return reservationDao.createReservation(reservation);
    }

    public DaoResult deleteReservation(int reservationId) {
        return reservationDao.deleteReservation(reservationId);
    }

    public void cancelReservationBySlot(int slotId) {
        List<Reservation> reservations = reservationDao.findReservationsBySlotId(slotId);
        for (Reservation reservation : reservations) {
            reservationDao.deleteReservation(reservation.getId());
        }
    }

    public void moveReservation(int fromSlotId, int toSlotId) {
        List<Reservation> reservations = reservationDao.findReservationsBySlotId(fromSlotId);
        if (reservations.isEmpty()) return;

        for (Reservation reservation : reservations) {
            reservation.setParkingSlotId(toSlotId);
            reservationDao.editReservation(reservation);
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationDao.getAllReservations();
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        return reservationDao.getReservationsByUserId(userId);
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

    public DaoResult cancelReservationByAdmin(int reservationId) {
        reservationDao.cancelReservation(reservationId);
        return DaoResult.SUCCESS;
    }

    public DaoResult editReservation(Reservation reservation) {
        return reservationDao.editReservation(reservation);
    }

    public boolean reservationExistsForPlate(String licensePlate) {
        return reservationDao.findReservationByPlate(licensePlate) != null;
    }

    public void deleteReservationByUserId(int id) {
        reservationDao.deleteReservationsByUserId(id);
    }
  
    public void deleteExpiredReservations() {
        reservationDao.deleteExpiredReservations();
    }

    public boolean hasActiveReservationForPlate(String plate) {
        Reservation reservation = reservationDao.findReservationByPlate(plate);
        if (reservation == null) {
            return false;
        }
        Date now = new Date();

        return reservation.getStartDateTime().before(now) && reservation.getEndDateTime().after(now);
    }

    public Reservation getActiveReservationForPlate(String plate) {
        return reservationDao.getActiveReservationForPlate(plate);
    }
}

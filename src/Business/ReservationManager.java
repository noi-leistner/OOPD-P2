package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Persistance.ReservationDAO;

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

    public Map<Integer,Integer> getOccupancyLastHour() {
        return reservationDao.getOccupancyLastHour();
    }

    public List<Reservation> getUserReservations(int userId) {
        //TODO: Implement
        return null;
    }

    public boolean hasCancelledReservations(int userId) {
        //List<Reservation> cancelled = reservationDao.getCancelledReservationsByUser(userId);
        //return !cancelled.isEmpty();
        return true;
    }

    public void clearCancelledNotifications(int userId) {
//        List<Reservation> cancelled = reservationDao.getCancelledReservationsByUser(userId);
//        for (Reservation r : cancelled) {
//            reservationDao.deleteReservationBySpaceId(r.getParking_slot_id());
//        }
    }

    public void cancelReservationByAdmin(int spaceId) {
        reservationDao.deleteReservation(spaceId);
        }
    }
}

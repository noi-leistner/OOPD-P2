package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Persistance.ReservationDAO;

public class ReservationManager {
    private ReservationDAO reservationDAO;

    public ReservationManager (ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public Reservation getReservationBySlot(int id) {
        return reservationDAO.findReservationBySlotId(id);
    }
}

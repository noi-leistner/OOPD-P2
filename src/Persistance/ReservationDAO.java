package Persistance;

import Business.Entities.Reservation;

import java.util.List;
import java.util.Map;

public interface ReservationDAO {
    void addReservation(Reservation reservation);
    void deleteReservation(int spotId);
    List<Reservation> getAllReservations();
    Map<Integer, Integer> getOccupancyLastHour();
    Reservation findReservationBySlotId(int id);
    Reservation findReservationByPlate(String licensePlate);

}

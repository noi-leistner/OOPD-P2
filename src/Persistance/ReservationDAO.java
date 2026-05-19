package Persistance;

import Business.DaoResult;
import Business.Entities.Reservation;

import java.util.List;
import java.util.Map;

public interface ReservationDAO {

    void addReservation(Reservation reservation);
    void deleteReservation(int reservationId);
    void cancelReservation(int reservationId);
    List<Reservation> getAllReservations();
    Reservation findReservationBySlotId(int id);
    DaoResult editReservation(Reservation reservation);
    List<Reservation> getReservationsByUserId(int userId);
    Reservation findReservationByPlate(String licensePlate);
    void deleteReservationsByUserId(int userId);
}
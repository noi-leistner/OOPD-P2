package Persistance;

import Business.DaoResult;
import Business.Entities.Reservation;

import java.util.List;
import java.util.Map;

public interface ReservationDAO {

    void addReservation(Reservation reservation);
    DaoResult deleteReservation(int reservationId);
    DaoResult createReservation(Reservation reservation);
    void cancelReservation(int reservationId);
    List<Reservation> getAllReservations();
    List<Reservation> findReservationsBySlotId(int id);
    DaoResult editReservation(Reservation reservation);
    List<Reservation> getReservationsByUserId(int userId);
    Reservation findReservationByPlate(String licensePlate);
    void deleteExpiredReservations();
}
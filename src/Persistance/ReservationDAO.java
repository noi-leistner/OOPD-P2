package Persistance;

import Business.Entities.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public interface ReservationDAO {

    void addReservation(Reservation reservation);
    void deleteReservation(int spotId);
    List<Reservation> getAllReservations();
    Map<Integer, Integer> getOccupancyLastHour();
    Reservation findReservationBySlotId(int id);
}

//h
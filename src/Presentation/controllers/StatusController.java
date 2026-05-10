package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.ParkingLotManager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StatusController {

    private final ParkingLotManager manager;

    public StatusController(ParkingLotManager manager) {
        this.manager = manager;
    }

    // Returns all parking spaces with the current state. Each space already contains:
    // id, floor, type, occupation_status, reservation_status.
    //        (License plate is fetched separately per space)
    public List<ParkingSpace> getParkingTableData(){
        return manager.getAllSpaces();
    }

    public Map<Integer, Integer> getOccupancyChartData(){
        return manager.getOccupancyLastDay();
    }
}
/*
/ To check if the map works:
 (1) -> Insert reservations by MySQL:
 - INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, date) VALUES
    (user_id*, '1111ABC', 3, NOW() - INTERVAL 55 MINUTE);

    *change user id by a real user
  (2) -> Run the query inside the function lastHourOccupancy:
    SELECT TIMESTAMPDIFF(MINUTE, date, NOW()) as minutes_ago, COUNT(*) as total
    FROM reservations
    WHERE date >= NOW() - INTERVAL 1 HOUR
    GROUP BY TIMESTAMPDIFF(MINUTE, date, NOW())
    ORDER BY minutes_ago ASC;
 */

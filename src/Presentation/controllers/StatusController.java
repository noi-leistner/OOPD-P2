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
        return manager.getOccupancyLastHour();
    }
}

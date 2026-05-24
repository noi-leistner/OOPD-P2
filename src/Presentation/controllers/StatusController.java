package Presentation.controllers;

import Business.Entities.ParkingSpace;
import Business.ParkingLogManager;
import Business.ParkingLotManager;
import Business.SimulationManager;
import Business.ReservationManager;
import Persistance.ParkingLogDAO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for the current parking lot status view.
 * Coordinates ParkingLotManager and SimulationManager to provide
 * real-time space data and control the traffic simulation.
 */
public class StatusController {

    private final ParkingLotManager parkingLotManager;
    private final SimulationManager simulationManager;

    /**
     * Creates new StatusController.
     *
     * @param parkingLotManager  provides current parking space data
     * @param simulationManager  manages the traffic simulation lifecycle
     */
    public StatusController(ParkingLotManager parkingLotManager,SimulationManager simulationManager) {
        this.parkingLotManager = parkingLotManager;
        this.simulationManager = simulationManager;
    }

    /**
     * Returns all parking spaces with their current state (id, floor, type,
     * occupation status and reservation status).
     */
    public List<ParkingSpace> getParkingTableData(){
        return parkingLotManager.getAllSpaces();
    }

    /** Starts the traffic simulation. */
    public void startSimulation() { simulationManager.start(); }

    /**
     * Sets a callback to be invoked on each simulation tick,
     * used to refresh the UI when the simulation updates the parking state.
     */
    public void setSimulationCallback(Runnable callback) {simulationManager.setOnTickCallBack(callback);}


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

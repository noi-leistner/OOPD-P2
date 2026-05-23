package Business;

import Persistance.ParkingLogDAO;

import java.util.Map;

/**
 * Manages parking entry/exit logging for the parking system.
 * --- Responsabilities: ---
 * - Record Vehicle entries and data exits with user context.
 * - Query occupancy history for dashboard reporting.
 * - Check if another vehicle is currently parked.
 */
public class ParkingLogManager {

    /** DAO for parking log database operations*/
    private final ParkingLogDAO parkingLogDao;

    /** Constructor */
    public ParkingLogManager(ParkingLogDAO parkingLogDao) {
        this.parkingLogDao = parkingLogDao;
    }
    /** Records a vehicle entry event.
     * @param spaceId -> Slot Id where the vehicle is parked.
     * @param licensePlate -> The vehicle's plate
     * @param userId -> The user performing the action (-1 for simulation)
     *  */
    public void logEntry(int spaceId, String licensePlate, int userId) {
        parkingLogDao.insertLog(spaceId, licensePlate, userId, "ENTRY");
    }

    /**
     * Records a vehicle exit event.
     * @param spaceId -> -> Slot Id where the vehicle is parked.
     * @param licensePlate -> The vehicle's plate
     * @param userId -> The user performing the action (-1 for simulation)
     */
    public void logExit(int spaceId, String licensePlate, int userId) {
        parkingLogDao.insertLog(spaceId, licensePlate, userId, "EXIT");
    }

    /**
     * Returns occupancy counts per slot for the last hour.
     * @return map of slotId -> entry count; empty for no activity.
     */
    public Map<Integer, Integer> getOccupancyLastHour() {
        return parkingLogDao.getOccupancyLastHour();
    }

    /**
     * Checks if a vehicle is currently parked (unmatched entry with no EXIT).
     * @param licensePlate -> licensePlate the vehicle's plate
     * @return true if the user vehicle has an open entry log.
     */
    public boolean isVehicleCurrentlyParked(String licensePlate) {
        return parkingLogDao.isVehicleCurrentlyParked(licensePlate);
    }
}

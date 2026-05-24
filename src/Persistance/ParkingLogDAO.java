package Persistance;

import java.util.Map;

/**
 * DAO interface for operations related to vehicle entry/exit logs.
 * Implemented by ParkingLogDAOSql.
 */
public interface ParkingLogDAO {

    void insertLog(int spaceId, String licensePlate, int userId, String action);
    Map<Integer, Integer> getOccupancyLastHour();
    boolean isVehicleCurrentlyParked(String licensePlate);
    boolean removeLog(int logId);
}
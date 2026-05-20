package Business;

import Persistance.ParkingLogDAO;

import java.util.Map;

public class ParkingLogManager {

    private final ParkingLogDAO parkingLogDao;

    public ParkingLogManager(ParkingLogDAO parkingLogDao) {
        this.parkingLogDao = parkingLogDao;
    }

    public void logEntry(int spaceId, String licensePlate, int userId) {
        parkingLogDao.insertLog(spaceId, licensePlate, userId, "ENTRY");
    }

    public void logExit(int spaceId, String licensePlate, int userId) {
        parkingLogDao.insertLog(spaceId, licensePlate, userId, "EXIT");
    }

    public Map<Integer, Integer> getOccupancyLastHour() {
        return parkingLogDao.getOccupancyLastHour();
    }

    public boolean isVehicleCurrentlyParked(String licensePlate) {
        return parkingLogDao.isVehicleCurrentlyParked(licensePlate);
    }
}

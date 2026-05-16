package Persistance;

public interface ParkingLogDAO {
    void insertLog(int spaceId, String licensePlate, int userId, String action);
}
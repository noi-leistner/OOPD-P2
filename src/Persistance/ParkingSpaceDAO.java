package Persistance;

import Business.Entities.ParkingSpace;

import java.util.List;
import java.util.Map;

public interface ParkingSpaceDAO {
    boolean addParkingSpace(ParkingSpace space);
    boolean updateParkingSpace(ParkingSpace space);
    boolean deleteParkingSpace(int id);
    String getParkedPlateAtSpace(int id);
    List<ParkingSpace> getAllParkingSpaces();
    boolean existsById(int id);
    ParkingSpace getParkingSpaceById(int id);
    List<ParkingSpace> getAvailableSpacesForType(String vehicleType);
    List<ParkingSpace> getSpotsByType(String type);
    ParkingSpace getFirstAvailableSpaceForType(String vehicleType);
    ParkingSpace getReservedSpaceByPlate(String licensePlate);
    ParkingSpace getOccupiedSpaceByPlate(String licensePlate);
    boolean occupySpace(int spaceId, String licensePlate);
    boolean vacateSpace(int spaceId);
    boolean vacateSpacesByUserId(int userId);
    List<ParkingSpace> findAvailableUnreserved();
    int getTotalUnreservedSpaces();
}

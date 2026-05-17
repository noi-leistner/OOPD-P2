package Persistance;

import Business.Entities.ParkingSpace;

import java.util.List;
import java.util.Map;

public interface ParkingSpaceDAO {
    boolean addParkingSpace(ParkingSpace space);

    boolean updateParkingSpace(ParkingSpace space);

    boolean deleteParkingSpace(int id);

    List<ParkingSpace> getAllParkingSpaces();

    boolean existsById(int id);

    ParkingSpace getParkingSpaceById(int id);

    List<ParkingSpace> getAvailableSpacesForType(String vehicleType);
    ParkingSpace getFirstAvailableSpaceForType(String vehicleType);
    ParkingSpace getReservedSpaceByPlate(String licensePlate);
    ParkingSpace getOccupiedSpaceByPlate(String licensePlate);
}

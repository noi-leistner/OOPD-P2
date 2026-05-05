package Persistance;

import Business.Entities.ParkingSpace;

import java.util.List;

public interface ParkingSpaceDAO {
    boolean addParkingSpace(ParkingSpace space);

    boolean updateParkingSpace(ParkingSpace space);

    boolean deleteParkingSpace(int id);

    List<ParkingSpace> getAllParkingSpaces();

    boolean existsById(int id);

    ParkingSpace getParkingSpaceById(int id);
}

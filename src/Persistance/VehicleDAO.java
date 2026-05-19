package Persistance;

import Business.Entities.Vehicle;

import java.util.List;

public interface VehicleDAO {
    Vehicle findByPlate(String licensePlate);
    List<Vehicle> findByUserId(int userId);
    boolean deleteByUserId(int userId);
}
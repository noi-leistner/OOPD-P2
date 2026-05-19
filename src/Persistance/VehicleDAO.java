package Persistance;

import Business.Entities.Vehicle;

public interface VehicleDAO {
    Vehicle findByPlate(String licensePlate);
    boolean deleteByUserId(int userId);
    boolean addVehicle(String licensePlate, int userId, String vehicleType);
    boolean existsByPlate(String licensePlate);
}
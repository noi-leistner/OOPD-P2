package Persistance;

import Business.Entities.Vehicle;

/**
 * DAO interface for operations of vehicles.
 * Implemented by VehicleDAOSql.
 */
public interface VehicleDAO {
    Vehicle findByPlate(String licensePlate);
    boolean deleteByUserId(int userId);
    boolean addVehicle(String licensePlate, int userId, String vehicleType);
    boolean existsByPlate(String licensePlate);
    void addVehicle(Vehicle vehicle);
    boolean insertSimulatedVehicle(String plate, String vehicleType);
    boolean deleteSimulatedVehicle(String plate);
}
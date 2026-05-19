package Persistance;

import Business.Entities.Vehicle;

public interface VehicleDAO {
    Vehicle findByPlate(String licensePlate);
    void addVehicle(Vehicle vehicle);
}
package Business;

import Business.Entities.Vehicle;
import Persistance.VehicleDAO;

public class VehicleManager {
    private final VehicleDAO vehicleDao;

    public VehicleManager (VehicleDAO vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public boolean vehiclePlateExists(String licensePlate) {
        return vehicleDao.findByPlate(licensePlate) != null;
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null && v.getUserId() == userId;
    }

    public String getVehicleType(String licensePlate) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null ? v.getType() : null;
    }

    public boolean existsForOtherUser(String plate, int userId) {
        Vehicle vehicle = vehicleDao.findByPlate(plate);
        return vehicle != null && vehicle.getUserId() != userId;
    }

    public Vehicle ensureVehicleExists(String plate, String type, int userId) {
        Vehicle existing = vehicleDao.findByPlate(plate);
        if (existing == null) {
            vehicleDao.addVehicle(new Vehicle(plate, userId, type));
            return vehicleDao.findByPlate(plate);
        }
        return existing;
    }
}

package Business;

import Business.Entities.Vehicle;
import Persistance.VehicleDAO;

/**
 * Manages vehicle ownership and registrations for the system.
 * -- Responsabilities: --
 * - Validate plate existence and user ownership.
 * - Register, retrieve, and delete vehicles.
 * - Insert/remove temporary records for simulated vehicles.
 */
public class VehicleManager {
    private final VehicleDAO vehicleDao;
    /** DAO for vehicle database operations */
    public VehicleManager(VehicleDAO vehicleDao) {
        this.vehicleDao = vehicleDao;
    }
    /** Constructor */
    public boolean vehiclePlateExists(String licensePlate) {
        return vehicleDao.findByPlate(licensePlate) != null;
    }
    /** Returns true if the plate exists and is owned by the given user. */
    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null && v.getUserId() == userId;
    }
    /**
     * Returns the type of the vehicle with the given plate.
     * @return "car" / "motorcycle", or null if the plate is not registered
     */
    public String getVehicleType(String licensePlate) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null ? v.getType() : null;
    }
    /**
     * Checks if a plate is registered to a different user.
     * Used to prevent plate conflicts during registration or edits.
     */
    public boolean existsForOtherUser(String plate, int userId) {
        Vehicle vehicle = vehicleDao.findByPlate(plate);
        return vehicle != null && vehicle.getUserId() != userId;
    }
    /**
     * Returns the vehicle for the plate if it exists; creates and persists it otherwise.
     * @return the existing or newly created vehicle
     */
    public Vehicle ensureVehicleExists(String plate, String type, int userId) {
        Vehicle existing = vehicleDao.findByPlate(plate);
        if (existing == null) {
            vehicleDao.addVehicle(new Vehicle(plate, userId, type));
            return vehicleDao.findByPlate(plate);
        }
        return existing;
    }
    /** Deletes all vehicles registered to the given user (e.g. on account deletion). */
    public void deleteByUserID(int id) {
        vehicleDao.deleteByUserId(id);
    }
    /**
     * Registers a new vehicle under the given user.
     * @return true if inserted successfully, false if a DB error occurred
     */
    public boolean registerVehicle(String licensePlate, int userId, String vehicleType) {
        return vehicleDao.addVehicle(licensePlate, userId, vehicleType);
    }
    /** Inserts a temporary vehicle record used exclusively during simulation. */
    public void insertSimulatedVehicle(String plate, String type) {
        vehicleDao.insertSimulatedVehicle(plate, type);
    }
    /** Removes the temporary simulation vehicle record after exit or failed entry. */
    public void deleteSimulatedVehicle(String plate) {
        vehicleDao.deleteSimulatedVehicle(plate);

    }
}

package Persistance;

import Business.Entities.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VehicleDAOSql implements VehicleDAO {

    private static final Logger log = Logger.getLogger(VehicleDAOSql.class.getName());

    @Override
    public Vehicle findByPlate(String licensePlate) {
        String sql = "SELECT license_plate, user_id, vehicle_type FROM vehicles WHERE UPPER(license_plate) = UPPER(?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                            rs.getString("license_plate"),
                            rs.getInt("user_id"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deleteByUserId(int userId) {
        String sql = "DELETE FROM vehicles WHERE user_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean addVehicle(String licensePlate, int userId, String vehicleType) {
        String sql = "INSERT INTO vehicles (license_plate, user_id, vehicle_type) VALUES (?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            stmt.setInt(2, userId);
            stmt.setString(3, vehicleType);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsByPlate(String licensePlate) {
        String sql = "SELECT 1 FROM vehicles WHERE UPPER(license_plate) = UPPER(?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
  
    @Override
    public void addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (license_plate, user_id, vehicle_type) VALUES (?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setInt(2, vehicle.getUserId());
            stmt.setString(3, vehicle.getType());
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public boolean insertSimulatedVehicle(String plate, String vehicleType) {
        String sql = "INSERT INTO vehicles (license_plate, user_id, vehicle_type) VALUES (?, -1, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            stmt.setString(2, vehicleType);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "insertSimulatedVehicle failed", e);
            return false;
        }
    }

    @Override
    public boolean deleteSimulatedVehicle(String plate) {
        String sql = "DELETE FROM vehicles WHERE license_plate = ? AND user_id = -1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "deleteSimulatedVehicle failed", e);
            return false;
        }
    }
}
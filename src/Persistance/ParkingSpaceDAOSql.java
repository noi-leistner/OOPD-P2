package Persistance;

import Business.Entities.ParkingSpace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingSpaceDAOSql implements ParkingSpaceDAO {

    // Helper to build a ParkingSpace from a ResultSet row
    private ParkingSpace mapRow(ResultSet rs) throws SQLException {
        return new ParkingSpace(
                rs.getInt("identifier"),
                rs.getInt("floor"),
                rs.getBoolean("occupation_status"),
                rs.getString("parked_license_plate"),
                rs.getString("vehicle_type")
        );
    }

    @Override
    public boolean addParkingSpace(ParkingSpace space) {
        String sql = "INSERT INTO parking_slots (identifier, floor, occupation_status, vehicle_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getId());
            stmt.setInt(2, space.getFloor());
            stmt.setBoolean(3, space.isOccupied());
            stmt.setString(4, space.getType());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateParkingSpace(ParkingSpace space) {
        String sql = "UPDATE parking_slots SET floor = ?, occupation_status = ?, vehicle_type = ? WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getFloor());
            stmt.setBoolean(2, space.isOccupied());
            stmt.setString(3, space.getType());
            stmt.setInt(4, space.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteParkingSpace(int id) {
        String sql = "DELETE FROM parking_slots WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ParkingSpace> getAllParkingSpaces() {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type FROM parking_slots";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                spaces.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spaces;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM parking_slots WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ParkingSpace getParkingSpaceById(int id) {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type FROM parking_slots WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type " +
                "FROM parking_slots WHERE vehicle_type = ? AND occupation_status = FALSE";
        List<ParkingSpace> spaces = new ArrayList<>();
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) spaces.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spaces;
    }

    @Override
    public ParkingSpace getFirstAvailableSpaceForType(String vehicleType) {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type " +
                "FROM parking_slots WHERE vehicle_type = ? AND occupation_status = FALSE LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ParkingSpace getReservedSpaceByPlate(String licensePlate) {
        String sql = "SELECT ps.identifier, ps.floor, ps.occupation_status, ps.parked_license_plate, ps.vehicle_type " +
                "FROM parking_slots ps " +
                "JOIN reservations r ON r.parking_slot_id = ps.identifier " +
                "WHERE r.vehicle_license_plate = ? AND r.is_cancelled = FALSE " +
                "LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ParkingSpace getOccupiedSpaceByPlate(String licensePlate) {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type " +
                "FROM parking_slots " +
                "WHERE parked_license_plate = ? AND occupation_status = TRUE LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean occupySpace(int spaceId, String licensePlate) {
        String sql = "UPDATE parking_slots SET occupation_status = TRUE, parked_license_plate = ? WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            stmt.setInt(2, spaceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean vacateSpace(int spaceId) {
        String sql = "UPDATE parking_slots SET occupation_status = FALSE, parked_license_plate = NULL WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spaceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean vacateSpacesByUserId(int userId) {
        String sql = "UPDATE parking_slots ps " +
                "JOIN vehicles v ON v.license_plate = ps.parked_license_plate " +
                "SET ps.occupation_status = FALSE, ps.parked_license_plate = NULL " +
                "WHERE v.user_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<ParkingSpace> findAvailableUnreserved() {
        String sql = "SELECT identifier, floor, occupation_status, vehicle type " +
                     "FROM parking_slots " +
                     "WHERE occupation_status = FALSE AND reservation_status = FALSE ";
        List<ParkingSpace> spaces = new ArrayList<>();
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) spaces.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Error reading config.json", e);
        }
        return spaces;
    }

}

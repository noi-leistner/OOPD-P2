package Persistance;

import Business.Entities.ParkingSpace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpaceDAOSql implements ParkingSpaceDAO {

    @Override
    public boolean addParkingSpace(ParkingSpace space) {
        String sql = "INSERT INTO parking_slots (identifier, floor, occupation_status, reservation_status, vehicle_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getId());
            stmt.setInt(2, space.getFloor());
            stmt.setBoolean(3, space.isOccupied());
            stmt.setBoolean(4, space.isReserved());
            stmt.setString(5, space.getType());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateParkingSpace(ParkingSpace space) {
        String sql = "UPDATE parking_slots SET floor = ?, occupation_status = ?, reservation_status = ?, vehicle_type = ? WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getFloor());
            stmt.setBoolean(2, space.isOccupied());
            stmt.setBoolean(3, space.isReserved());
            stmt.setString(4, space.getType());
            stmt.setInt(5, space.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

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
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ParkingSpace> getAllParkingSpaces() {
        String sql = "SELECT identifier, floor, occupation_status, reservation_status, vehicle_type FROM parking_slots";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                spaces.add(new ParkingSpace(
                        rs.getInt("identifier"),
                        rs.getInt("floor"),
                        rs.getBoolean("occupation_status"),
                        rs.getBoolean("reservation_status"),
                        rs.getString("vehicle_type")
                ));
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
        String sql = "SELECT identifier, floor, occupation_status, reservation_status, vehicle_type FROM parking_slots WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpace(
                            rs.getInt("identifier"),
                            rs.getInt("floor"),
                            rs.getBoolean("occupation_status"),
                            rs.getBoolean("reservation_status"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        String sql = "SELECT identifier, floor, occupation_status, reservation_status, vehicle_type " +
                "FROM parking_slots " +
                "WHERE vehicle_type = ? AND occupation_status = FALSE AND reservation_status = FALSE";
        List<ParkingSpace> spaces = new ArrayList<>();
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    spaces.add(new ParkingSpace(
                            rs.getInt("identifier"),
                            rs.getInt("floor"),
                            rs.getBoolean("occupation_status"),
                            rs.getBoolean("reservation_status"),
                            rs.getString("vehicle_type")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spaces;
    }

    @Override
    public ParkingSpace getFirstAvailableSpaceForType(String vehicleType) {
        String sql = "SELECT identifier, floor, occupation_status, reservation_status, vehicle_type " +
                "FROM parking_slots " +
                "WHERE vehicle_type = ? AND occupation_status = FALSE AND reservation_status = FALSE " +
                "LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpace(
                            rs.getInt("identifier"),
                            rs.getInt("floor"),
                            rs.getBoolean("occupation_status"),
                            rs.getBoolean("reservation_status"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ParkingSpace getReservedSpaceByPlate(String licensePlate) {
        String sql = "SELECT ps.identifier, ps.floor, ps.occupation_status, ps.reservation_status, ps.vehicle_type " +
                "FROM parking_slots ps " +
                "JOIN reservations r ON r.parking_slot_id = ps.identifier " +
                "WHERE r.vehicle_license_plate = ? " +
                "LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpace(
                            rs.getInt("identifier"),
                            rs.getInt("floor"),
                            rs.getBoolean("occupation_status"),
                            rs.getBoolean("reservation_status"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ParkingSpace getOccupiedSpaceByPlate(String licensePlate) {
        String sql = "SELECT identifier, floor, occupation_status, reservation_status, vehicle_type " +
                "FROM parking_slots " +
                "WHERE parked_license_plate = ? AND occupation_status = TRUE " +
                "LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpace(
                            rs.getInt("identifier"),
                            rs.getInt("floor"),
                            rs.getBoolean("occupation_status"),
                            rs.getBoolean("reservation_status"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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

}

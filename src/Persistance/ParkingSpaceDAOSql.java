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
        String sql = "INSERT INTO parking_slots (id, floor, occupation_status, reservation_status, vehicle_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getId());
            stmt.setInt(2, space.getFloor());
            stmt.setString(3, space.getCurrentStatus());
            stmt.setString(4, space.getReservationStatus());
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
        String sql = "UPDATE parking_slots SET floor = ?, occupation_status = ?, reservation_status = ?, vehicle_type = ? WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, space.getFloor());
            stmt.setString(2, space.getCurrentStatus());
            stmt.setString(3, space.getReservationStatus());
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
        String sql = "DELETE FROM parking_slots WHERE id = ?";
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
        String sql = "SELECT id, floor, occupation_status, reservation_status, vehicle_type FROM parking_slots";
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                spaces.add(new ParkingSpace(
                        rs.getInt("id"),
                        rs.getInt("floor"),
                        rs.getString("occupation_status"),
                        rs.getString("reservation_status"),
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
        String sql = "SELECT 1 FROM parking_slots WHERE id = ?";
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
        String sql = "SELECT id, floor, occupation_status, reservation_status, vehicle_type FROM parking_slots WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ParkingSpace(
                            rs.getInt("id"),
                            rs.getInt("floor"),
                            rs.getString("occupation_status"),
                            rs.getString("reservation_status"),
                            rs.getString("vehicle_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

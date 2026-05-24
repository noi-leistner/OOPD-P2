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

/**
 * SQL implementation of ParkingSpaceDAO.
 * Handles all database operations for parking spaces.
 */
public class ParkingSpaceDAOSql implements ParkingSpaceDAO {

    private static final Logger log = Logger.getLogger(ParkingSpaceDAOSql.class.getName());

    /** Builds a ParkingSpace object from the current row of a ResultSet. */
    private ParkingSpace mapRow(ResultSet rs) throws SQLException {
        return new ParkingSpace(
                rs.getInt("identifier"),
                rs.getInt("floor"),
                rs.getBoolean("occupation_status"),
                rs.getString("parked_license_plate"),
                rs.getString("vehicle_type")
        );
    }

    /**
     * Inserts a new parking space into the database.
     *
     * @param space the parking space to add
     *
     * @return true if successful, false otherwise
     */
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
            log.log(Level.SEVERE, "Error adding parking space in DAO", e);
        }
        return false;
    }

    /**
     * Updates an existing parking space's floor, occupation status and vehicle type.
     *
     * @param space the parking space with updated values
     *
     * @return true if a row was updated, false otherwise
     */
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
            log.log(Level.SEVERE, "Error updating parking space in DAO", e);
            return false;
        }
    }

    /**
     * Deletes a parking space by its identifier.
     *
     * @param id the space identifier
     *
     * @return true if a row was deleted, false otherwise
     */
    @Override
    public boolean deleteParkingSpace(int id) {
        String sql = "DELETE FROM parking_slots WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error deleting parking space in DAO", e);
            return false;
        }
    }

    /**
     * Returns all parking spaces in the database.
     *
     * @return list of all parking spaces
     */
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
            log.log(Level.SEVERE, "Error getting all parking spaces in DAO", e);
        }
        return spaces;
    }

    /**
     * Checks whether a parking space with the given ID exists.
     *
     * @param id the space identifier
     *
     * @return true if it exists, false otherwise
     */
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
            log.log(Level.SEVERE, "Error getting if exists parking space in DAO", e);
            return false;
        }
    }

    /**
     * Returns a single parking space by its identifier.
     *
     * @param id the space identifier
     *
     * @return the matching ParkingSpace, or null if not found
     */
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
            log.log(Level.SEVERE, "Error getting space by Id in DAO");
        }
        return null;
    }

    /**
     * Returns all unoccupied spaces that match the given vehicle type.
     *
     * @param vehicleType the type of vehicle (e.g. "car", "motorcycle")
     *
     * @return list of available matching spaces
     */
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
            log.log(Level.SEVERE, "Error getting available parking spaces in DAO", e);
        }
        return spaces;
    }

    /**
     * Returns all spaces (occupied or not) that match the given vehicle type.
     *
     * @param type the vehicle type
     *
     * @return list of matching spaces
     */
    @Override
    public List<ParkingSpace> getSpotsByType(String type) {
        String sql = "SELECT identifier, floor, occupation_status, parked_license_plate, vehicle_type " +
                "FROM parking_slots WHERE vehicle_type = ?";
        List<ParkingSpace> spaces = new ArrayList<>();
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) spaces.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error getting available parking spaces in DAO", e);
        }
        return spaces;
    }

    /**
     * Returns the first unoccupied space for the given vehicle type.
     *
     * @param vehicleType the vehicle type
     *
     * @return the first available space, or null if none exist
     */
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
            log.log(Level.SEVERE, "Error getting available parking spaces in DAO", e);
        }
        return null;
    }

    /**
     * Returns the license plate of the vehicle currently parked at a given space,
     * based on the most recent ENTRY log.
     *
     * @param id the space identifier
     *
     * @return the license plate, or null if no vehicle is parked there
     */
    @Override
    public String getParkedPlateAtSpace(int id) {
        String sql = "SELECT license_plate FROM parking_log " +
                "WHERE parking_slot_id = ? AND action = 'ENTRY' " +
                "ORDER BY timestamp DESC LIMIT 1";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("license_plate");
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the reserved space associated with a given license plate.
     *
     * @param licensePlate the vehicle's license plate
     *
     * @return the reserved ParkingSpace, or null if no active reservation exists
     */
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
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the space currently occupied by a given license plate.
     *
     * @param licensePlate the vehicle's license plate
     *
     * @return the occupied ParkingSpace, or null if not found
     */
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
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Marks a space as occupied by a given license plate.
     *
     * @param spaceId      the space to occupy
     * @param licensePlate the vehicle's license plate
     *
     * @return true if successful, false otherwise
     */
    @Override
    public boolean occupySpace(int spaceId, String licensePlate) {
        String sql = "UPDATE parking_slots SET occupation_status = TRUE, parked_license_plate = ? WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            stmt.setInt(2, spaceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Marks a space as vacant and clears its license plate.
     *
     * @param spaceId the space to vacate
     *
     * @return true if successful, false otherwise
     */
    @Override
    public boolean vacateSpace(int spaceId) {
        String sql = "UPDATE parking_slots SET occupation_status = FALSE, parked_license_plate = NULL WHERE identifier = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spaceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Vacates all spaces currently occupied by vehicles belonging to a given user.
     * Used when deleting a user account.
     *
     * @param userId the user's ID
     *
     * @return true if successful, false otherwise
     */
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
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Returns the total number of spaces that have no active reservation.
     *
     * @return count of unreserved spaces
     */
    @Override
    public int getTotalUnreservedSpaces() {
        String sql = "SELECT COUNT(*) FROM parking_slots " +
                "WHERE identifier NOT IN (" +
                "  SELECT parking_slot_id FROM reservations WHERE is_cancelled = FALSE" +
                ")";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            Logger.getLogger(ConfigDAO.class.getName()).log(Level.SEVERE, "Get total unreserved spaces failed", e);
        }
        return 0;
    }

}

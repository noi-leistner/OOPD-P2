package Persistance;

import Business.DaoResult;
import Business.Entities.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationDAOSql implements ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAOSql.class.getName());

    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("vehicle_license_plate"),
                rs.getInt("parking_slot_id"),
                rs.getDate("date"),
                rs.getBoolean("is_cancelled")
        );
    }

    @Override
    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, date) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUser_id());
            stmt.setString(2, reservation.getVehiclePlate());
            stmt.setInt(3, reservation.getParking_slot_id());
            stmt.setDate(4, new java.sql.Date(reservation.getDate().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void deleteReservation(int spotId) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void cancelReservation(int reservationId) {
        String sql = "UPDATE reservations SET is_cancelled = TRUE WHERE id = ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE is_cancelled = FALSE";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }




    @Override
    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return list;
    }

    @Override
    public Reservation findReservationBySlotId(int slotId) {
        String sql = "SELECT * FROM reservations WHERE parking_slot_id = ? AND is_cancelled = FALSE";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
    @Override
    public Reservation findReservationByPlate(String plate) {
        String sql = "SELECT * FROM reservations WHERE parking_slot_id = ? AND is_cancelled = FALSE";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public DaoResult editReservation(Reservation reservation) {
        String sql = "UPDATE reservations " +
                "SET parking_slot_id = ?, date = ? " +
                "WHERE id = ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getParking_slot_id());
            stmt.setDate(2, new java.sql.Date(reservation.getDate().getTime()));
            stmt.setInt(3, reservation.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return DaoResult.SUCCESS;
            } else {
                return DaoResult.NOT_FOUND;
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
        }
    }
}
package Persistance;

import Business.DaoResult;
import Business.Entities.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                rs.getTimestamp("start_date"),
                rs.getTimestamp("end_date"),
                rs.getBoolean("is_cancelled")
        );
    }

    @Override
    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUser_id());
            stmt.setString(2, reservation.getVehiclePlate());
            stmt.setInt(3, reservation.getParkingSlotId());
            stmt.setTimestamp(4, new java.sql.Timestamp(reservation.getStartDateTime().getTime()));
            stmt.setTimestamp(5, new java.sql.Timestamp(reservation.getEndDateTime().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public DaoResult deleteReservation(int reservationId) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
            return DaoResult.SUCCESS;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
        }
    }

    @Override
    public DaoResult createReservation(Reservation reservation) {
        // Check for overlapping reservations on the same slot
        String checkSql = "SELECT 1 FROM reservations " +
                "WHERE parking_slot_id = ? " +
                "AND is_cancelled = FALSE " +
                "AND start_date < ? " +
                "AND end_date > ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, reservation.getParkingSlotId());
            checkStmt.setTimestamp(2, new java.sql.Timestamp(reservation.getEndDateTime().getTime()));
            checkStmt.setTimestamp(3, new java.sql.Timestamp(reservation.getStartDateTime().getTime()));

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return DaoResult.ALREADY_EXISTS;
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
        }

        String sql = "INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUser_id());
            stmt.setString(2, reservation.getVehiclePlate());
            stmt.setInt(3, reservation.getParkingSlotId());
            stmt.setTimestamp(4, new java.sql.Timestamp(reservation.getStartDateTime().getTime()));
            stmt.setTimestamp(5, new java.sql.Timestamp(reservation.getEndDateTime().getTime()));
            stmt.executeUpdate();
            return DaoResult.SUCCESS;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
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
    public List<Reservation> findReservationsBySlotId(int slotId) {
        String sql = "SELECT * FROM reservations WHERE parking_slot_id = ? AND is_cancelled = FALSE";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return reservations;
    }

    @Override
    public DaoResult editReservation(Reservation reservation) {
        String checkSql = "SELECT 1 FROM reservations " +
                "WHERE parking_slot_id = ? " +
                "AND is_cancelled = FALSE " +
                "AND id != ? " +
                "AND start_date < ? " +
                "AND end_date > ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, reservation.getParkingSlotId());
            checkStmt.setInt(2, reservation.getId());
            checkStmt.setTimestamp(3, new java.sql.Timestamp(reservation.getEndDateTime().getTime()));
            checkStmt.setTimestamp(4, new java.sql.Timestamp(reservation.getStartDateTime().getTime()));

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return DaoResult.ALREADY_EXISTS;
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
        }

        String sql = "UPDATE reservations " +
                "SET parking_slot_id = ?, start_date = ?, end_date = ? " +
                "WHERE id = ?";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getParkingSlotId());
            stmt.setTimestamp(2, new java.sql.Timestamp(reservation.getStartDateTime().getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(reservation.getEndDateTime().getTime()));
            stmt.setInt(4, reservation.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? DaoResult.SUCCESS : DaoResult.NOT_FOUND;

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return DaoResult.DATABASE_ERROR;
        }
    }

    @Override
    public Reservation findReservationByPlate(String licensePlate) {
        String sql = "SELECT * FROM reservations WHERE vehicle_license_plate = ? AND is_cancelled = 0";
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

    @Override
    public void deleteReservationsByUserId(int userId) {
        String sql = "DELETE FROM reservations WHERE user_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void deleteExpiredReservations() {
        String sql = "DELETE FROM reservations WHERE end_date < NOW()";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public Reservation getActiveReservationForPlate(String plate) {
        String sql = "SELECT * FROM reservations " +
                "WHERE vehicle_license_plate = ? " +
                "AND is_cancelled = FALSE " +
                "AND start_date <= NOW() " +
                "AND end_date >= NOW()";

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
}
package Persistance;

import Business.Entities.Reservation;

import java.util.Map;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReservationDAOSql implements ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAO.class.getName());

    public Reservation getReservationBySpaceId(int spaceId) {
        String sql = "SELECT * FROM reservations WHERE parking_slot_id = ? LIMIT 1";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spaceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public void markCancelledByAdmin(int reservationId) {
        String sql = "UPDATE reservations SET cancelled_by_admin = TRUE WHERE id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void deleteReservationBySpaceId(int spaceId) {
        String sql = "DELETE FROM reservations WHERE parking_slot_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spaceId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public List<Reservation> getCancelledReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND cancelled_by_admin = TRUE";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}

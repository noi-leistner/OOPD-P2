package Persistance;

import Business.Entities.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public class ReservationDAOSql implements ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAOSql.class.getName());

    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, date) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUser_id());
            stmt.setString(2, reservation.getVehiclePlate());
            stmt.setInt(3, reservation.getParking_slot_id());
            stmt.setDate(4, new java.sql.Date(reservation.getDate().getTime()));
            stmt.executeQuery();
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void deleteReservation(int spotId) {
        String sql = "DELETE FROM reservations WHERE spot_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, spotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    public Map<Integer, Integer> getOccupancyLastHour() {
        Map<Integer, Integer> result = new HashMap<>();
        String sql = "SELECT TIMESTAMPDIFF(MINUTE, date, NOW()) as minutes_ago, Count(*) as total" +
                     "FROM reservations" + "WHERE date >= NOW() - INTERVAL 1 HOUR" +
                     "GROUPED BY TIMESTAMPDIFF(MINUTE, date, NOW())" + "ORDER BY minutes_ago DESC";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                int minutes = rs.getInt("minutes_ago");
                int count = rs.getInt("total");
                result.put(minutes, count);
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result; // {1->8, 2->7, ..., 59->23}
    }


    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("vehicle_license_plate"),
                rs.getInt("parking_slot_id"),
                rs.getDate("date")
        );
    }

    public Reservation findReservationBySlotId(int slotId) {
        String sql = "SELECT * FROM reservations WHERE parking_slot_id = ?";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}

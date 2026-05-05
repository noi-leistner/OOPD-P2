package Persistance;

import Business.Entities.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public class ReservationDAO {

    private static final Logger log = Logger.getLogger(ReservationDAO.class.getName());

    void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, vehicle_license_plate, parking_slot_id, date) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUser_id());
            stmt.setString(2, reservation.getVehicle_license_plate());
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

    List<Reservation> getAllReservations() {
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


    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setUser_id(rs.getInt("user_id"));
        r.setVehicle_license_plate(rs.getString("vehicle_license_plate"));
        r.setParking_slot_id(rs.getInt("parking_slot_id"));
        r.setDate(rs.getDate("date"));
        return r;
    }
}

package Persistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParkingLogDAOSql implements ParkingLogDAO {

    @Override
    public void insertLog(int spaceId, String licensePlate, int userId, String action) {
        String sql = "INSERT INTO parking_log (parking_slot_id, license_plate, user_id, action, timestamp) " +
                "VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, spaceId);
            stmt.setString(2, licensePlate);
            stmt.setInt(3, userId);
            stmt.setString(4, action);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
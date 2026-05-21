package Persistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    @Override
    public Map<Integer, Integer> getOccupancyLastHour() {
        Map<Integer, Integer> netByMinute = new LinkedHashMap<>();
        String sql = """
        SELECT FLOOR(TIMESTAMPDIFF(SECOND, timestamp, NOW()) / 60) AS minutes_ago,
               SUM(CASE WHEN action='ENTRY' THEN 1 WHEN action='EXIT' THEN -1 ELSE 0 END) AS net
        FROM parking_log
        WHERE timestamp >= NOW() - INTERVAL 1 HOUR
        GROUP BY minutes_ago
        ORDER BY minutes_ago DESC
        """;
        try (Connection c = ConfigDAO.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                netByMinute.put(rs.getInt("minutes_ago"), rs.getInt("net"));
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getOccupancyLastHour failed", e);
        }

        // Convert net-per-minute → cumulative running total (walk 59→0)
        Map<Integer, Integer> result = new LinkedHashMap<>();
        int running = 0;
        for (int m = 59; m >= 0; m--) {
            running += netByMinute.getOrDefault(m, 0);
            result.put(m, Math.max(running, 0));
        }
        return result;
    }


}
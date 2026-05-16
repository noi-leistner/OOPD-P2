package Persistance;

import Business.Entities.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleDAOSql implements VehicleDAO {

    @Override
    public Vehicle findByPlate(String licensePlate) {
        String sql = "SELECT license_plate, user_id, vehicle_type FROM vehicles WHERE UPPER(license_plate) = UPPER(?)";
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Vehicle(
                            rs.getString("license_plate"),
                            rs.getInt("user_id"),
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
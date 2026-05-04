package Persistance;

import Business.Entities.ParkingSpace;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingSpaceDAO {

    private static final Logger log = Logger.getLogger(ParkingSpaceDAO.class.getName());

    // SQL Queries
    private static final String INSERT =
            "INSERT INTO parking_slots (occupation_status, reservation_status, vehicle_type) "+
                    "Values (?,?,?)";

    private static final String UPDATE =
            "UPDATE parking_slots " +
            "SET occupation_status=?, reservation_status=?, vehicle_type=? " +
            "WHERE identifier=? ";

    private static final String DELETE =
            "DELETE FROM parking_slots WHERE identifier=? ";

    private static final String SELECT_ALL =
            "SELECT * FROM parking_slots ";

    private static final String SELECT_BY_CODE =
            "SELECT * FROM parking_slots WHERE identifier=? ";

    void addParkingSpace(ParkingSpace space) {
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            //stmt.setInt(1, space.getCode());
            //stmt.setInt(2, space.getFloor());
            stmt.setInt(1, space.getCurrentStatus());
            stmt.setInt(2, space.getReservationStatus());
            stmt.setString(3, space.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void editParkingSpace(ParkingSpace space) {
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            //stmt.setInt(1, space.getCode());
            //stmt.setInt(2, space.getFloor());
            stmt.setInt(1, space.getCurrentStatus());
            stmt.setInt(2, space.getReservationStatus());
            stmt.setString(3, space.getType());
            stmt.setInt(4, space.getSpaceId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void removeParkingSpace(ParkingSpace space) {
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setInt(1, space.getSpaceId());
            stmt.executeUpdate();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<ParkingSpace> getAllParkingSpaces() {
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection conn = ConfigDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
            ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                spaces.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spaces;
    }

    ParkingSpace getParkingSpaceById(int id) {
        try (Connection conn = ConfigDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CODE)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // IF we actualize the tables and add floor and code, we have to change the queries also.
    private ParkingSpace mapRow(ResultSet rs) throws SQLException {
        ParkingSpace space = new ParkingSpace();
        space.setSpaceId(rs.getInt("identifier"));
        //space.setCode(rs.getInt("code"));
        //space.setFloor(rs.getInt("floor"));
        space.setCurrentStatus(rs.getInt("occupation_status"));
        space.setReservationStatus(rs.getInt("reservation_status"));
        space.setType(rs.getString("vehicle_type"));
        return space;
    }
}

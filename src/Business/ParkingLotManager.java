package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Business.Entities.Vehicle;
import Persistance.*;

import java.util.List;
import java.util.Map;

public class ParkingLotManager {

    private ParkingSpaceDAO parkingSpaceDao;
    private ReservationDAO reservationDao;
    private VehicleDAO vehicleDao;
    private ParkingLogDAO parkingLogDao;

    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao, ReservationDAO reservationDao, VehicleDAO vehicleDao, ParkingLogDAO parkingLogDao) {
        this.parkingSpaceDao = parkingSpaceDao;
        this.reservationDao = reservationDao;
        this.vehicleDao = vehicleDao;
        this.parkingLogDao = parkingLogDao;
    }

    public Reservation getReservationForSpace(int spaceId) {
        return reservationDao.findReservationBySlotId(spaceId);
    }

    public boolean vehiclePlateExists(String licensePlate) {
        return vehicleDao.findByPlate(licensePlate) != null;
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null && v.getUserId() == userId;
    }

    public void logParkingAction(int spaceId, String licensePlate, int userId, String action) {
        parkingLogDao.insertLog(spaceId, licensePlate, userId, action);
    }

    public DaoResult addSpace(ParkingSpace space) {
        if (space == null) return DaoResult.DATABASE_ERROR;
        if (parkingSpaceDao.existsById(space.getId())) return DaoResult.ALREADY_EXISTS;

        boolean saved = parkingSpaceDao.addParkingSpace(space);
        return saved ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }

    public DaoResult editSpace(ParkingSpace space) {
        if (space == null) return DaoResult.DATABASE_ERROR;

        boolean updated = parkingSpaceDao.updateParkingSpace(space);
        return updated ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }

    public DaoResult deleteSpace(int spaceId) {
        boolean deleted = parkingSpaceDao.deleteParkingSpace(spaceId);
        return deleted ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }


    public ParkingSpace getSpaceDetails(int spaceId) {
        return parkingSpaceDao.getParkingSpaceById(spaceId);
    }

    public List<ParkingSpace> getAllSpaces() {
        return parkingSpaceDao.getAllParkingSpaces();
    }

    public boolean slotExistById(int slotId) {
        return parkingSpaceDao.existsById(slotId);
    }

    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return parkingSpaceDao.getAvailableSpacesForType(vehicleType);
    }

    public ParkingSpace enterWithReservation(String licensePlate, int userId) {
        ParkingSpace space = parkingSpaceDao.getReservedSpaceByPlate(licensePlate);
        if (space == null) return null;
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(space.getId());
        if (fresh == null || fresh.isOccupied()) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).occupySpace(space.getId(), licensePlate);
        if (ok) {
            parkingLogDao.insertLog(space.getId(), licensePlate, userId, "ENTRY");
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public ParkingSpace enterWithoutReservation(String licensePlate, int spaceId, int userId) {
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(spaceId);
        if (fresh == null || fresh.isOccupied()) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).occupySpace(spaceId, licensePlate);
        if (ok) {
            parkingLogDao.insertLog(spaceId, licensePlate, userId, "ENTRY");
            return parkingSpaceDao.getParkingSpaceById(spaceId);
        }
        return null;
    }

    public ParkingSpace exit(String licensePlate, int userId) {
        ParkingSpace space = parkingSpaceDao.getOccupiedSpaceByPlate(licensePlate);
        if (space == null) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).vacateSpace(space.getId());
        if (ok) {
            parkingLogDao.insertLog(space.getId(), licensePlate, userId, "EXIT");
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public String getVehicleType(String licensePlate) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null ? v.getType() : null;
    }

    public boolean reservationExistsForPlate(String licensePlate) {
        return reservationDao.findReservationByPlate(licensePlate) != null;
    }

    public Reservation reserve(String licensePlate, String vehicleType, int spaceId) {
        //TODO: Implement
        return null;
    }

    public void cancelReservation(int reservationId, String licensePlate){
        //TODO: Implement
    }

    public void cancelReservationByAdmin(int spaceId) {
        reservationDao.deleteReservation(spaceId);

        ParkingSpace space = parkingSpaceDao.getParkingSpaceById(spaceId);
        if (space != null) {
            ParkingSpace updated = new ParkingSpace(
                    getSpaceDetails(spaceId).getId(),
                    getSpaceDetails(spaceId).getFloor(),
                    false,
                    false,
                    getSpaceDetails(spaceId).getType()
            );
            parkingSpaceDao.updateParkingSpace(updated);

        }
    }

    public List<Reservation> getUserReservations(int userId) {
        //TODO: Implement
        return null;
    }

    public Map<Integer,Integer> getOccupancyLastHour() {
        return reservationDao.getOccupancyLastHour();
    }

    // TODO: maybe not need this function
    public boolean slotExistsById(int id) {
        return parkingSpaceDao.existsById(id);
    }
}

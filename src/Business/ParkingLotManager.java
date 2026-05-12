package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Persistance.ParkingSpaceDAO;
import Persistance.ParkingSpaceDAOSql;
import Persistance.ReservationDAO;
import Persistance.VehicleDAO;

import java.util.List;
import java.util.Map;

public class ParkingLotManager {

    private ParkingSpaceDAO parkingSpaceDao;
    private ReservationDAO reservationDao;
    private VehicleDAO vehicleDao;

    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao, ReservationDAO reservationDao, VehicleDAO vehicleDao) {
        this.parkingSpaceDao = parkingSpaceDao;
        this.reservationDao = reservationDao;
        this.vehicleDao = vehicleDao;
    }

    public Reservation getReservationForSpace(int spaceId) {
        return reservationDao.findReservationBySlotId(spaceId);
    }

    public boolean vehiclePlateExists(String licensePlate) {
        return vehicleDao.findByPlate(licensePlate) != null;
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

    public ParkingSpace enterWithReservation(String licensePlate) {
        ParkingSpace space = parkingSpaceDao.getReservedSpaceByPlate(licensePlate);
        if (space == null) return null;
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(space.getId());
        if (fresh == null || fresh.isOccupied()) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).occupySpace(space.getId(), licensePlate);
        return ok ? parkingSpaceDao.getParkingSpaceById(space.getId()) : null;
    }

    public ParkingSpace enterWithoutReservation(String licensePlate, int spaceId) {
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(spaceId);
        System.out.println("=== enterWithoutReservation ===");
        System.out.println("Space ID: " + spaceId);
        System.out.println("Fresh space null? " + (fresh == null));
        if (fresh != null) {
            System.out.println("Is occupied: " + fresh.isOccupied());
            System.out.println("Is reserved: " + fresh.isReserved());
        }
        if (fresh == null || fresh.isOccupied()) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).occupySpace(spaceId, licensePlate);
        System.out.println("occupySpace result: " + ok);
        return ok ? parkingSpaceDao.getParkingSpaceById(spaceId) : null;
    }

    public ParkingSpace exit(String licensePlate){
        ParkingSpace space = parkingSpaceDao.getOccupiedSpaceByPlate(licensePlate);
        if (space == null) return null;
        boolean ok = ((ParkingSpaceDAOSql) parkingSpaceDao).vacateSpace(space.getId());
        return ok ? parkingSpaceDao.getParkingSpaceById(space.getId()) : null;
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

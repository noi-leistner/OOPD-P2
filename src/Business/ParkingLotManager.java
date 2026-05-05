package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Persistance.ParkingSpaceDAO;
import Persistance.ReservationDAO;

import java.util.List;
import java.util.Map;

public class ParkingLotManager {

    private ParkingSpaceDAO parkingSpaceDao;
    private ReservationDAO reservationDao;

    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao, ReservationDAO reservationDao) {
        this.parkingSpaceDao = parkingSpaceDao;
        this.reservationDao = reservationDao;
    }

    public ParkingLotManager(ReservationDAO reservationDao) {

    }

    public SpaceResult addSpace(ParkingSpace space) {
        if (space == null) return SpaceResult.DATABASE_ERROR;
        if (parkingSpaceDao.existsById(space.getId())) return SpaceResult.ALREADY_EXISTS;

        boolean saved = parkingSpaceDao.addParkingSpace(space);
        return saved ? SpaceResult.SUCCESS : SpaceResult.DATABASE_ERROR;
    }

    public SpaceResult editSpace(ParkingSpace space) {
        if (space == null) return SpaceResult.DATABASE_ERROR;

        boolean updated = parkingSpaceDao.updateParkingSpace(space);
        return updated ? SpaceResult.SUCCESS : SpaceResult.DATABASE_ERROR;
    }

    public SpaceResult deleteSpace(int spaceId) {
        boolean deleted = parkingSpaceDao.deleteParkingSpace(spaceId);
        return deleted ? SpaceResult.SUCCESS : SpaceResult.DATABASE_ERROR;
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
        //TODO: Implement
        return null;
    }

    public ParkingSpace enterWithReservation(String licensePlate) {
        //TODO: Implement
        return null;
    }

    public ParkingSpace enterWithoutReservation(String licensePlate, String vehicleType) {
        //TODO: Implement
        return null;
    }

    public void exit(String licensePlate){
        //TODO: Implement
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
                    "Free",
                    "Free",
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

    public boolean slotExistsById(int id) {
        return parkingSpaceDao.existsById(id);
    }
}

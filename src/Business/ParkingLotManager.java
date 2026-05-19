package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Business.Entities.Vehicle;
import Persistance.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingLotManager {

    private static final int NUM_FLOORS = 4;
    private static final int MAX_SLOTS_PER_FLOOR = 20;

    private ParkingSpaceDAO parkingSpaceDao;
    private VehicleDAO vehicleDao;
    private ParkingLogDAO parkingLogDao;

    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao, VehicleDAO vehicleDao, ParkingLogDAO parkingLogDao) {
        this.parkingSpaceDao = parkingSpaceDao;
        this.vehicleDao = vehicleDao;
        this.parkingLogDao = parkingLogDao;
    }

    public boolean vehiclePlateExists(String licensePlate) {
        return vehicleDao.findByPlate(licensePlate) != null;
    }

    public boolean vehicleBelongsToUser(String licensePlate, int userId) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null && v.getUserId() == userId;
    }

    public boolean registerVehicle(String licensePlate, int userId, String vehicleType) {
        return vehicleDao.addVehicle(licensePlate, userId, vehicleType);
    }

    public boolean vehiclePlateExistsInSystem(String licensePlate) {
        return vehicleDao.existsByPlate(licensePlate);
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
        return parkingSpaceDao.deleteParkingSpace(spaceId) ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }

    public ParkingSpace findAlternativeSpace(String type, int excludeId) {
        return parkingSpaceDao.getAllParkingSpaces().stream()
                .filter(s -> s.getId() != excludeId)
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .filter(s -> !s.isOccupied())
                .filter(s -> !s.isReserved())
                .findFirst()
                .orElse(null);
    }

    public void moveVehicle(ParkingSpace from, ParkingSpace to) {
        to.setOccupied(true);
        from.setOccupied(false);
        parkingSpaceDao.updateParkingSpace(to);
        parkingSpaceDao.updateParkingSpace(from);
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
        boolean ok = parkingSpaceDao.occupySpace(space.getId(), licensePlate);
        if (ok) {
            parkingLogDao.insertLog(space.getId(), licensePlate, userId, "ENTRY");
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public ParkingSpace enterWithoutReservation(String licensePlate, int spaceId, int userId) {
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(spaceId);
        if (fresh == null || fresh.isOccupied()) return null;

        if (parkingSpaceDao.occupySpace(spaceId, licensePlate)) {
            parkingLogDao.insertLog(spaceId, licensePlate, userId, "ENTRY");
            return parkingSpaceDao.getParkingSpaceById(spaceId);
        }
        return null;
    }

    public ParkingSpace exit(String licensePlate, int userId) {
        ParkingSpace space = parkingSpaceDao.getOccupiedSpaceByPlate(licensePlate);
        if (space == null) return null;

        if (parkingSpaceDao.vacateSpace(space.getId())) {
            parkingLogDao.insertLog(space.getId(), licensePlate, userId, "EXIT");
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public String getVehicleType(String licensePlate) {
        Vehicle v = vehicleDao.findByPlate(licensePlate);
        return v != null ? v.getType() : null;
    }

    public Reservation reserve(String licensePlate, String vehicleType, int spaceId) {
        //TODO: Implement
        return null;
    }

    public void cancelReservation(int reservationId, String licensePlate){
        //TODO: Implement
    }

    public List<Integer> getAvailableFloors(int currentFloor) {
        List<ParkingSpace> allSpaces = parkingSpaceDao.getAllParkingSpaces();

        Map<Integer, Long> slotsPerFloor = allSpaces.stream()
                .collect(Collectors.groupingBy(ParkingSpace::getFloor, Collectors.counting()));

        List<Integer> availableFloors = new ArrayList<>();
        for (int floor = 0; floor <= NUM_FLOORS; floor++) {
            long count = slotsPerFloor.getOrDefault(floor, 0L);

            if (floor == currentFloor) {
                availableFloors.add(floor);
            } else if (count < MAX_SLOTS_PER_FLOOR) {
                availableFloors.add(floor);
            }
        }
        return availableFloors;
    }

    // TODO: maybe not need this function
    public boolean slotExistsById(int id) {
        return parkingSpaceDao.existsById(id);
    }

    public Map<Integer,Integer> getOccupancyLastHour() {
        return parkingLogDao.getOccupancyLastHour();
    }

    public void vacateSpacesByUserId(int id) {parkingSpaceDao.vacateSpacesByUserId(id);}
}
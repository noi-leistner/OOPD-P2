package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Persistance.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingLotManager {

    private static final int NUM_FLOORS = 4;
    private static final int MAX_SLOTS_PER_FLOOR = 20;

    private final ParkingSpaceDAO parkingSpaceDao;

    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao) {
        this.parkingSpaceDao = parkingSpaceDao;
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

        if (parkingSpaceDao.occupySpace(space.getId(), licensePlate)) {
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public ParkingSpace enterWithoutReservation(String licensePlate, int spaceId, int userId) {
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(spaceId);
        if (fresh == null || fresh.isOccupied()) return null;

        if (parkingSpaceDao.occupySpace(spaceId, licensePlate)) {
            return parkingSpaceDao.getParkingSpaceById(spaceId);
        }
        return null;
    }

    public ParkingSpace exit(String licensePlate, int userId) {
        ParkingSpace space = parkingSpaceDao.getOccupiedSpaceByPlate(licensePlate);
        if (space == null) return null;

        if (parkingSpaceDao.vacateSpace(space.getId())) {
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }

    public Reservation reserve(String licensePlate, String vehicleType, int spaceId) {
        //TODO: Implement
        return null;
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
}
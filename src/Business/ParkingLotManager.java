package Business;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;

import Persistance.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages parking space operation for the parking system
 * -- Responsabilities: --
 * - CRUD operations on parking spaces through the DAO
 * - Vehicle entry and exit flow (with and without reservation)
 * - Space availability queries by type and floor
 */
public class ParkingLotManager {

    /** Number of floors*/
    private static final int NUM_FLOORS = 4;
    /** Number of slots per floor*/
    private static final int MAX_SLOTS_PER_FLOOR = 20;

    /** DAO for parking space database operations. */
    private final ParkingSpaceDAO parkingSpaceDao;
    /** Constructor */
    public ParkingLotManager (ParkingSpaceDAO parkingSpaceDao) {
        this.parkingSpaceDao = parkingSpaceDao;
    }
    /**
     * Adds a new parking space if it does not already exists.
     * @param space -> space to park, if not found returns DATABASE_ERROR.
     * @return SUCCESS, ALREADY_EXISTS or DATABASE_ERROR
     */
    public DaoResult addSpace(ParkingSpace space) {
        if (space == null) return DaoResult.DATABASE_ERROR;
        if (parkingSpaceDao.existsById(space.getId())) return DaoResult.ALREADY_EXISTS;
        boolean saved = parkingSpaceDao.addParkingSpace(space);
        return saved ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }
    /**
     * Updates an existing parking space.
     * @param space -> The space with updated fields; if space is Null returns DATABASE_ERROR
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult editSpace(ParkingSpace space) {
        if (space == null) return DaoResult.DATABASE_ERROR;

        boolean updated = parkingSpaceDao.updateParkingSpace(space);
        return updated ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }
    /**
     * Deletes an existing parking space. Used when a user/simulated_user leaves the parking.
     * @param spaceId -> id for the parking spot to delete
     * @return SUCCESS or DATABASE_ERROR
     */
    public DaoResult deleteSpace(int spaceId) {
        return parkingSpaceDao.deleteParkingSpace(spaceId) ? DaoResult.SUCCESS : DaoResult.DATABASE_ERROR;
    }
    /**
     * Returns the plate currently occupying the given space, or null if it's empty.
     * @param id -> Space id.
     */
    public String getParkedPlateAtSpace(int id) {
        return parkingSpaceDao.getParkedPlateAtSpace(id);
    }
    /**
     * Finds an available space for a given type, expluding a specific spot.
     * Used to suggest Alternatives when a slot is unavailable.
     * @param type -> "car" or "motorcycle"
     * @param excludeId -> slot Id to skip (conflicting slot).
     * @return first matching available space, or null if none.
     */
    public ParkingSpace findAlternativeSpace(String type, int excludeId) {
        return parkingSpaceDao.getAllParkingSpaces().stream()
                .filter(s -> s.getId() != excludeId)
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .filter(s -> !s.isOccupied())
                .findFirst()
                .orElse(null);
    }
    /** Transfers occupancy from one space to another and persists both.
     * @param  from the space to vacate
     * @param to the space to occupy*/
    public void moveVehicle(ParkingSpace from, ParkingSpace to) {
        to.setOccupied(true);
        from.setOccupied(false);
        parkingSpaceDao.updateParkingSpace(to);
        parkingSpaceDao.updateParkingSpace(from);
    }
    /** Returns full details for a single space, or null if not found. */
    public ParkingSpace getSpaceDetails(int spaceId) {
        return parkingSpaceDao.getParkingSpaceById(spaceId);
    }
    /** Returns all parking spaces regardless of status. */
    public List<ParkingSpace> getAllSpaces() {
        return parkingSpaceDao.getAllParkingSpaces();
    }
    /**
     * Returns available (unoccupied, unreserved) spaces for the given vehicle type.
     * @param vehicleType "car" or "motorcycle"
     */
    public List<ParkingSpace> getAvailableSpacesForType(String vehicleType) {
        return parkingSpaceDao.getAvailableSpacesForType(vehicleType);
    }
    /**
     * Returns all spaces of the given type, regardless of occupancy.
     * @param type "car" or "motorcycle"
     */
    public List<ParkingSpace> getSpotsByType(String type) {
        return parkingSpaceDao.getSpotsByType(type);
    }
    /**
     * Marks the reserved space fot the given plate as occupied.
     * @param licensePlate plate with an active reservation
     * @return the updated space, or null if no reservation found or occupy failed
     */
    public ParkingSpace enterWithReservation(String licensePlate) {
        ParkingSpace space = parkingSpaceDao.getReservedSpaceByPlate(licensePlate);
        if (space == null) return null;
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(space.getId());
        if (fresh == null) return null;

        if (parkingSpaceDao.occupySpace(space.getId(), licensePlate)) {
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }
    /** Occupies a specific space for a walk-in vehicle (no reservation).
     * @param licensePlate -> Plate for the vehicle entry
     * @param spaceId -> spaceId the target slot
     * @return the updated space, or null if the slot is already occupied or not found
     */
    public ParkingSpace enterWithoutReservation(String licensePlate, int spaceId) {
        ParkingSpace fresh = parkingSpaceDao.getParkingSpaceById(spaceId);
        if (fresh == null || fresh.isOccupied()) return null;

        if (parkingSpaceDao.occupySpace(spaceId, licensePlate)) {
            return parkingSpaceDao.getParkingSpaceById(spaceId);
        }
        return null;
    }
    /**
     * Vacates the space currently occupied by the given plate
     * @param licensePlate -> Where the car was parked.
     * @return the updated (and now free) space, or null if an error ocurred.
     */
    public ParkingSpace exit(String licensePlate) {
        ParkingSpace space = parkingSpaceDao.getOccupiedSpaceByPlate(licensePlate);
        if (space == null) return null;

        if (parkingSpaceDao.vacateSpace(space.getId())) {
            return parkingSpaceDao.getParkingSpaceById(space.getId());
        }
        return null;
    }
    /**
     * Returns floors that can accept new slots (below MAX_SLOTS_PER_FLOOR), always
     * including the current floor.
     * @param currentFloor the floor being edited; always included in result
     * @return list of valid floor numbers
     */
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
    /** Returns true if a space with the given ID exists in the database. */
    public boolean slotExistsById(int id) {return parkingSpaceDao.existsById(id);}
    /** Frees all spaces currently occupied by the given user (e.g. on account deletion). */
    public void vacateSpacesByUserId(int id) {parkingSpaceDao.vacateSpacesByUserId(id);}
    /** Returns the count of spaces that are not reserved (available for walk-ins). */
    public int getTotalUnreservedSpaces() {return parkingSpaceDao.getTotalUnreservedSpaces();}
}
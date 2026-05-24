package Business;

import Business.Entities.ParkingSpace;
import Persistance.ConfigDAO;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Manages automated vehicle entry/exit simulation for the parking system.
 * --- Responsabilities: ---
 * - Run a background daemon thread that triggers ticks at a random interval.
 * - Decide entry vs exit probability based on the current occupancy.
 * - Delegate actual entry/exit logic to ParkingLotManager and ParkingLogManager
 */
public class SimulationManager {
    /** Logger */
    private static final Logger logger = Logger.getLogger(SimulationManager.class.getName());

    /** Simulated user ID for all simulated */
    private static final int SIMULATED_USER_ID = -1;

    /** Managers */
    private final ParkingLotManager parkingLotManager;
    private final ParkingLogManager parkingLogManager;
    private final VehicleManager vehicleManager;

    /** Simulation variables */
    private final List<String> simulatedPlates = new ArrayList<>();
    private final Random random = new Random();
    private volatile boolean running = false;
    private Thread simulationThread;
    private Runnable onTickCallBack;

    /** Constructor */
    public SimulationManager(ParkingLotManager parkingLotManager, ParkingLogManager parkingLogManager, VehicleManager vehicleManager) {
        this.parkingLotManager = parkingLotManager;
        this.parkingLogManager = parkingLogManager;
        this.vehicleManager = vehicleManager;
    }

    /** Sets the callback executed on the EDT after each simulation tick.
     * @param onTickCallBack Runnable to refresh UI.
     */
    public void setOnTickCallBack(Runnable onTickCallBack) {
        this.onTickCallBack = onTickCallBack;
    }

    /** Starts simulation on a background daemon thread. */
    public void start() {
        if (running) return;
        running = true;

        simulationThread = new Thread(() -> {
            while (running) {
                try {
                    int maxDelay = ConfigDAO.getVehicleEntryTime();
                    int delay = random.nextInt(maxDelay) + 1;
                    Thread.sleep(delay*1000L);

                    if (running) {tick();}
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        simulationThread.setDaemon(true); // Stop when closing the app.
        simulationThread.start();
        logger.info("Simulation started");
    }

    /** Stops the simulation */
    public void stop() {
        running = false;
        if (simulationThread != null) simulationThread.interrupt();
        logger.info("Simulation stopped");
    }

    /** One simulation tick: picks entry exit based one p(entry) = available / totalUnreserved,
     * Then notifies the UI callback on the EDT.
     */
    private void tick() {
        List<ParkingSpace> availableMotos = parkingLotManager.getAvailableSpacesForType("Motorcycle");
        List<ParkingSpace> availableCars = parkingLotManager.getAvailableSpacesForType("Car");
        List<ParkingSpace> availableTrucks = parkingLotManager.getAvailableSpacesForType("Truck");
        List<ParkingSpace> available = new ArrayList<>();
        available.addAll(availableMotos);
        available.addAll(availableCars);
        available.addAll(availableTrucks);
        int totalUnreserved = parkingLotManager.getTotalUnreservedSpaces();

        // decide entry or exit based on current occupancy
        boolean parkingEmpty = simulatedPlates.isEmpty();

        if (totalUnreserved == 0) {
            return;
        }

        if (available.isEmpty()) {
            if (!simulatedPlates.isEmpty()) simulateExit();
            return;
        }

        double pEntry = (double) available.size() / totalUnreserved;

        if (simulatedPlates.isEmpty()){
            simulateEntry(available);
        } else {
            if (random.nextDouble() < pEntry) {
                simulateEntry(available);
            } else {
                simulateExit();
            }

        }

        // notify UI to refresh chart
        if (onTickCallBack != null) {
            SwingUtilities.invokeLater(onTickCallBack);
        }
    }

    /** Picks a random available space, generates a plate, and enters without reservation.
     * Inserts a temporary vehicle record; removes it if the entry fails.
     * @param available non-empty list of available spaces
     */
    public void simulateEntry(List<ParkingSpace> available) {
        ParkingSpace space = available.get(random.nextInt(available.size()));
        String plate = plateGenerator();
        vehicleManager.insertSimulatedVehicle(plate, space.getType());

        ParkingSpace result = parkingLotManager.enterWithoutReservation(plate, space.getId());
        if (result != null) {
            simulatedPlates.add(plate);
            parkingLogManager.logEntry(space.getId(), plate, SIMULATED_USER_ID);
            logger.info("[ENTRY]>  " + plate + " ← slot: [" + result.getId() + "]");
        } else {
            logger.warning("- Spot was full and the driver: " + plate + " left.");
            vehicleManager.deleteSimulatedVehicle(plate);
        }
    }

    /** Picks a random simulated plate and exists; cleans up vehicle record and log. */
    private void simulateExit() {
        if (simulatedPlates.isEmpty()) return;
        String plate = simulatedPlates.get(random.nextInt(simulatedPlates.size()));

        ParkingSpace result = parkingLotManager.exit(plate);
        if (result != null) {
            simulatedPlates.remove(plate);
            parkingLogManager.logExit(result.getId(), plate, SIMULATED_USER_ID);
            vehicleManager.deleteSimulatedVehicle(plate);
            logger.info("<[EXIT]  " + plate + " ← slot: [ " + result.getId() + "]");
        }
    }

    /** Generates a plate with consonants and 4 random numbers */
    private String plateGenerator() {
        //String plate = "";
        String letters = "BCDFGHJKLMNPQRSTVWXYZ";
        char l1 = letters.charAt(random.nextInt(letters.length()));
        char l2 = letters.charAt(random.nextInt(letters.length()));
        char l3 = letters.charAt(random.nextInt(letters.length()));
        int nums = random.nextInt(9000) + 1000;
        return "" + nums + l1 + l2 + l3;
    }
}

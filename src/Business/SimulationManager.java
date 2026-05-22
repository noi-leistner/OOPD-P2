package Business;

import Business.*;
import Business.Entities.ParkingSpace;
import Persistance.ConfigDAO;
import Persistance.ParkingLogDAO;
import Persistance.ParkingSpaceDAO;
import Persistance.VehicleDAO;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class SimulationManager {
    private static final Logger logger = Logger.getLogger(SimulationManager.class.getName());
    private static final int SIMULATED_USER_ID = -1;

    private final ParkingLotManager parkingLotManager;
    private final ParkingLogManager parkingLogManager;
    private final VehicleManager vehicleManager;

    private final List<String> simulatedPlates = new ArrayList<>();
    private final Random random = new Random();

    private volatile boolean running = false;
    private Thread simulationThread;

    private Runnable onTickCallBack;

    public SimulationManager(ParkingLotManager parkingLotManager, ParkingLogManager parkingLogManager, VehicleManager vehicleManager) {
        this.parkingLotManager = parkingLotManager;
        this.parkingLogManager = parkingLogManager;
        this.vehicleManager = vehicleManager;
    }
    public void setOnTickCallBack(Runnable onTickCallBack) {
        this.onTickCallBack = onTickCallBack;
    }

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

    public void stop() {
        running = false;
        if (simulationThread != null) simulationThread.interrupt();
        logger.info("Simulation stopped");
    }

    private void tick() {
            List<ParkingSpace> availableMotos = parkingLotManager.getSpotsByType("motorcycle");
            List<ParkingSpace> availableCars = parkingLotManager.getSpotsByType("car");
            List<ParkingSpace> available = new ArrayList<>();
            available.addAll(availableMotos);
            available.addAll(availableCars);
            int totalUnreserved = parkingLotManager.getTotalUnreservedSpaces();

            // decide entry or exit based on current occupancy
            boolean parkingEmpty = simulatedPlates.isEmpty();

            if (totalUnreserved == 0) return;

            double pEntry = (double) available.size() / totalUnreserved;

            if (simulatedPlates.isEmpty()){
                simulateEntry(available);
            } else {
                if (available.isEmpty()) {
                    simulateExit();
                } else {
                    if (random.nextDouble() < pEntry) {
                        simulateEntry(available);
                    } else {
                        simulateExit();
                    }
                }
            }

            // notify UI to refresh chart
            if (onTickCallBack != null) {
                SwingUtilities.invokeLater(onTickCallBack);
            }
    }

    public void simulateEntry(List<ParkingSpace> available) {
        boolean simulation_quit = false;
        ParkingSpace space = available.get(random.nextInt(available.size()));
        String plate = plateGenerator();
        vehicleManager.insertSimulatedVehicle(plate, space.getType());

        ParkingSpace result = parkingLotManager.enterWithoutReservation(plate, space.getId(), SIMULATED_USER_ID);
        logger.info("ENTRY attempt — plate: " + plate + " slot: " + space.getId());
        if (result != null) {
            simulatedPlates.add(plate);
            parkingLogManager.logEntry(space.getId(), plate, SIMULATED_USER_ID);
            logger.info("[ENTRY]>  " + plate + " ← slot: [" + result.getId() + "]");
        } else {
            result = parkingLotManager.enterWithoutReservation(plate, space.getId(), SIMULATED_USER_ID);
            logger.info("SECOND ENTRY attempt — plate: " + plate + " slot: " + space.getId());
            if (result != null) {
                simulatedPlates.add(plate);
                parkingLogManager.logEntry(result.getId(), plate, SIMULATED_USER_ID);
                logger.info("[ENTRY]>  " + plate + " ← slot: [" + result.getId() + "]");
            } else {
                logger.warning("- Spot was full and the driver: " + plate + " left.");
                vehicleManager.deleteSimulatedVehicle(plate);
            }
        }
    }

    private void simulateExit() {
        if (simulatedPlates.isEmpty()) return;
        String plate = simulatedPlates.get(random.nextInt(simulatedPlates.size()));

        ParkingSpace result = parkingLotManager.exit(plate, SIMULATED_USER_ID);
        if (result != null) {
            simulatedPlates.remove(plate);
            parkingLogManager.logExit(result.getId(), plate, SIMULATED_USER_ID);
            vehicleManager.deleteSimulatedVehicle(plate);
            logger.info("<[EXIT]  " + plate + " ← slot: [ " + result.getId() + "]");
        }
    }

    private String plateGenerator() {
        String plate = "";
        String letters = "BCDFGHJKLMNPQRSTVWXYZ";
        char l1 = letters.charAt(random.nextInt(letters.length()));
        char l2 = letters.charAt(random.nextInt(letters.length()));
        char l3 = letters.charAt(random.nextInt(letters.length()));
        int nums = random.nextInt(9000) + 1000;
        return "" + nums + l1 + l2 + l3;
    }

    public boolean isRunning () {return running;}
}

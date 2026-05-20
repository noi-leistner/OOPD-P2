package Business;

import Business.*;
import Business.Entities.ParkingSpace;
import Persistance.ConfigDAO;
import Persistance.ParkingLogDAO;
import Persistance.ParkingSpaceDAO;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.InputStreamReader;

public class SimulationManager {
    private static final Logger logger = Logger.getLogger(SimulationManager.class.getName());
    private static final int SIMULATED_USER_ID = -1;

    private final ParkingLotManager parkingLotManager;
    private final ParkingSpaceDAO parkingSpaceDAO;
    private final ParkingLogDAO parkingLogDAO;

    private final List<String> simulatedPlates = new ArrayList<>();
    private final Random random = new Random();

    private boolean running = false;
    private Thread simulationThread;

    private Runnable onTickCallBack;

    public SimulationManager (ParkingLotManager parkingLotManager,
                              ParkingSpaceDAO parkingSpaceDAO,
                              ParkingLogDAO parkingLogDAO) {
        this.parkingLotManager = parkingLotManager;
        this.parkingSpaceDAO = parkingSpaceDAO;
        this.parkingLogDAO = parkingLogDAO;
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
            List<ParkingSpace> available = parkingSpaceDAO.findAvailableUnreserved();
            int totalOccupied = simulatedPlates.size();
            int totalSpaces = parkingSpaceDAO.getAllParkingSpaces().size();

            // decide entry or exit based on current occupancy
            boolean parkingFull = available.isEmpty();
            boolean parkingEmpty = simulatedPlates.isEmpty();

            if (parkingFull) {
                simulateExit(available);
            } else if (parkingEmpty) {
                simulateEntry(available);
            } else {
                // 60% chance entry, 40% chance exit
                if (random.nextInt(100) < 60) {
                    simulateEntry(available);
                } else {
                    simulateExit(available);
                }
            }

            // notify UI to refresh chart
            if (onTickCallBack != null) {
                SwingUtilities.invokeLater(onTickCallBack);
            }
    }

    public void simulateEntry(List<ParkingSpace> available) {
        if (available.isEmpty()) return;

        ParkingSpace space = available.get(random.nextInt(available.size()));
        String plate = plateGenerator();

        ParkingSpace result = parkingLotManager.enterWithoutReservation(plate, space.getId(), SIMULATED_USER_ID);
        if (result != null) {
            simulatedPlates.add(plate);
            parkingLogDAO.insertLog(space.getId(), plate, SIMULATED_USER_ID, "enter");
            logger.info("simulated plate " + plate);
        }
    }

    private void simulateExit(List<ParkingSpace> available) {
        if (available.isEmpty()) return;

        ParkingSpace space = available.get(random.nextInt(available.size()));
        String plate = simulatedPlates.get(random.nextInt(simulatedPlates.size()));

        ParkingSpace result = parkingLotManager.exit(plate, SIMULATED_USER_ID);
        if (result != null) {
            simulatedPlates.remove(plate);
            parkingLogDAO.insertLog(space.getId(), plate, SIMULATED_USER_ID, "EXIT");
            logger.info("Simmulation created for plate (EXIT)" + plate + "Spot: " + space.getId());
        }
    }

    private String plateGenerator() {
        String plate = "";
        String letters = "BCDFGHJKLMNPQRSTVWXYZ";
        char l1 = letters.charAt(random.nextInt(letters.length()));
        char l2 = letters.charAt(random.nextInt(letters.length()));
        char l3 = letters.charAt(random.nextInt(letters.length()));
        char l4 = letters.charAt(random.nextInt(letters.length()));
        int nums = random.nextInt(9000) + 1000;
        return "" + l1 + l2 + l3 + l4 + nums;
    }

    public boolean isRunning () {return running;}
}

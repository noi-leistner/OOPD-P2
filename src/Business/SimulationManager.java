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
    private final ParkingSpaceDAO parkingSpaceDAO;
    private final ParkingLogDAO parkingLogDAO;
    private final VehicleDAO vehicleDAO;

    private final List<String> simulatedPlates = new ArrayList<>();
    private final Random random = new Random();

    private volatile boolean running = false;
    private Thread simulationThread;

    private Runnable onTickCallBack;

    public SimulationManager (ParkingLotManager parkingLotManager,
                              ParkingSpaceDAO parkingSpaceDAO,
                              ParkingLogDAO parkingLogDAO,
                              VehicleDAO vehicleDAO) {
        this.parkingLotManager = parkingLotManager;
        this.parkingSpaceDAO = parkingSpaceDAO;
        this.parkingLogDAO = parkingLogDAO;
        this.vehicleDAO = vehicleDAO;
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
            int totalUnreserved = parkingSpaceDAO.getTotalUnreservedSpaces();

            // decide entry or exit based on current occupanc7
            boolean parkingEmpty = simulatedPlates.isEmpty();

            if (totalUnreserved == 0) return;

            double pEntry = (double) available.size() / totalUnreserved;

            if (simulatedPlates.isEmpty()){
                simulateExit();
            } else if (parkingEmpty) {
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

    public void simulateEntry(List<ParkingSpace> available) {

        ParkingSpace space = available.get(random.nextInt(available.size()));
        String plate = plateGenerator();
        logger.info("ENTRY attempt — plate: " + plate + " slot: " + space.getId()); // ← afegir


        vehicleDAO.insertSimulatedVehicle(plate, space.getType());

        ParkingSpace result = parkingLotManager.enterWithoutReservation(plate, space.getId(), SIMULATED_USER_ID);
        if (result != null) {
            simulatedPlates.add(plate);
            parkingLogDAO.insertLog(space.getId(), plate, SIMULATED_USER_ID, "ENTRY");
            logger.info("\u001B[34m" + "[ENTRY] " + plate + " → slot " + space.getId() + " at " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "\u001B[0m)");
        }

    }

    private void simulateExit() {
        if (simulatedPlates.isEmpty()) return;
        String plate = simulatedPlates.get(random.nextInt(simulatedPlates.size()));

        ParkingSpace result = parkingLotManager.exit(plate, SIMULATED_USER_ID);
        if (result != null) {
            simulatedPlates.remove(plate);
            parkingLogDAO.insertLog(result.getId(), plate, SIMULATED_USER_ID, "EXIT");
            vehicleDAO.deleteSimulatedVehicle(plate);
            logger.info("\u001B[34m" + "[EXIT]  " + plate + " ← slot " + result.getId() + " at " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "\u001B[0m)");
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

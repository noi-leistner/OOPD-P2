package Business;

import Business.*;
import Business.Entities.ParkingSpace;
import Persistance.ConfigDAO;
import Persistance.ParkingLogDAO;
import Persistance.ParkingSpaceDAO;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

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

                    if (running) {tick()};
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
        int totalUnreserved = parkingSpaceDAO.getAllParkingSpaces().stream().filter(s -> !s.isReserved()).toList().size();
        if (totalUnreserved == 0) return;
    }

    private void simulateEntry() {

    }
}

package Presentation.views;

import Business.*;
import Persistance.*;
import Presentation.controllers.*;

import javax.swing.*;
import java.awt.*;

/**
 * The primary container window and application entry point for the Parking management system.
 * <p>
 * This class acts as the top-level application framework frame. It sets up foundational settings
 * (size, exit rules, centering layout), acts as the primary Dependency Injection container by
 * bootstrapping all DAOs, Managers, and Controllers, and exposes layout context swapping methods
 * via a {@link CardLayout} switcher mechanism.
 */
public class MainWindow extends JFrame {

    /** The internal structural layout engine tracking screen context cards. */
    private final CardLayout cardLayout;

    /** The root element panel container acting as the parent canvas hosting our sub-views. */
    private final JPanel mainPanel;

    /** The operational application control deck panel, swapped in upon validation of active sessions. */
    private DashboardPanel dashboardPanel;

    /** Unique dictionary key mapping identifier tracking the authentication screen card. */
    public static final String AUTH_SCREEN = "AUTH";

    /** Unique dictionary key mapping identifier tracking the main application card. */
    public static final String DASHBOARD_SCREEN = "DASHBOARD";

    /**
     * Bootstraps the application framework frame, runs the initial concrete database component dependency
     * chains, binds them to controller targets, and flips screen tracking visibility flags to the entry screen.
     */
    public MainWindow() {
        setTitle("Parking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        UserDAO userDAO = new UserDAOSql();
        ParkingSpaceDAO parkingSpaceDAO = new ParkingSpaceDAOSql();
        ReservationDAO reservationDAO = new ReservationDAOSql();
        VehicleDAO vehicleDAO = new VehicleDAOSql();
        ParkingLogDAO parkingLogDAO = new ParkingLogDAOSql();

        AuthManager authManager = new AuthManager(userDAO);
        SessionManager sessionManager = SessionManager.getInstance();
        AuthController authController = new AuthController(authManager, sessionManager);
        ParkingLotManager parkingLotManager = new ParkingLotManager(parkingSpaceDAO);
        VehicleManager vehicleManager = new VehicleManager(vehicleDAO);
        ParkingLogManager parkingLogManager = new ParkingLogManager(parkingLogDAO);
        ReservationManager reservationManager = new ReservationManager(reservationDAO);
        SimulationManager simulationManager = new SimulationManager(parkingLotManager, parkingLogManager, vehicleManager);
        ReservationController reservationController = new ReservationController(reservationManager);
        EntryExitController entryExitController = new EntryExitController(parkingLotManager, reservationController, vehicleManager, parkingLogManager);
        ParkingSpaceController slotController = new ParkingSpaceController(parkingLotManager, reservationController);
        StatusController statusController = new StatusController(parkingLotManager, simulationManager);
        OccupancyController occupancyController = new OccupancyController(parkingLogManager, simulationManager, new OccupancyView());

        AuthPanel authPanel = new AuthPanel(this, authController);
        OccupancyView occupancyView = new OccupancyView();

        dashboardPanel = new  DashboardPanel(this, authController, slotController, reservationController, statusController, entryExitController, occupancyController);

        mainPanel.add(AUTH_SCREEN, authPanel);
        mainPanel.add(DASHBOARD_SCREEN, dashboardPanel);

        add(mainPanel);

        cardLayout.show(mainPanel, AUTH_SCREEN);
    }

    /**
     * Grabs the active application administration layout framework interface.
     *
     * @return The active internal dashboard instance containing tab panels.
     */
    public DashboardPanel getDashboard() {
        return dashboardPanel;
    }

    /**
     * Flips the active presentation card view inside the window frame stack.
     *
     * @param screen The unique key string tracking the targeted destination panel layout.
     */
    public void switchTo(String screen) {
        cardLayout.show(mainPanel, screen);
        this.revalidate();
        this.repaint();
    }
}

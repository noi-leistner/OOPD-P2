package Presentation.views;

import Business.AuthManager;
import Business.ParkingLotManager;
import Business.ReservationManager;
import Business.SessionManager;
import Persistance.*;
import Presentation.controllers.AuthController;
import Presentation.controllers.EntryExitController;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.StatusController;
import Presentation.controllers.ReservationController;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private DashboardPanel dashboardPanel;

    public static final String AUTH_SCREEN = "AUTH";
    public static final String DASHBOARD_SCREEN = "DASHBOARD";

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

        AuthManager authManager = new AuthManager(userDAO, vehicleDAO, parkingSpaceDAO, reservationDAO);
        SessionManager sessionManager = SessionManager.getInstance();
        AuthController authController = new AuthController(authManager, sessionManager);
        ParkingLotManager parkingLotManager = new ParkingLotManager(parkingSpaceDAO, vehicleDAO, parkingLogDAO);
        ReservationManager reservationManager = new ReservationManager(reservationDAO);
        ReservationController reservationController = new ReservationController(reservationManager);
        EntryExitController entryExitController = new EntryExitController(parkingLotManager, reservationController, vehicleDAO);
        ParkingSpaceController slotController = new ParkingSpaceController(parkingLotManager, reservationController);
        //TODO: idk if we need a status controller (maybe slotController is enough)
        StatusController statusController = new StatusController(parkingLotManager, reservationManager);

        AuthPanel authPanel = new AuthPanel(this, authController);

        dashboardPanel = new  DashboardPanel(this, authController, slotController, reservationController, statusController, entryExitController);

        mainPanel.add(AUTH_SCREEN, authPanel);
        mainPanel.add(DASHBOARD_SCREEN, dashboardPanel);

        add(mainPanel);

        cardLayout.show(mainPanel, AUTH_SCREEN);
    }

    public DashboardPanel getDashboard() {
        return dashboardPanel;
    }

    public void switchTo(String screen) {
        cardLayout.show(mainPanel, screen);
        this.revalidate();
        this.repaint();
    }
}

package Presentation.views;

import Business.AuthManager;
import Business.ParkingLotManager;
import Business.ReservationManager;
import Business.SessionManager;
import Persistance.*;
import Presentation.controllers.AuthController;
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

        AuthManager authManager       = new AuthManager(userDAO);
        SessionManager sessionManager = SessionManager.getInstance();
        AuthController authController = new AuthController(authManager, sessionManager);
        ParkingLotManager parkingLotManager = new ParkingLotManager(parkingSpaceDAO);
        ReservationManager reservationManager = new ReservationManager(reservationDAO);
        ParkingSpaceController slotController = new ParkingSpaceController(parkingLotManager, reservationManager);
        //TODO: idk if we need a status controller (maybe slotController is enough)
        StatusController statusController = new StatusController(parkingLotManager, reservationManager);
        ReservationController reservationController = new ReservationController(reservationManager);


        authController.setParkingLotManager(parkingLotManager);

        AuthPanel authPanel = new AuthPanel(this, authController);

        dashboardPanel = new  DashboardPanel(this, authController, slotController, reservationController, statusController);


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

//h
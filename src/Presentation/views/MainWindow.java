package Presentation.views;

import Business.AuthManager;
import Business.SessionManager;
import Persistance.UserDAOSql;
import Presentation.controllers.AuthController;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;


    public static final String AUTH_SCREEN = "AUTH";
    public static final String DASHBOARD_SCREEN = "DASHBOARD";

    public MainWindow() {
        setTitle("Parking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        UserDAOSql userDAO            = new UserDAOSql();
        AuthManager authManager       = new AuthManager(userDAO);
        SessionManager sessionManager = SessionManager.getInstance();
        AuthController authController = new AuthController(authManager, sessionManager);


        AuthPanel authPanel = new AuthPanel(this, authController);
        DashboardPanel dashboardPanel = new  DashboardPanel(this, authController);

        mainPanel.add(AUTH_SCREEN, authPanel);
        mainPanel.add(DASHBOARD_SCREEN, dashboardPanel);

        add(mainPanel);

        cardLayout.show(mainPanel, AUTH_SCREEN);
    }

    public void switchTo(String screen) {
        cardLayout.show(mainPanel, screen);
        this.revalidate();
        this.repaint();
    }
}

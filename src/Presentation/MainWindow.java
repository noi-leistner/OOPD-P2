package Presentation;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    //TODO: Implement

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String AUTH_SCREEN = "AUTH";
    public static final String DASHBOARD_SCREEN = "DASHBOARD";

    public MainWindow() {
        setTitle("Parking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        AuthController authController = new AuthController();

        mainPanel.add(new AuthPanel(this, authController), AUTH_SCREEN);
        //mainPanel.add(new DashboardPanel(this), DASHBOARD_SCREEN);

        add(mainPanel);

        cardLayout.show(mainPanel, AUTH_SCREEN);
    }

    public void switchTo(String screen) {
        cardLayout.show(mainPanel, screen);
    }
}

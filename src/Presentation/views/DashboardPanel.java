package Presentation.views;

import Business.Entities.User;
import Business.SessionManager;
import Presentation.controllers.AuthController;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel contentArea;

    public DashboardPanel(MainWindow mainWindow, AuthController authController) {
        setLayout(new BorderLayout());

//        // 1. Cabecera de bienvenida
//        JLabel welcomeLabel = new JLabel("Welcome to the Parking Dashboard!");
//        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
//        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
//        add(welcomeLabel, BorderLayout.NORTH);
//
//        // 2. Contenido central (puedes añadir estadísticas o botones luego)
//        JPanel centerPanel = new JPanel();
//        centerPanel.add(new JLabel("You have successfully logged in."));
//        add(centerPanel, BorderLayout.CENTER);

//        // 3. Botón de Logout para volver al inicio
//        JButton logoutButton = new JButton("Logout");
//        logoutButton.addActionListener(e -> {
//            // Aquí podrías llamar a authController.logOut() si lo necesitas
//            mainWindow.switchTo(MainWindow.AUTH_SCREEN);
//        });

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        add(buildSidebar(), BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

//        JButton logoutButton = new JButton("Logout");
//        logoutButton.addActionListener(e -> {
//            //authController.logOut();
//            mainWindow.switchTo(MainWindow.AUTH_SCREEN);
//        });
//        JPanel southPanel = new JPanel();
//        southPanel.add(logoutButton);
//        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 5, 5));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        User currentUser = SessionManager.getInstance().getCurrentUser();

        //TODO: should be currentUser.isAdmin()
        if (true) {
            addButton(sidebar, "Manage Slots",  "SLOTS");
            addButton(sidebar, "Manage Bookings",  "BOOKINGS");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");

            contentArea.add(new ManageSlotsPanel(),   "SLOTS");
//            contentArea.add(new ManageUsersPanel(),   "USERS");
//            contentArea.add(new OccupancyPanel(),  "OCCUPANCY");
//            contentArea.add(new CurrentStatusPanel(),  "STATUS");
            addButton(sidebar, "Log Out", "LOGOUT");

            showContent("SLOTS");
        } else {
            addButton(sidebar, "Vehicle Entry", "VEHICLE_ENTRY");
            addButton(sidebar, "Vehicle Exit",     "VEHICLE_EXIT");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");

//            contentArea.add(new VehicleEntryPanel(), "VEHICLE_ENTRY");
//            contentArea.add(new VehicleExitPanel(), "VEHICLE_EXIT");
//            contentArea.add(new OccupancyPanel(), "OCCUPANCY");
//            contentArea.add(new CurrentStatusPanel(),  "STATUS");
            addButton(sidebar, "Log Out", "LOGOUT");

            showContent("MY_RESERVATIONS");
        }

        return sidebar;
    }

    private void addButton(JPanel sidebar, String label, String contentName) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> showContent(contentName));
        sidebar.add(btn);
    }

    public void showContent(String name) {
        cardLayout.show(contentArea, name);
    }
}
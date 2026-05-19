package Presentation.views;

import Business.Entities.Reservation;
import Business.Entities.User;
import Business.SessionManager;
import Presentation.controllers.AuthController;
import Presentation.controllers.EntryExitController;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.StatusController;
import Presentation.controllers.ReservationController;
import Presentation.controllers.StatusController;
import Presentation.theme.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel contentArea;

    private EntryExitController entryExitController;

    private MainWindow mainWindow;
    private AuthController authController;
    private StatusController statusController;

    private final java.util.List<JButton> buttons = new java.util.ArrayList<>();
    private JButton initialButton;

    private ParkingSpaceController slotController;
    private ReservationController reservationController;


    public DashboardPanel(MainWindow mainWindow, AuthController authController, ParkingSpaceController slotController, ReservationController reservationController, StatusController statusController, EntryExitController entryExitController) {
        this.slotController = slotController;
        this.mainWindow = mainWindow;
        this.authController = authController;
        this.statusController = statusController;
        this.reservationController = reservationController;
        this.entryExitController = entryExitController;

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        add(contentArea, BorderLayout.CENTER);
    }

    public void refresh() {
        removeAll();
        buttons.clear();
        contentArea.removeAll();

        add(buildSidebar(), BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        showCancelledReservationNotification();
        revalidate();
        repaint();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser.isAdmin()) {
            sidebar.add(addTitle("Admin functionalities"));
            sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

            addButton(sidebar, "Manage Slots",  "SLOTS");
            addButton(sidebar, "Manage Bookings",  "BOOKINGS");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");

            ManageSlotsPanel manageSlotsPanel = new ManageSlotsPanel(slotController, reservationController);
            manageSlotsPanel.refreshTable();

            OccupancyPanel occupancyPanel = new OccupancyPanel(statusController);

            contentArea.add(manageSlotsPanel,   "SLOTS");

            ManageBookingsPanel manageBookingsPanel = new ManageBookingsPanel(reservationController, slotController);
            manageBookingsPanel.refreshTable();
            contentArea.add(manageBookingsPanel,   "BOOKINGS");
            contentArea.add(new OccupancyPanel(statusController),  "OCCUPANCY");
            contentArea.add(new CurrentStatusPanel(statusController),  "STATUS");
            addButton(sidebar, "Log Out", "LOGOUT");
            contentArea.add(new LogOutPanel(mainWindow, authController, reservationController, slotController, entryExitController), "LOGOUT");

            showContent("SLOTS");
            highlightButton(initialButton);
        } else {
            sidebar.add(addTitle("Client functionalities"));
            sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

            addButton(sidebar, "Vehicle Entry", "VEHICLE_ENTRY");
            addButton(sidebar, "Vehicle Exit",     "VEHICLE_EXIT");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");
            addButton(sidebar, "Log Out", "LOGOUT");

            contentArea.add(new VehicleEntryPanel(entryExitController), "VEHICLE_ENTRY");
            contentArea.add(new VehicleExitPanel(entryExitController), "VEHICLE_EXIT");
            contentArea.add(new OccupancyPanel(statusController), "OCCUPANCY");
            contentArea.add(new CurrentStatusPanel(statusController),  "STATUS");
            contentArea.add(new CurrentStatusPanel(statusController),  "STATUS");
            contentArea.add(new LogOutPanel(mainWindow, authController, reservationController, slotController, entryExitController), "LOGOUT");


            showContent("VEHICLE_ENTRY");
            highlightButton(initialButton);
        }

        return sidebar;
    }

    private JPanel addTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel titlePanel = new JPanel(new GridBagLayout());

        titlePanel.add(title);

        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        titlePanel.setPreferredSize(new Dimension(180, 40));

        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        return titlePanel;
    }

    private void addButton(JPanel sidebar, String label, String contentName) {
        JButton btn = new JButton(label);

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.setBackground(AppColors.BUTTON_DEFAULT);
        btn.setForeground(Color.BLACK);

        if (contentName.equals("SLOTS") || contentName.equals("VEHICLE_ENTRY")) {
            initialButton = btn;
        }

        buttons.add(btn);

        btn.addActionListener(e -> {
            highlightButton(btn);
            showContent(contentName);
        });

        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private void highlightButton(JButton selected) {
        for (JButton btn : buttons) {
            btn.setBackground(AppColors.BUTTON_DEFAULT);
            btn.setForeground(Color.BLACK);
        }

        selected.setBackground(AppColors.LIGHT_BLUE);
        selected.setForeground(Color.WHITE);
    }

    public void showContent(String name) {
        cardLayout.show(contentArea, name);
    }

    private void showCancelledReservationNotification() {

        User user = SessionManager.getInstance().getCurrentUser();

        if (user == null || user.isAdmin()) {
            return;
        }

        List<Reservation> cancelled = reservationController.getCancelledReservations(user.getId());
        if (!cancelled.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("The following reservations have been cancelled:\n\n");

            for (Reservation r : cancelled) {
                message.append("• Plate: ")
                        .append(r.getVehiclePlate())
                        .append(" | Spot: ")
                        .append(r.getParking_slot_id())
                        .append(" | Date: ")
                        .append(r.getDate())
                        .append("\n");
            }

            message.append("\nPlease make a new reservation if needed.");

            JOptionPane.showMessageDialog(
                    this,
                    message.toString(),
                    "Reservation Cancelled",
                    JOptionPane.WARNING_MESSAGE
            );


            reservationController.deleteCancelledReservations(user.getId());
        }
    }
}
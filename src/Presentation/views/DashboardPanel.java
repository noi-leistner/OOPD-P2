package Presentation.views;

import Business.Entities.Reservation;
import Business.Entities.User;
import Business.SessionManager;
import Presentation.controllers.AuthController;
import Presentation.controllers.EntryExitController;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.StatusController;
import Presentation.controllers.ReservationController;
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
        reservationController.deleteExpiredReservations();

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
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AppColors.DARK_BLUE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();

            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(210,0));
        sidebar.add(buildTitleHeader());

        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser.isAdmin()) {
            sidebar.add(buildSectionLabel("Admin"));
            sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

            addButton(sidebar, "Manage Slots",  "SLOTS");
            addButton(sidebar, "Manage Bookings",  "BOOKINGS");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");
            addButton(sidebar, "Log out", "LOGOUT");

            ManageSlotsPanel manageSlotsPanel = new ManageSlotsPanel(slotController, reservationController);
            manageSlotsPanel.refreshTable();

            OccupancyPanel occupancyPanel = new OccupancyPanel(statusController);
            ManageBookingsPanel manageBookingsPanel = new ManageBookingsPanel(reservationController, slotController);
            manageBookingsPanel.refreshTable();
            contentArea.add(manageSlotsPanel,   "SLOTS");
            contentArea.add(manageBookingsPanel,   "BOOKINGS");
            contentArea.add(new OccupancyPanel(statusController),  "OCCUPANCY");
            contentArea.add(new CurrentStatusPanel(statusController, reservationController),  "STATUS");
            contentArea.add(new LogOutPanel(mainWindow, authController, reservationController, slotController, entryExitController), "LOGOUT");
          

            sidebar.add(Box.createVerticalGlue());
            sidebar.add(buildDivider());
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

            showContent("SLOTS");
            highlightButton(initialButton);
        } else {
            sidebar.add(buildSectionLabel("Client"));
            sidebar.setBackground(AppColors.DARK_BLUE);
            sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

            addButton(sidebar, "Vehicle Entry", "VEHICLE_ENTRY");
            addButton(sidebar, "Vehicle Exit",     "VEHICLE_EXIT");
            addButton(sidebar, "Manage Reservations", "RESERVE");
            addButton(sidebar, "Last Hour Occupancy",  "OCCUPANCY");
            addButton(sidebar, "Current Parking status", "STATUS");
            addButton(sidebar, "Log Out", "LOGOUT");

            contentArea.add(new VehicleEntryPanel(entryExitController), "VEHICLE_ENTRY");
            contentArea.add(new VehicleExitPanel(entryExitController), "VEHICLE_EXIT");
            ManageReservationsPanel manageReservationsPanel = new ManageReservationsPanel(reservationController, slotController, entryExitController);
            manageReservationsPanel.refreshTable();
            contentArea.add(manageReservationsPanel, "RESERVE");
            contentArea.add(new OccupancyPanel(statusController), "OCCUPANCY");
            contentArea.add(new CurrentStatusPanel(statusController, reservationController),  "STATUS");
            contentArea.add(new LogOutPanel(mainWindow, authController), "LOGOUT");
            sidebar.add(Box.createVerticalGlue());
            sidebar.add(buildDivider());
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));


            showContent("VEHICLE_ENTRY");
            highlightButton(initialButton);
        }

        return sidebar;
    }

    // ── "ParkManager" title — text only, no logo ──────────────────────────────
    private JPanel buildTitleHeader() {
        JLabel title = new JLabel("Parking Manager");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.CENTER);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        header.setMinimumSize(new Dimension(0, 64));
        header.setPreferredSize(new Dimension(210, 64));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 30)));

        return header;
    }


    private void addButton(JPanel sidebar, String label, String contentName) {
        JButton btn = new JButton(label);

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.setBackground(AppColors.DARK_BLUE);
        btn.setForeground(AppColors.FIELD_BORDER);

        if (contentName.equals("SLOTS") || contentName.equals("VEHICLE_ENTRY")) {
            initialButton = btn;
        }

        buttons.add(btn);

        btn.addActionListener(e -> {
            highlightButton(btn);
            showContent(contentName);
        });

        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
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
                        .append(r.getParkingSlotId())
                        .append(" | Start Date: ")
                        .append(r.getStartDateTime())
                        .append(" | End Date: ")
                        .append(r.getEndDateTime())
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

    private JPanel buildDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AppColors.TEXT_MUTED);
                g2.fillRect(0, getHeight()/2, getWidth(), 1);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        return divider;
    }

    private JPanel buildSectionLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Arial", Font.BOLD, 10));
        label.setForeground(AppColors.TEXT_MUTED);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        panel.setOpaque(false);
        panel.add(label);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 2, 0));

        return panel;
    }
}
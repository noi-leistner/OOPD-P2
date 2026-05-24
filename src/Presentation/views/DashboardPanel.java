package Presentation.views;

import Business.Entities.Reservation;
import Business.Entities.User;
import Business.SessionManager;
import Presentation.controllers.*;
import Presentation.theme.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Acts as the primary control center and navigation cockpit of the application.
 * <p>
 * This container orchestrates a multi-view workplace layout by splitting screen real estate
 * between a solid branding control sidebar (acting as the application menu) and a dynamic
 * main content space mapped via a card layout structure.
 * It dynamically interrogates global session context states upon setup to determine user authorization levels,
 * populating separate navigation sub-modules for Admin managers versus standard parking Clients.
 */
public class DashboardPanel extends JPanel {

    /** The structural manager tracking visible sub-views within the content dashboard footprint. */
    private final CardLayout cardLayout;

    /** The actual sub-panel repository mapped by card layout indices. */
    private final JPanel contentArea;

    /** Root application coordinator reference used to jump across master view hierarchies. */
    private final MainWindow mainWindow;

    /** The authentication orchestration controller handling system entries and security closures. */
    private final AuthController authController;

    /** Core status monitor managing background simulation parameters and data refresh streams. */
    private final StatusController statusController;

    /** Analysis dashboard engine overseeing spatial occupation calculations and telemetry chart components. */
    private final OccupancyController occupancyController;

    /** Persistence controller facilitating changes to underlying garage floor space configurations. */
    private final ParkingSpaceController slotController;

    /** Registry manager driving reservation allocations, validation checking, and booking records. */
    private final ReservationController reservationController;

    /** Hardware sensor mock facade handling physical garage barrier operations. */
    private final EntryExitController entryExitController;

    /** Dynamic array keeping track of sidebar navigation navigation items for unified click highlighting. */
    private final List<JButton> buttons = new java.util.ArrayList<>();

    /** The targeted default navigation entry initialized on system load based on user access scopes. */
    private JButton initialButton;

    /** Embedded view layout allowing administrative modifications to garage spaces. */
    private ManageSlotsPanel manageSlotsPanel;

    /** Live grid reporting sensor metrics and booking status details. */
    private CurrentStatusPanel currentStatusPanel;

    /** Administrative tracking interface summarizing total consumer booking metrics. */
    private ManageBookingsPanel manageBookingsPanel;

    /** Client console managing targeted slot space reservations. */
    private ManageReservationsPanel  manageReservationsPanel;

    /**
     * Instantiates a baseline layout dashboard ready for structural rendering.
     * Maps essential upstream business controllers and builds internal card layouts to handle nested views.
     *
     * @param mainWindow            the parent top-level window layout wrapper
     * @param authController        the authentication bridge pipeline
     * @param slotController        the slot modification controller
     * @param reservationController the booking registry controller
     * @param statusController       the simulation manager controller
     * @param entryExitController   the hardware mock logic module
     * @param occupancyController   the chart generation metrics tracker
     */
    public DashboardPanel(MainWindow mainWindow, AuthController authController, ParkingSpaceController slotController, ReservationController reservationController, StatusController statusController, EntryExitController entryExitController, OccupancyController occupancyController) {
        this.slotController = slotController;
        this.mainWindow = mainWindow;
        this.authController = authController;
        this.statusController = statusController;
        this.reservationController = reservationController;
        this.entryExitController = entryExitController;
        this.occupancyController = occupancyController;

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        add(contentArea, BorderLayout.CENTER);
    }

    /**
     * Triggers a comprehensive reconstruction of the dashboard framework components.
     * <p>
     * This operation scrubs stale components, purges expired bookings from the database engines,
     * rebuilds the left side menu layout according to the current session profile rules, flashes relevant push warnings
     * regarding revoked client slots, and wires real-time asynchronous callbacks into the simulation engine loop thread.
     */
    public void refresh() {
        reservationController.deleteExpiredReservations();

        removeAll();
        buttons.clear();
        contentArea.removeAll();

        add(buildSidebar(), BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        showCancelledReservationNotification();

        statusController.startSimulation();

        statusController.setSimulationCallback(() -> {
            if (manageSlotsPanel != null && manageSlotsPanel.isShowing())   manageSlotsPanel.refreshTable();
            if (currentStatusPanel != null && currentStatusPanel.isShowing()) currentStatusPanel.loadData();
            if (occupancyController.getView() != null && occupancyController.getView().isShowing()) occupancyController.refreshChart();
            contentArea.repaint();
            revalidate();
        });

        revalidate();
        repaint();
    }

    /**
     * Assembles the main dark sidebar. Evaluates runtime permissions to lock or unlock admin controls,
     * populates sub-panel views inside the local map, and fires focus switches toward initial views.
     *
     * @return a completed, self-rendering control column panel instance
     */
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
            addButton(sidebar, "Log Out", "LOGOUT");

            manageSlotsPanel = new ManageSlotsPanel(slotController, reservationController);
            manageSlotsPanel.refreshTable();

            contentArea.add(occupancyController.getView(), "OCCUPANCY");
            manageBookingsPanel = new ManageBookingsPanel(reservationController, slotController);
            manageBookingsPanel.refreshTable();
            contentArea.add(manageSlotsPanel,   "SLOTS");
            contentArea.add(manageBookingsPanel,   "BOOKINGS");
            currentStatusPanel = new CurrentStatusPanel(statusController, reservationController, authController);
            currentStatusPanel.loadData();
            contentArea.add(currentStatusPanel,   "STATUS");
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
            manageReservationsPanel = new ManageReservationsPanel(reservationController, slotController, entryExitController);
            manageReservationsPanel.refreshTable();
            contentArea.add(manageReservationsPanel, "RESERVE");
            contentArea.add(occupancyController.getView(), "OCCUPANCY");
            currentStatusPanel = new CurrentStatusPanel(statusController, reservationController, authController);
            currentStatusPanel.loadData();
            contentArea.add(currentStatusPanel,  "STATUS");
            contentArea.add(new LogOutPanel(mainWindow, authController, reservationController, slotController, entryExitController), "LOGOUT");
            sidebar.add(Box.createVerticalGlue());
            sidebar.add(buildDivider());
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));


            showContent("VEHICLE_ENTRY");
            highlightButton(initialButton);
        }

        return sidebar;
    }

    /**
     * Builds a header container hosting the non-interactive application branding title text.
     *
     * @return a text header branding element decorated with a bottom matte trim line
     */
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

    /**
     * Appends a flat, styled menu item button to the structural menu layout column.
     * Pairs the choice up with mouse switch callbacks to manipulate active card stacks.
     *
     * @param sidebar     the layout panel receiving the constructed component button
     * @param label       the text description shown over the button body face
     * @param contentName the structural string key mapping directly to a target component card
     */
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

    /**
     * Toggles layout background paint shades across the button list collection.
     * Ensures only the currently viewed sub-module highlights in the menu column.
     *
     * @param selected the target button element representing the visible layout area
     */
    private void highlightButton(JButton selected) {
        for (JButton btn : buttons) {
            btn.setBackground(AppColors.BUTTON_DEFAULT);
            btn.setForeground(Color.BLACK);
        }

        selected.setBackground(AppColors.LIGHT_BLUE);
        selected.setForeground(Color.WHITE);
    }

    /**
     * Directs the local card layout manager to bring an internal sub-view panel forward.
     *
     * @param name the registered key identity of the layout panel choice to show
     */
    public void showContent(String name) {
        cardLayout.show(contentArea, name);
    }

    /**
     * Scans for system cancellations affecting the currently logged-in client.
     * <p>
     * If forced cleanups are discovered, this compiles a descriptive alert manifest list,
     * launches a warning modal message dialog window, and deletes the alert records.
     */
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

    /**
     * Instantiates a low-profile separator block line element used to cleanly partition control sets.
     *
     * @return a panel segment configured with customized horizontal rule graphics painting
     */
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

    /**
     * Builds a text label to organize the menu column sections into structural groupings.
     *
     * @param text the heading text string
     * @return a clean descriptive metadata title container label panel instance
     */
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
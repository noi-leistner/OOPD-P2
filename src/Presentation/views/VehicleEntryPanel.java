package Presentation.views;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.SessionManager;
import Presentation.controllers.EntryExitController;
import Presentation.controllers.ParkingSpaceController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class VehicleEntryPanel extends JPanel {

    private final EntryExitController entryExitController;

    // Step 1 — plate input
    private JPanel stepOnePanel;
    private JTextField plateField;

    // Step 2 — space selection
    private JPanel stepTwoPanel;
    private JComboBox<SpaceItem> spaceCombo;
    private JLabel vehicleTypeLabel;
    private JLabel noSpacesLabel;

    // Shared
    private JLabel statusLabel;
    private String currentPlate;
    private String currentVehicleType;

    public VehicleEntryPanel(EntryExitController controller) {
        this.entryExitController = controller;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        stepOnePanel = buildStepOne();
        stepTwoPanel = buildStepTwo();
        stepTwoPanel.setVisible(false);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(stepOnePanel);
        wrapper.add(stepTwoPanel);
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(statusLabel);

        add(wrapper, BorderLayout.NORTH);
    }

    // -------------------------------------------------------------------------
    // Step 1 — plate input
    // -------------------------------------------------------------------------

    private JPanel buildStepOne() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Vehicle Entry");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your license plate to park your vehicle.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel plateLabel = new JLabel("License plate: ");
        plateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        plateField = new JTextField(14);
        plateField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton checkBtn = buildPrimaryButton("Check");
        checkBtn.addActionListener(e -> onCheck());

        row.add(plateLabel);
        row.add(plateField);
        row.add(Box.createHorizontalStrut(12));
        row.add(checkBtn);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(row);

        return panel;
    }

    // -------------------------------------------------------------------------
    // Step 2 — space selection (vehicle type is auto-detected, not chosen)
    // -------------------------------------------------------------------------

    private JPanel buildStepTwo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("No reservation found. Please select a space.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Vehicle type — display only, not selectable
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        vehicleTypeLabel = new JLabel("Vehicle type: ");
        vehicleTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        typeRow.add(vehicleTypeLabel);

        // Space selection
        JPanel spaceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spaceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel spaceLabel = new JLabel("Available space: ");
        spaceLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        spaceCombo = new JComboBox<>();
        spaceCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        spaceCombo.setPreferredSize(new Dimension(220, 28));

        spaceRow.add(spaceLabel);
        spaceRow.add(spaceCombo);

        noSpacesLabel = new JLabel("No available spaces for this vehicle type.");
        noSpacesLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        noSpacesLabel.setForeground(AppColors.RED);
        noSpacesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        noSpacesLabel.setVisible(false);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton confirmBtn = buildPrimaryButton("Confirm Entry");
        confirmBtn.addActionListener(e -> onConfirm());

        JButton backBtn = buildSecondaryButton("Back");
        backBtn.addActionListener(e -> resetToStepOne());

        btnRow.add(confirmBtn);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(backBtn);

        panel.add(Box.createVerticalStrut(25));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(20));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(16));
        panel.add(typeRow);
        panel.add(Box.createVerticalStrut(12));
        panel.add(spaceRow);
        panel.add(noSpacesLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnRow);

        return panel;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    private void onCheck() {
        String plate = plateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            showError("Please enter a license plate.");
            return;
        }

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();

            if (!entryExitController.vehicleBelongsToUser(plate, userId)) {
                // Check if it belongs to someone else
                if (entryExitController.vehiclePlateExistsInSystem(plate)) {
                    showError("This vehicle is registered to another account.");
                    return;
                }
                // Plate doesn't exist at all — offer to register it
                AddVehicleDialog dialog = new AddVehicleDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this), plate);
                if (!dialog.isConfirmed()) return;

                boolean added = entryExitController.registerVehicle(plate, userId, dialog.getSelectedType());
                if (!added) {
                    showError("Failed to register vehicle. Please try again.");
                    return;
                }
            }

            currentPlate = plate;
            clearStatus();

            if (entryExitController.isVehicleCurrentlyParked(plate)) {
                showError("This vehicle is already parked.");
                return;
            }

            if (entryExitController.hasReservationNow(plate)) {
                // Has a reservation — ask for confirmation first
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "<html><b>Reservation found</b> for plate <b>" + plate + "</b>.<br>" +
                                "You will be assigned your reserved parking space.<br><br>" +
                                "Proceed?</html>",
                        "Reservation Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) return;

                ParkingSpace space = entryExitController.getReservedSpaceForPlate(plate);
                String occupantPlate = null;
                if (space.isOccupied()) {
                    occupantPlate = entryExitController.getParkedPlateAtSpace(space.getId());

                    JOptionPane.showMessageDialog(this,
                            "<html>Your reserved space <b>#" + space.getId() + "</b> is occupied and no alternative spaces are available.<br>" +
                                    "The other user will be kicked out of the parking spot.</html>",
                            "No Alternative Available",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                ParkingSpace entered = entryExitController.enterWithReservation(plate, userId);
                if (entered != null) {
                    if (space.isOccupied()) {
                        entryExitController.exitParking(occupantPlate, userId);
                    }
                    showSuccess("✓  Parked at reserved space #" + space.getId() + "  (Floor " + space.getFloor() + ")");
                    plateField.setText("");
                } else {
                    showError("Your reserved space is currently occupied. Please contact an administrator.");
                }
            } else {
                // No reservation — auto-detect type and show space selection
                String vehicleType = entryExitController.getVehicleType(plate);
                if (vehicleType == null) {
                    showError("Could not determine vehicle type.");
                    return;
                }
                showStepTwo(vehicleType);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error: " + ex.getMessage());
        }
    }

    private void onConfirm() {
        SpaceItem selected = (SpaceItem) spaceCombo.getSelectedItem();
        if (selected == null) {
            showError("Please select a parking space.");
            return;
        }

        int userId = SessionManager.getInstance().getCurrentUser().getId();

        Reservation reservation = entryExitController.getFirstReservationForSpace(selected.id);

        if (reservation != null) {
            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(reservation.getStartDateTime());
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "<html>Space <b>#" + selected.id + "</b> is reserved by another user.<br>" +
                            "Your vehicle will be <b>removed on " + dateStr + "</b> when the reservation begins.<br><br>" +
                            "Do you want to continue with this space, or choose another one?</html>",
                    "Space Has a Reservation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null
            );
            if (choice != JOptionPane.YES_OPTION) return;
        } else {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "<html>Space <b>#" + selected.id + "</b> (Floor " + selected.floor + ") has no reservation.<br>" +
                            "Confirm entry?</html>",
                    "Confirm Entry",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) return;
        }

        ParkingSpace space = entryExitController.enterWithoutReservation(currentPlate, selected.id, userId);

        if (space != null) {
            showSuccess("✓  Parked at space #" + space.getId() + "  (Floor " + space.getFloor() + ")");
            resetToStepOne();
        } else {
            showError("Space #" + selected.id + " was just taken. Please choose another.");
            refreshSpaceCombo(currentVehicleType);
        }
    }

    private void showStepTwo(String vehicleType) {
        currentVehicleType = vehicleType;
        vehicleTypeLabel.setText("Vehicle type: " + vehicleType);
        stepTwoPanel.setVisible(true);
        refreshSpaceCombo(vehicleType);
        revalidate();
        repaint();
    }

    private void refreshSpaceCombo(String vehicleType) {
        List<ParkingSpace> spaces = entryExitController.getAvailableSpacesForType(vehicleType);
        spaceCombo.removeAllItems();

        if (spaces == null || spaces.isEmpty()) {
            spaceCombo.setVisible(false);
            noSpacesLabel.setVisible(true);
        } else {
            noSpacesLabel.setVisible(false);
            spaceCombo.setVisible(true);
            for (ParkingSpace s : spaces) {
                spaceCombo.addItem(new SpaceItem(s.getId(), s.getFloor(), s.getType()));
            }
        }
    }

    private void resetToStepOne() {
        stepTwoPanel.setVisible(false);
        plateField.setText("");
        currentPlate = null;
        currentVehicleType = null;
        clearStatus();
        revalidate();
        repaint();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showSuccess(String msg) {
        statusLabel.setForeground(new Color(0, 140, 0));
        statusLabel.setText(msg);
    }

    private void showError(String msg) {
        statusLabel.setForeground(AppColors.RED);
        statusLabel.setText(msg);
    }

    private void clearStatus() {
        statusLabel.setText(" ");
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        return btn;
    }

    private JButton buildSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        return btn;
    }

    // -------------------------------------------------------------------------
    // SpaceItem — display wrapper for the JComboBox
    // -------------------------------------------------------------------------

    private static class SpaceItem {
        final int id;
        final int floor;
        final String type;

        SpaceItem(int id, int floor, String type) {
            this.id = id;
            this.floor = floor;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Space #" + id + "  —  Floor " + floor + "  (" + type + ")";
        }
    }
}
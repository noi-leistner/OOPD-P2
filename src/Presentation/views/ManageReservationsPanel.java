package Presentation.views;

import Business.DaoResult;
import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.Entities.User;
import Business.Entities.Vehicle;
import Business.SessionManager;
import Presentation.controllers.EntryExitController;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.ReservationController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageReservationsPanel extends BaseManagePanel {
    private final ReservationController reservationController;
    private final ParkingSpaceController slotController;
    private final EntryExitController entryExitController;

    private DefaultTableModel tableModel;
    private JTable table;
    private Reservation selectedReservation;
    private List<Reservation> currentReservations = new ArrayList<>();

    public ManageReservationsPanel(ReservationController reservationController, ParkingSpaceController slotController, EntryExitController entryExitController) {
        this.reservationController = reservationController;
        this.slotController = slotController;
        this.entryExitController = entryExitController;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    private JPanel buildButtonArea() {
        JButton addResBtn = buildButton("Make Reservation");
        addResBtn.addActionListener(e -> showAddReservationDialog());

        JButton editResBtn = buildButton("Edit Reservation");
        editResBtn.addActionListener(e -> {
            if (selectedReservation == null) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            showEditReservationDialog(selectedReservation);
        });

        JButton cancelResBtn = buildButton("Cancel Reservation");
        cancelResBtn.addActionListener(e -> {
            if (selectedReservation == null) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cancel reservation " + selectedReservation.getId() + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                DaoResult cancelResult = reservationController.cancelReservation(selectedReservation.getId());
                if (cancelResult != DaoResult.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "Failed to cancel reservation.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                switch (slotController.removeReservationFromSlot(selectedReservation.getParking_slot_id())) {
                    case SUCCESS -> {
                        JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
                        refreshTable();
                    }
                    case NOT_FOUND      -> JOptionPane.showMessageDialog(this, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR -> JOptionPane.showMessageDialog(this, "Something went wrong updating the slot.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshTable();
            }
        });

        return buildButtonArea("Manage Bookings", addResBtn, editResBtn, cancelResBtn);
    }

    private JScrollPane buildTable() {
        String[] columns = {"User Id", "Plate", "Slot", "Type", "Start Date", "End Date"};
        tableModel = buildTableModel(columns);
        table = new JTable(tableModel);

        return buildTable(columns, tableModel, table, () -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < currentReservations.size()) {
                selectedReservation = currentReservations.get(row);
            }
        });
    }

    public void refreshTable() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        java.util.List<Reservation> reservations = reservationController.getReservationsByUserId(currentUser.getId());
        loadData(reservations);
    }

    public void loadData(List<Reservation> reservations) {
        currentReservations = reservations;
        tableModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            ParkingSpace space = slotController.getSpaceDetails(reservation.getParking_slot_id());
            tableModel.addRow(new Object[]{
                    reservation.getUser_id(),
                    reservation.getVehiclePlate(),
                    space != null ? space.getId() : "N/A",
                    space != null ? space.getType() : "N/A",
                    reservation.getEndDateTime(),
                    reservation.getEndDateTime()
            });
        }
    }

    private void showAddReservationDialog() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog("Make Reservation", new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        JLabel titleLabel = new JLabel("Edit Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        //Get car id
        JTextField licenseField = new JTextField(15);
        addField(formPanel, "Vehicle license plate:", licenseField);

        //Get vehicle type
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        addField(formPanel, "Vehicle type:", typeCombo);

        //combobox with available spots for type
        JComboBox<ParkingSpace> spotsCombo = new JComboBox<>();
        addField(formPanel, "Available Spots:", spotsCombo);

        updateSpotsCombo(spotsCombo, (String) typeCombo.getSelectedItem(), dialog);

        typeCombo.addActionListener(e ->
                updateSpotsCombo(spotsCombo, (String) typeCombo.getSelectedItem(), dialog)
        );

        // start date
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        JSpinner startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        addField(formPanel, "Start date:", startDateSpinner);

        // start time
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        JSpinner startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        addField(formPanel, "Start time:", startTimeSpinner);

        // end date
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        JSpinner endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy"));
        addField(formPanel, "End date:", endDateSpinner);

        // end time
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        JSpinner endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        addField(formPanel, "End time:", endTimeSpinner);

        okBtn.addActionListener(e -> {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            String plate = licenseField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();

            if (plate.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a license plate.");
                return;
            }

            if (entryExitController.vehicleExistsForOtherUser(plate, currentUser.getId())) {
                JOptionPane.showMessageDialog(dialog,
                        "This vehicle is registered to another user.",
                        "Vehicle conflict",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle existing = entryExitController.ensureVehicleExists(plate, type, currentUser.getId());

            if (existing == null) {
                JOptionPane.showMessageDialog(dialog, "Something went wrong registering the vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!existing.getType().equalsIgnoreCase(type)) {
                JOptionPane.showMessageDialog(dialog,
                        "This vehicle is already registered as type: " + existing.getType() + ". Type cannot be changed.",
                        "Vehicle conflict",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date start = combineDateAndTime(startDateSpinner, startTimeSpinner);
            Date end   = combineDateAndTime(endDateSpinner, endTimeSpinner);

            if (!end.after(start)) {
                JOptionPane.showMessageDialog(dialog,
                        "End date and time must be after start date and time.",
                        "Invalid dates",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (start.before(new Date())) {
                JOptionPane.showMessageDialog(dialog,
                        "Start date and time cannot be in the past.",
                        "Invalid dates",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Reservation newReservation = new Reservation(
                    currentUser.getId(),
                    licenseField.getText(),
                    ((ParkingSpace) spotsCombo.getSelectedItem()).getId(),
                    start,
                    end,
                    false
            );

            //TODO: set spot as reserved
            switch (reservationController.makeReservation(newReservation)) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(dialog, "Reservation added!");
                    dialog.dispose();
                    refreshTable();
                }
                case ALREADY_EXISTS -> JOptionPane.showMessageDialog(dialog, "This reservation would overlap with another!.", "Error", JOptionPane.ERROR_MESSAGE);
                case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditReservationDialog(Reservation reservation) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog("Edit Reservation", new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        ParkingSpace currentSpot = slotController.getSpaceDetails(reservation.getParking_slot_id());

        // start date
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        JSpinner startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        startDateSpinner.setValue(reservation.getStartDateTime());

        // start time
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        JSpinner startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        startTimeSpinner.setValue(reservation.getStartDateTime());

        // end date
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        JSpinner endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy"));
        endDateSpinner.setValue(reservation.getEndDateTime());

        // end time
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        JSpinner endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        endTimeSpinner.setValue(reservation.getEndDateTime());

        List<ParkingSpace> availableSpots = slotController.getAvailableSpotsByType(currentSpot.getType());

        if (availableSpots == null) {
            availableSpots = new ArrayList<>();
        }

        //TODO: current spot appearing twice
        if (!availableSpots.contains(currentSpot)) {
            availableSpots.add(0, currentSpot);
        }

        JComboBox<ParkingSpace> spotsCombo = new JComboBox<>(availableSpots.toArray(new ParkingSpace[0]));


        spotsCombo.setSelectedItem(currentSpot);

        JLabel titleLabel = new JLabel("Edit Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        addField(formPanel, "Slot identifier:", spotsCombo);
        addField(formPanel, "Start date:", startDateSpinner);
        addField(formPanel, "Start time:", startTimeSpinner);
        addField(formPanel, "End date:", endDateSpinner);
        addField(formPanel, "End time:", endTimeSpinner);

        okBtn.addActionListener(e -> {
            Date start = combineDateAndTime(startDateSpinner, startTimeSpinner);
            Date end   = combineDateAndTime(endDateSpinner, endTimeSpinner);

            if (!end.after(start)) {
                JOptionPane.showMessageDialog(dialog,
                        "End date and time must be after start date and time.",
                        "Invalid dates",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (start.before(new Date())) {
                JOptionPane.showMessageDialog(dialog,
                        "Start date and time cannot be in the past.",
                        "Invalid dates",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Reservation editedReservation = new Reservation(reservation.getId(), reservation.getUser_id(), reservation.getVehiclePlate(), ((ParkingSpace) spotsCombo.getSelectedItem()).getId(), start, end, false);

            switch (reservationController.editReservation(editedReservation)) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(dialog, "Reservation edited!");
                    dialog.dispose();
                    refreshTable();
                }
                case NOT_FOUND -> JOptionPane.showMessageDialog(dialog, "Reservation not found.", "Error", JOptionPane.WARNING_MESSAGE);
                case ALREADY_EXISTS -> JOptionPane.showMessageDialog(dialog, "This reservation would overlap with another!.", "Error", JOptionPane.ERROR_MESSAGE);
                case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateSpotsCombo(JComboBox<ParkingSpace> spotsCombo, String type, JDialog dialog) {
        List<ParkingSpace> availableSpots = slotController.getAvailableSpotsByType(type);

        spotsCombo.removeAllItems();

        if (availableSpots == null || availableSpots.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "There are no parking spots available for this type!");
            return;
        }

        for (ParkingSpace space : availableSpots) {
            spotsCombo.addItem(space);
        }
    }
}

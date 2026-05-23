package Presentation.views;

import Business.DaoResult;
import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.ReservationController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An admin dashboard panel for handling customer parking bookings.
 * Lets you view a list of all current reservations, edit booking
 * timelines/assigned spaces, or cancel reservations entirely.
 */
public class ManageBookingsPanel extends BaseManagePanel {
    private final ReservationController reservationController;
    private final ParkingSpaceController slotController;

    private DefaultTableModel tableModel;
    private JTable table;
    private Reservation selectedReservation;
    private List<Reservation> currentReservations = new ArrayList<>();

    /**
     * Initializes the manager panel layout, sets up columns,
     * and maps out the action buttons.
     */
    public ManageBookingsPanel(ReservationController reservationController, ParkingSpaceController slotController) {
        this.reservationController = reservationController;
        this.slotController = slotController;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    /**
     * Creates the upper control bar. Sets up action listeners to make sure
     * a row is actually selected before attempting an edit or cancellation.
     */
    private JPanel buildButtonArea() {
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
                DaoResult cancelResult = reservationController.cancelReservationWithNotification(selectedReservation.getId());
                if (cancelResult != DaoResult.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "Failed to cancel reservation.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
                }
                refreshTable();
            }
        });

        return buildButtonArea("Manage Bookings", editResBtn, cancelResBtn);
    }

    /**
     * Configures the main data table framework and tracks row selection adjustments
     * so the control buttons know exactly which reservation is active.
     */
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

    /**
     * Pulls the latest reservation list from the database controller
     * and triggers a visual table redraw.
     */
    public void refreshTable() {
        List<Reservation> reservations = reservationController.getAllReservations();
        loadData(reservations);
    }

    /**
     * Wipes the existing table data rows and rebuilds them row-by-row,
     * matching up the raw spot IDs with detailed layout data like spot types.
     */
    public void loadData(List<Reservation> reservations) {
        currentReservations = reservations;
        tableModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            ParkingSpace space = slotController.getSpaceDetails(reservation.getParkingSlotId());
            tableModel.addRow(new Object[]{
                    reservation.getUserId(),
                    reservation.getVehiclePlate(),
                    space != null ? space.getId() : "N/A",
                    space != null ? space.getType() : "N/A",
                    reservation.getStartDateTime(),
                    reservation.getEndDateTime()
            });
        }
    }

    /**
     * Pops open an editor dialog window. Gives admins time-spinners to update
     * start/end markers and a dropdown menu to select alternative spots, verifying
     * that reservation time windows don't overlap or fall into the past.
     */
    private void showEditReservationDialog(Reservation reservation) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog("Edit Reservation", new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        ParkingSpace currentSpot = slotController.getSpaceDetails(reservation.getParkingSlotId());

        // start date
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        JSpinner startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        addField(formPanel, "Start date:", startDateSpinner);
        startDateSpinner.setValue(reservation.getStartDateTime());

        // start time
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        JSpinner startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        addField(formPanel, "Start time:", startTimeSpinner);
        startTimeSpinner.setValue(reservation.getStartDateTime());

        // end date
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        JSpinner endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy"));
        addField(formPanel, "End date:", endDateSpinner);
        endDateSpinner.setValue(reservation.getEndDateTime());

        // end time
        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        JSpinner endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        addField(formPanel, "End time:", endTimeSpinner);
        endTimeSpinner.setValue(reservation.getEndDateTime());

        List<ParkingSpace> availableSpots = slotController.getSpotsByType(currentSpot.getType());

        if (availableSpots == null) {
            availableSpots = new ArrayList<>();
        }

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

            Reservation editedReservation = new Reservation(reservation.getId(), reservation.getUserId(), reservation.getVehiclePlate(), ((ParkingSpace) spotsCombo.getSelectedItem()).getId(), start, end, false);

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
}

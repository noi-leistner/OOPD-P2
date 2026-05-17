package Presentation.views;

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

public class ManageBookingsPanel extends BaseManagePanel {
    private ReservationController reservationController;
    private ParkingSpaceController slotController;

    private DefaultTableModel tableModel;
    private JTable table;
    private Reservation selectedReservation;
    private List<Reservation> currentReservations = new ArrayList<>();

    public ManageBookingsPanel(ReservationController reservationController, ParkingSpaceController slotController) {
        this.reservationController = reservationController;
        this.slotController = slotController;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

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
                reservationController.cancelReservationFromAdmin(selectedReservation.getId());
                refreshTable();
            }
        });

        return buildButtonArea("Manage Bookings", editResBtn, cancelResBtn);
    }

    private JScrollPane buildTable() {
        String[] columns = {"User Id", "Plate", "Slot", "Type", "Date"};
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
        List<Reservation> reservations = reservationController.getAllReservations();
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
                    reservation.getDate()
            });
        }
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

        //TODO: add time logic (not only date)
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        dateSpinner.setValue(reservation.getDate());

        List<ParkingSpace> availableSpots = slotController.getAvailableSpotsByType(currentSpot.getType());

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
        addField(formPanel, "Date:", dateSpinner);

        okBtn.addActionListener(e -> {
            Reservation editedReservation = new Reservation(reservation.getId(), reservation.getUser_id(), reservation.getVehiclePlate(), ((ParkingSpace) spotsCombo.getSelectedItem()).getId(), (Date) dateSpinner.getValue(), false);

            switch (reservationController.editReservation(editedReservation)) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(dialog, "Reservation edited!");
                    dialog.dispose();
                    refreshTable();
                }
                case NOT_FOUND -> JOptionPane.showMessageDialog(dialog, "Reservation not found.", "Error", JOptionPane.WARNING_MESSAGE);
                case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

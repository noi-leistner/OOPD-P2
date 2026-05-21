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

public class ManageSlotsPanel extends BaseManagePanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private ParkingSpaceController slotController;
    private ReservationController reservationController;

    private ParkingSpace selectedSpace;
    private List<ParkingSpace> currentSpaces = new ArrayList<>();

    public ManageSlotsPanel(ParkingSpaceController slotController, ReservationController reservationController) {
        this.slotController = slotController;
        this.reservationController = reservationController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    private JPanel buildButtonArea() {
        JButton addBtn = buildButton("Add Slot");
        addBtn.addActionListener(e -> showSlotInfoDialog("Add slot", null));

        JButton editBtn = buildButton("Edit Slot");
        editBtn.addActionListener(e -> {
            if (selectedSpace == null) {
                JOptionPane.showMessageDialog(this, "Please select a slot first.");
                return;
            }
            showSlotInfoDialog("Edit slot", selectedSpace);
        });

        JButton removeBtn = buildButton("Remove Slot");
        removeBtn.addActionListener(e -> {
            if (selectedSpace == null) {
                JOptionPane.showMessageDialog(this, "Please select a slot first.");
                return;
            }
            showRemoveSlotDialog(selectedSpace.getId());
        });

        return buildButtonArea("Parking Slots", addBtn, editBtn, removeBtn);
    }

    private JScrollPane buildTable() {
        String[] columns = {"Code", "Floor", "Current Status", "Reservation Status", "Type"};
        tableModel = buildTableModel(columns);
        table = new JTable(tableModel);

        return buildTable(columns, tableModel, table, () -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < currentSpaces.size()) {
                selectedSpace = currentSpaces.get(row);
            }
        });
    }

    public void refreshTable() {
        List<ParkingSpace> spaces = slotController.getAllSpaces();
        loadData(spaces);
    }

    public void loadData(List<ParkingSpace> spaces) {
        currentSpaces = spaces;
        tableModel.setRowCount(0);

        for (ParkingSpace space : spaces) {
            Reservation reservation = reservationController.getActiveReservationForPlate(space.getParkedLicensePlate());

            tableModel.addRow(new Object[]{
                    space.getId(),
                    space.getFloor(),
                    space.isOccupied() ? "Occupied" : "Free",
                    reservation != null ? "Reserved" : "Unreserved",
                    space.getType()
            });
        }
    }

    private void showSlotInfoDialog(String text, ParkingSpace space) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog(text, new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});

        JTextField idField = new JTextField(15);

        List<Integer> availableFloors;
        if (space != null) {
            idField.setText(String.valueOf(space.getId()));
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);

            availableFloors = slotController.getAvailableFloors(space.getFloor());
            typeCombo.setSelectedItem(space.getType());
        } else {
            availableFloors = slotController.getAvailableFloors(-1);
            if (availableFloors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All floors are full.");
                return;
            }
        }

        JComboBox<Integer> floorCombo = new JComboBox<>(availableFloors.toArray(new Integer[0]));
        if (space != null) {
            floorCombo.setSelectedItem(space.getFloor());
        }

        JLabel titleLabel = new JLabel(text.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        addField(formPanel, "Slot identifier:", idField);
        addField(formPanel, "Floor:", floorCombo);
        addField(formPanel, "Vehicle type:", typeCombo);

        if (space != null) {
            formPanel.add(new JLabel("Reservation status:"));
            formPanel.add(buildReservationInfo(space));
        }

        okBtn.addActionListener(e -> {
            if (idField.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in the ID field!");
                return;
            }


            int code = Integer.parseInt(idField.getText());
            int floor = (Integer) floorCombo.getSelectedItem();
            String vehicleType = (String) typeCombo.getSelectedItem();

            if (space != null) {
                // Edit existing slot

                boolean typeChanged = !space.getType().equals(vehicleType);
                boolean isOccupied = space.isOccupied();
                boolean isReserved = !reservationController.getReservationsBySlotId(space.getId()).isEmpty();

                if (typeChanged && (isOccupied || isReserved)) {
                    JOptionPane.showMessageDialog(dialog, "The slot type of a reserved or occupied space can't be changed!");
                    return;
                }

                switch (slotController.editSpace(code, floor, vehicleType)) {
                    case SUCCESS -> {
                        JOptionPane.showMessageDialog(dialog, "Slot edited!");
                        dialog.dispose();
                        refreshTable();
                    }
                    case NOT_FOUND ->
                            JOptionPane.showMessageDialog(dialog, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR ->
                            JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Add new slot
                switch (slotController.addSpace(code, floor, vehicleType, false)) {
                    case SUCCESS -> {
                        JOptionPane.showMessageDialog(dialog, "Slot added!");
                        dialog.dispose();
                        refreshTable();
                    }
                    case ALREADY_EXISTS ->
                            JOptionPane.showMessageDialog(dialog, "A slot with this ID already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR ->
                            JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JComponent buildReservationInfo(ParkingSpace space) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        List<Reservation> reservations = reservationController.getReservationsBySlotId(space.getId());
        if (reservations.isEmpty()) {
            panel.add(new JLabel("      No reservations found."));
        } else {
            for (Reservation reservation : reservations) {
                panel.add(new JLabel("      Vehicle Plate: " + reservation.getVehiclePlate()));
                panel.add(new JLabel("      Start Date: " + reservation.getStartDateTime()));
                panel.add(new JLabel("      End Date: " + reservation.getEndDateTime()));
                panel.add(Box.createVerticalStrut(10));
            }
            panel.add(new JLabel("To edit or cancel the reservation go to Manage Bookings!"));
        }

        JScrollPane scrollPane = new JScrollPane(panel);

        Dimension fixedSize = new Dimension(300, 120);

        scrollPane.setPreferredSize(fixedSize);
        scrollPane.setMinimumSize(fixedSize);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    private void showRemoveSlotDialog(int spaceId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete slot " + spaceId + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean hasReservation = reservationController.getReservationsBySlotId(spaceId)
                    .stream().anyMatch(r -> r.getStartDateTime().before(new Date()) && r.getEndDateTime().after(new Date()));
            switch (slotController.removeSpace(spaceId, hasReservation)) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(this, "Slot removed!");
                    refreshTable();
                }
                case CANNOT_REMOVE_OCCUPIED ->
                        JOptionPane.showMessageDialog(this, "Slot is occupied and no alternative spaces are available.\nThe slot can not be removed!", "Cannot Remove", JOptionPane.WARNING_MESSAGE);
                case NOT_FOUND ->
                        JOptionPane.showMessageDialog(this, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                case DATABASE_ERROR ->
                        JOptionPane.showMessageDialog(this, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
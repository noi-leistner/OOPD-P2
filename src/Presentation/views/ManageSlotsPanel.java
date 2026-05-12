package Presentation.views;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.ReservationController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

import static Business.DaoResult.*;

public class ManageSlotsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private ParkingSpaceController slotController;
    private ReservationController reservationController;

    public ManageSlotsPanel(ParkingSpaceController slotController, ReservationController reservationController) {
        this.slotController = slotController;
        this.reservationController = reservationController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
    }

    private JPanel buildButtonArea() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Parking Slots");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(15));

        JPanel threeButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton addBtn = buildButton("Add Slot");
        addBtn.addActionListener(e -> showSlotInfoDialog("Add slot", null));

        JButton editBtn = buildButton("Edit Slot");
        editBtn.addActionListener(e -> showEditSlotDialog());

        JButton removeBtn = buildButton("Remove Slot");
        removeBtn.addActionListener(e -> showRemoveStatusDialog());

        threeButtons.add(addBtn);
        threeButtons.add(editBtn);
        threeButtons.add(removeBtn);
        wrapper.add(threeButtons);

        wrapper.add(Box.createVerticalStrut(15));
        return wrapper;
    }

    private JButton buildButton(String text) {
        JButton btn = new JButton(text);

        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBackground(AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        return btn;
    }

    private JScrollPane buildTable() {
        String[] columns = { "Code", "Floor", "Current Status", "Reservation Status", "Type" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(table);
    }

    public void refreshTable() {
        List<ParkingSpace> spaces = slotController.getAllSpaces();
        loadData(spaces);
    }

    public void loadData(List<ParkingSpace> spaces) {
        tableModel.setRowCount(0);
        for (ParkingSpace space : spaces) {
            tableModel.addRow(new Object[]{
                    space.getId(),
                    space.getFloor(),
                    space.isOccupied() ? "Occupied" : "Free",
                    space.isReserved() ? "Reserved" : "Unreserved",
                    space.getType()
            });
        }
    }

    private void styleButton(JButton btn, boolean cancel) {
        btn.setBackground(cancel ? AppColors.RED : AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btn.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void showSlotInfoDialog(String text, ParkingSpace space) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog(text, new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        //TODO: only allow floors with enough slots free
        JComboBox<Integer> floorCombo =new JComboBox<>(new Integer[]{0, 1, 2, 3, 4});
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});


        JTextField idField = new JTextField(15);
        if (space != null) {
            idField.setText(String.valueOf(space.getId()));
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);

            floorCombo.setSelectedItem(space.getFloor());
            typeCombo.setSelectedItem(space.getType());
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


            int code  = Integer.parseInt(idField.getText());
            int floor = (Integer) floorCombo.getSelectedItem();
            String vehicleType = (String) typeCombo.getSelectedItem();

            if (space != null) {
                // Edit existing slot
                //TODO: if spaceType changed check if still valid
                boolean isOccupied = slotController.getSpaceDetails(code).isOccupied();
                switch (slotController.editSpace(code, floor, vehicleType, isOccupied)) {
                    case SUCCESS -> {
                        JOptionPane.showMessageDialog(dialog, "Slot edited!");
                        dialog.dispose();
                        refreshTable();
                    }
                    case NOT_FOUND -> JOptionPane.showMessageDialog(dialog, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Add new slot
                switch (slotController.addSpace(code, floor, vehicleType, false, true)) {
                    case SUCCESS -> {
                        JOptionPane.showMessageDialog(dialog, "Slot added!");
                        dialog.dispose();
                        refreshTable();
                    }
                    case ALREADY_EXISTS -> JOptionPane.showMessageDialog(dialog, "A slot with this ID already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildReservationInfo(ParkingSpace space) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        panel.setMaximumSize(new Dimension(450, 120));

        if (!space.isReserved()) {
            panel.add(new JLabel("  No reservation for this spot."));
            return panel;
        }

        Reservation reservation = reservationController.getReservationBySlotId(space.getId());
        if (reservation == null) {
            panel.add(new JLabel("      No reservation found."));
            return panel;
        }

        panel.add(new JLabel("      Vehicle Plate: " + reservation.getVehiclePlate()));
        panel.add(new JLabel("      Date: "    + reservation.getDate()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("To edit or cancel the reservation go to Manage Bookings!"));

        return panel;
    }

    private void showEditSlotDialog() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JTextField idField = new JTextField(10);
        JButton nextBtn = new JButton("Next");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(nextBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog("Edit Slot", new Dimension(350, 160), formPanel, nextBtn, cancelBtn);

        JLabel title = new JLabel("EDIT PARKING SLOT");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(20));
        addField(formPanel, "Enter Slot Identifier:", idField);

        nextBtn.addActionListener(e -> {
            String input = idField.getText().trim();

            if (input.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a slot ID.");
                return;
            }

            try {
                int spaceId = Integer.parseInt(input);

                dialog.dispose();

                ParkingSpace space = slotController.getSpaceDetails(spaceId);
                if (space != null) {
                    showSlotInfoDialog("Edit slot", space);
                } else {
                    JOptionPane.showMessageDialog(null, "There is no parking space with this ID.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Slot ID must be a number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRemoveStatusDialog() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        JTextField idField = new JTextField(15);
        JButton removeBtn = new JButton("Remove");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(removeBtn, true);
        styleButton(cancelBtn, false);

        JDialog dialog = createBaseDialog("Remove Slot", new Dimension(350, 160), formPanel, removeBtn, cancelBtn);

        JLabel title = new JLabel("REMOVE PARKING SLOT");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(20));
        addField(formPanel, "Enter Slot Identifier to remove:", idField);

        removeBtn.addActionListener(e -> {
            String input = idField.getText().trim();
            if (input.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a slot ID.");
                return;
            }

            try {
                int spaceId = Integer.parseInt(input);

                if (!slotController.slotExists(spaceId)) {
                    JOptionPane.showMessageDialog(dialog, "There is no parking space with this ID.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(dialog, "Delete slot " + spaceId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    switch (slotController.removeSpace(spaceId)) {
                        case SUCCESS        -> {
                            JOptionPane.showMessageDialog(dialog, "Slot removed!");
                            dialog.dispose();
                            refreshTable();
                        }
                        case NOT_FOUND      -> JOptionPane.showMessageDialog(dialog, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                        case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Slot ID must be a number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JDialog createBaseDialog(String title, Dimension size, JPanel formPanel, JButton actionBtn, JButton cancelBtn) {
        JDialog dialog = new JDialog((Frame) null, title, true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        Dimension btnSize = new Dimension(100, 35);

        actionBtn.setPreferredSize(btnSize);
        cancelBtn.setPreferredSize(btnSize);

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(actionBtn);
        buttonPanel.add(cancelBtn);

        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(10)); // small spacing

        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);

        return dialog;
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);

        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension fieldSize = new Dimension(450, field.getPreferredSize().height);

        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);

        panel.add(jLabel);
        panel.add(Box.createVerticalStrut(5));

        panel.add(field);

        panel.add(Box.createVerticalStrut(15));
    }
}
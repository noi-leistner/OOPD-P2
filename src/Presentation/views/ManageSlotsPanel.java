package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.ParkingSpaceController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSlotsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private ParkingSpaceController slotController;

    public ManageSlotsPanel(ParkingSpaceController slotController) {
        this.slotController = slotController;
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
        wrapper.add(Box.createVerticalStrut(15));   // Spacing between title and buttons

        JPanel threeButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton addBtn = buildButton("Add Slot");
        addBtn.addActionListener(e -> showSlotInfoDialog("Add slot", null));

        JButton editBtn = buildButton("Edit Slot");
        editBtn.addActionListener(e -> showEditSlotDialog());

        JButton removeBtn = buildButton("Remove Slot");
        removeBtn.addActionListener(e -> showRemoveStatusDialog());

        JButton cancelResBtn = buildButton("Cancel Reservation");
        cancelResBtn.addActionListener(e -> showCancelReservationDialog());

        threeButtons.add(addBtn);
        threeButtons.add(editBtn);
        threeButtons.add(removeBtn);
        threeButtons.add(cancelResBtn);
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

    private void showCancelReservationDialog() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField idField = new JTextField(15);
        JButton cancelResBtn = new JButton("Cancel Reservation");
        JButton closeBtn = new JButton("Close");

        styleButton(cancelResBtn, true);
        styleButton(closeBtn, false);

        JDialog dialog = createBaseDialog("Cancel Reservation", new Dimension(350, 160), formPanel, cancelResBtn, closeBtn);

        JLabel titleLabel = new JLabel("CANCEL RESERVATION ON SLOT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(titleLabel);
        addField(formPanel, "Enter Slot Identifier:", idField);

        cancelResBtn.addActionListener(e -> {
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

                ParkingSpace space = slotController.getSpaceDetails(spaceId);
                if (space == null || !space.isReserved()) {
                    JOptionPane.showMessageDialog(dialog, "This slot has no active reservation.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Cancel the reservation on slot " + spaceId + "?\nThe user will be notified on next login.",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    slotController.cancelReservationFromAdmin(spaceId);
                    JOptionPane.showMessageDialog(dialog, "Reservation cancelled. The slot is now free.");
                    dialog.dispose();
                    refreshTable();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Slot ID must be a number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSlotInfoDialog(String text, ParkingSpace space) {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog(text, new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        //TODO: only allow floors with enough slots free
        JComboBox<Integer> floorCombo =new JComboBox<>(new Integer[]{0, 1, 2, 3, 4});
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        JComboBox<String> occStatusCombo = new JComboBox<>(new String[]{"Occupied", "Free"});
        JComboBox<String> resStatusCombo = new JComboBox<>(new String[]{"Reserved", "Unreserved"});

        JTextField idField = new JTextField(15);
        if (space != null) {
            idField.setText(String.valueOf(space.getId()));
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);

            floorCombo.setSelectedItem(space.getFloor());
            typeCombo.setSelectedItem(space.getType());
            occStatusCombo.setSelectedItem(space.isOccupied() ? "Occupied" : "Free");
            resStatusCombo.setSelectedItem(space.isReserved() ? "Reserved" : "Unreserved");
        } else {
            occStatusCombo.setSelectedItem("Free");
            resStatusCombo.setSelectedItem("Unreserved");
            occStatusCombo.setEnabled(false);
            resStatusCombo.setEnabled(false);
            occStatusCombo.setBackground(Color.LIGHT_GRAY);
            resStatusCombo.setBackground(Color.LIGHT_GRAY);
        }

        JLabel titleLabel = new JLabel(text.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(titleLabel);

        addField(formPanel, "Slot identifier:", idField);
        addField(formPanel, "Floor:", floorCombo);
        addField(formPanel, "Vehicle type:", typeCombo);
        addField(formPanel, "Occupational status:", occStatusCombo);
        addField(formPanel, "Reservation status:", resStatusCombo);

        okBtn.addActionListener(e -> {
            if (idField.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in the ID field!");
                return;
            }


            int code  = Integer.parseInt(idField.getText());
            int floor = (Integer) floorCombo.getSelectedItem();
            String vehicleType = (String) typeCombo.getSelectedItem();
            boolean occStatus = occStatusCombo.getSelectedItem().equals("Occupied");
            boolean resStatus = resStatusCombo.getSelectedItem().equals("Reserved");

            if (space != null) {
                // Edit existing slot
                switch (slotController.editSpace(code, floor, vehicleType, occStatus, resStatus)) {
                    case SUCCESS        -> {
                        JOptionPane.showMessageDialog(dialog, "Slot edited!");
                        dialog.dispose();
                        refreshTable();
                    }
                    case NOT_FOUND      -> JOptionPane.showMessageDialog(dialog, "Slot not found.", "Error", JOptionPane.WARNING_MESSAGE);
                    case DATABASE_ERROR -> JOptionPane.showMessageDialog(dialog, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Add new slot
                switch (slotController.addSpace(code, floor, vehicleType, occStatus, resStatus)) {
                    case SUCCESS        -> {
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

    private void showEditSlotDialog() {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField idField = new JTextField(15);
        JButton nextBtn = new JButton("Next");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(nextBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog("Edit Slot", new Dimension(350, 200), formPanel, nextBtn, cancelBtn);

        JLabel title = new JLabel("EDIT PARKING SLOT");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(title);
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
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField idField = new JTextField(15);
        JButton removeBtn = new JButton("Remove");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(removeBtn, true);
        styleButton(cancelBtn, false);

        JDialog dialog = createBaseDialog("Remove Slot", new Dimension(350, 200), formPanel, removeBtn, cancelBtn);

        JLabel title = new JLabel("REMOVE PARKING SLOT");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(title);
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
        dialog.setLayout(new BorderLayout());

        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setPreferredSize(size);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        Dimension btnSize = new Dimension(100, 35);

        actionBtn.setPreferredSize(btnSize);
        cancelBtn.setPreferredSize(btnSize);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(actionBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        return dialog;
    }

    private void addField(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }
}

//
package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSlotsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;

    public ManageSlotsPanel() {
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
        String[] columns = { "Code", "Floor", "Current Status", "Reservation Status", "Type", "Space ID" };
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

    // Call this to populate the table with data
    public void loadData(List<ParkingSpace> spaces) {
        tableModel.setRowCount(0); // clear existing rows
        for (ParkingSpace space : spaces) {
            tableModel.addRow(new Object[]{
                    space.getCode(),
                    space.getFloor(),
                    space.getCurrentStatus(),
                    space.getReservationStatus(),
                    space.getType(),
                    space.getSpaceId()
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

    private void showSlotInfoDialog(String text, String id) {
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(okBtn, false);
        styleButton(cancelBtn, true);

        JDialog dialog = createBaseDialog(text, new Dimension(400, 500), formPanel, okBtn, cancelBtn);

        JTextField idField = new JTextField(15);
        if (id != null) idField.setText(id);

        JTextField floorField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        JTextField plateField = new JTextField(15);
        JComboBox<String> occStatusCombo = new JComboBox<>(new String[]{"Occupied", "Free"});
        JComboBox<String> resStatusCombo = new JComboBox<>(new String[]{"Reserved", "Unreserved"});

        JLabel titleLabel = new JLabel(text.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(titleLabel);

        addField(formPanel, "Slot identifier:", idField);
        addField(formPanel, "Floor:", floorField);
        addField(formPanel, "Vehicle type:", typeCombo);
        addField(formPanel, "Vehicle plate:", plateField);
        addField(formPanel, "Occupational status:", occStatusCombo);
        addField(formPanel, "Reservation status:", resStatusCombo);

        okBtn.addActionListener(e -> {
            if (idField.getText().isBlank() || floorField.getText().isBlank() || plateField.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.");
            } else {
                System.out.println("Saving: " + idField.getText());
                if (id != null) {
                    JOptionPane.showMessageDialog(dialog, "Slot edited successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Slot added successfully!");
                }
                //TODO: pass to controller
                dialog.dispose();
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
            if (idField.getText().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a slot ID.");
            } else {
                dialog.dispose();
                //TODO: check if slot exists
                showSlotInfoDialog("Edit slot", idField.getText());
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
            String id = idField.getText().trim();
            //TODO: check if slot exists
            if (id.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a slot ID.");
            } else {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Delete slot " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dialog.dispose();
                }
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
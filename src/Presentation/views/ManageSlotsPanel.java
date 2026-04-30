package Presentation.views;

import Business.Entities.ParkingSpace;

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

        wrapper.add(Box.createVerticalStrut(15));

        JPanel threeButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton addBtn = new JButton("Add Slot");
        addBtn.addActionListener(e -> showSlotInfoDialog("Add slot"));

        JButton editBtn = new JButton("Edit Slot");
        editBtn.addActionListener(e -> showEditSlotDialog());

        JButton removeBtn = new JButton("Remove Slot");
        removeBtn.addActionListener(e -> showRemoveStatusDialog());

        threeButtons.add(addBtn);
        threeButtons.add(editBtn);
        threeButtons.add(removeBtn);
        wrapper.add(threeButtons);

        wrapper.add(Box.createVerticalStrut(15));
        return wrapper;
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

    private void showSlotInfoDialog(String text) {
        JTextField idField = new JTextField(15);
        JTextField floorField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle", "Truck"});
        JTextField plateField = new JTextField(15);
        JComboBox<String> occStatusCombo = new JComboBox<>(new String[]{"Occupied", "Free"});
        JComboBox<String> resStatusCombo = new JComboBox<>(new String[]{"Reserved", "Unreserved"});

        JPanel panel = new JPanel(new GridLayout(13, 1, 5, 5));

        JLabel title = new JLabel(text);
        Font font = new Font("Courier", Font.BOLD, 12);
        title.setFont(font);
        panel.add(title);

        panel.add(new JLabel("Slot identifier:"));
        panel.add(idField);

        panel.add(new JLabel("Floor:"));
        panel.add(floorField);

        panel.add(new JLabel("Vehicle type:"));
        panel.add(typeCombo);

        panel.add(new JLabel("Vehicle plate:"));
        panel.add(plateField);

        panel.add(new JLabel("Occupational status:"));
        panel.add(occStatusCombo);

        panel.add(new JLabel("Reservation status:"));
        panel.add(resStatusCombo);



        int result = JOptionPane.showConfirmDialog(
                this, panel, text, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (idField.getText().isBlank() || floorField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing fields", JOptionPane.WARNING_MESSAGE);
                //TODO: go back to add slot screen
                return;
            }
            JOptionPane.showMessageDialog(this, text + " - confirmation", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            // TODO: pass to controller
            System.out.println(text + ": " + idField.getText());
        }
    }

    private void showEditSlotDialog() {
        JTextField idField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        JLabel title = new JLabel("Edit Slot");
        Font font = new Font("Courier", Font.BOLD, 12);
        title.setFont(font);
        panel.add(title);

        panel.add(new JLabel("Slot identifier:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Edit Parking Slot", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (idField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing fields", JOptionPane.WARNING_MESSAGE);
                //TODO: go back to edit slot screen
                return;
            }
            showSlotInfoDialog("Edit slot");
            // TODO: pass to controller
            System.out.println("Edit slot: " + idField.getText());
        }
    }

    private void showRemoveStatusDialog() {
        JTextField idField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        JLabel title = new JLabel("Remove Slot");
        Font font = new Font("Courier", Font.BOLD, 12);
        title.setFont(font);
        panel.add(title);

        panel.add(new JLabel("Slot identifier:"));
        panel.add(idField);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove slot " + idField + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // TODO: pass to controller
            System.out.println("Remove slot: " + idField);
        }
    }
}
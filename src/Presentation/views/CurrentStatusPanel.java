package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.StatusController;
import Presentation.theme.AppColors;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CurrentStatusPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final StatusController statusController;

    public CurrentStatusPanel(StatusController statusController) {
        this.statusController = statusController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildHeader() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Current Parking Status");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton refreshBtn = buildButton("Refresh");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        //refreshBtn.addActionListener(e -> loadData());

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(refreshBtn);
        wrapper.add(Box.createVerticalStrut(5));

        return wrapper;
    }

    private JScrollPane buildTable() {
        String[] columns = {"Slot Id", "Floor", "Type", "Occupation", "Reservation", "Plate"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Color rows based on occupation status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    String occupation = (String) tableModel.getValueAt(row, 3);
                    if ("Occupied".equalsIgnoreCase(occupation)) {
                        c.setBackground(new Color(255, 230, 230)); // light red
                    } else {
                        c.setBackground(new Color(230, 255, 230)); // light green
                    }
                } else {
                    c.setBackground(AppColors.LIGHT_BLUE);
                    c.setForeground(Color.WHITE);
                }

                if (!isSelected) c.setForeground(Color.BLACK);

                return c;
            }
        });

        return new JScrollPane(table);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<ParkingSpace> spaces = statusController.getParkingTableData();
        for (ParkingSpace space : spaces) {
            tableModel.addRow(new Object[]{
                    space.getId(),
                    space.getFloor(),
                    space.getType(),
                    space.isOccupied() ? "Occupied" : "Free",
                    space.isReserved() ? "Reserved" : "Unreserved",
                    "-" // get licence plate
            });
        }
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
}
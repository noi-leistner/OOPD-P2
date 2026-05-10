package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.StatusController;
import Presentation.theme.AppColors;

import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.HierarchyEvent;

public class CurrentStatusPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final StatusController statusController;
    private Timer timer;

    public CurrentStatusPanel(StatusController statusController) {
        this.statusController = statusController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadData();

        // Timer that calls loadData() every 5 seconds
        timer = new Timer(5000, e -> loadData());

        // Timer only starts when currentStatusPanel is visible to optimize
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    timer.start();
                } else {
                    timer.stop();
                }
            }
        });
    }

    private JPanel buildHeader() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Current Parking Status");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(10));

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

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                } else if (column == 4) {
                    String reservation = (String) tableModel.getValueAt(row, 4);
                    if ("Reserved".equalsIgnoreCase(reservation)) {
                        c.setBackground(new Color(255, 200, 200)); // light red, can be changed according to the palette we want
                    } else {
                        c.setBackground(new Color(200, 255, 200)); // light green, can be changed according to the palette we want
                    }
                }

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
}
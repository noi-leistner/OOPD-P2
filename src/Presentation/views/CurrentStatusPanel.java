package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.StatusController;
import Presentation.theme.AppColors;

import java.util.ArrayList;
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
    private List<ParkingSpace> spaces = new ArrayList<>(); //TODO: Change once functions have been made, this isn't good architecture

    public CurrentStatusPanel(StatusController statusController) {
        this.statusController = statusController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        addTableClickListener();

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

    private void addTableClickListener() {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        ParkingSpace space = spaces.get(row);
                        showSpaceDetailDialog(space);
                    }
                }
            }
        });
    }

    private void showSpaceDetailDialog(ParkingSpace space) {
        JDialog dialog = new JDialog((Frame) null, "Space Details", true);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Parking Space Information");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        content.add(title);

        content.add(makeField("Code:", String.valueOf(space.getId())));
        content.add(makeField("Floor:", String.valueOf(space.getFloor())));
        content.add(makeField("Vehicle Type:", space.getType()));
        content.add(makeField("Occupation Status:", space.isOccupied() ? "Occupied" : "Free"));
        content.add(makeField("Reservation Status:", space.isReserved() ? "Reserved" : "Unreserved"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        if (space.isReserved()) {
            content.add(new JSeparator());

            JLabel reservationTitle = new JLabel("Reservation Information");
            reservationTitle.setFont(new Font("Arial", Font.BOLD, 13));
            content.add(reservationTitle);

            content.add(makeField("User:", "— (not yet available)")); // TODO: get user by reservation

            JButton cancelBtn = new JButton("Cancel Reservation");
            cancelBtn.setBackground(AppColors.RED);
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setOpaque(true);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Do you want to cancel the reservation for space " + space.getId() + "?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: call function to cancel reservation
                    dialog.dispose();
                }
            });
            buttonPanel.add(cancelBtn);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(AppColors.LIGHT_BLUE);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setOpaque(true);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(320, 200));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel makeField(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(lbl);
        row.add(val);
        return row;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        spaces = statusController.getParkingTableData();
        for (ParkingSpace space : spaces) {
            tableModel.addRow(new Object[]{
                    space.getId(),
                    space.getFloor(),
                    space.getType(),
                    space.isOccupied() ? "Occupied" : "Free",
                    space.isReserved() ? "Reserved" : "Unreserved",
                    "-" // TODO: get licence plate
            });
        }
    }
}
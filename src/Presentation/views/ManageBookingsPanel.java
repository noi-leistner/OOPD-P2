package Presentation.views;

import Business.Entities.ParkingSpace;
import Business.Entities.Reservation;
import Business.Entities.User;
import Presentation.controllers.ParkingSpaceController;
import Presentation.controllers.ReservationController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO: has functions in common with ManageSlotsPanel
public class ManageBookingsPanel extends JPanel {
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
        //TODO: make table
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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

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
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Cancel reservation " + selectedReservation.getId() + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                reservationController.cancelReservationFromAdmin(selectedReservation.getId());
                refreshTable();
            }
        });

        buttons.add(editResBtn);
        buttons.add(cancelResBtn);
        wrapper.add(buttons);

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

    private JScrollPane buildTable() {
        String[] columns = { "User Id", "Plate", "Slot", "Type", "Date" };
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

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < currentReservations.size()) {
                    selectedReservation = currentReservations.get(row);
                }
            }
        });

        return new JScrollPane(table);
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

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        dateSpinner.setValue(reservation.getDate());

        List<ParkingSpace> availableSpots = slotController.getAvailableSpotsByType(currentSpot.getType());
        JComboBox<ParkingSpace> spotsCombo = new JComboBox<>(availableSpots.toArray(new ParkingSpace[0]));

        if (!availableSpots.contains(currentSpot)) {
            availableSpots.addFirst(currentSpot);
        }

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

        mainPanel.add(Box.createVerticalStrut(10));

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

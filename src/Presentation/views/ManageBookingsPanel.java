package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.ReservationController;
import Presentation.theme.AppColors;

import javax.swing.*;
import java.awt.*;

public class ManageBookingsPanel extends JPanel {
    private ReservationController reservationController;

    public ManageBookingsPanel(ReservationController reservationController) {
        this.reservationController = reservationController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildButtonArea(), BorderLayout.NORTH);
        //TODO: make table
        //add(buildTable(), BorderLayout.CENTER);
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

        //TODO: if we only need cancel reservation button we can simplify this
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton cancelResBtn = buildButton("Cancel Reservation");
        cancelResBtn.addActionListener(e -> showCancelReservationDialog());

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
        //TODO: decide if delete using reservation, slot or vehicle id
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
                    reservationController.cancelReservationFromAdmin(spaceId);
                    space.setReserved(false);
                    JOptionPane.showMessageDialog(dialog, "Reservation cancelled.");
                    dialog.dispose();
                    //refreshTable();
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

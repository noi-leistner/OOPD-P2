package Presentation.views;

import Business.Entities.ParkingSpace;
import Presentation.controllers.EntryExitController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VehicleExitPanel extends JPanel {

    private final EntryExitController controller;

    private JTextField plateField;
    private JLabel statusLabel;

    public VehicleExitPanel(EntryExitController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        add(buildForm(), BorderLayout.NORTH);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Vehicle Exit");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("Enter your license plate to release your parking space.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel plateLabel = new JLabel("License plate: ");
        plateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        row.add(plateLabel);

        plateField = new JTextField(14);
        plateField.setFont(new Font("Arial", Font.PLAIN, 14));
        row.add(plateField);

        row.add(Box.createHorizontalStrut(12));

        JButton exitBtn = buildDangerButton("Exit Parking");
        exitBtn.addActionListener(e -> onExit());
        row.add(exitBtn);

        panel.add(row);
        panel.add(Box.createVerticalStrut(20));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);

        return panel;
    }

    private void onExit() {
        String plate = plateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            showError("Please enter a license plate.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "<html>Mark vehicle <b>" + plate + "</b> as exited?<br>" +
                        "The parking space will be freed.</html>",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        ParkingSpace freed = controller.exitParking(plate);

        if (freed != null) {
            showSuccess("✓  Vehicle " + plate + " has exited. Space #" + freed.getId() + " is now free.");
            plateField.setText("");
        } else {
            showError("No vehicle with plate \"" + plate + "\" is currently parked.");
        }
    }

    private void showSuccess(String msg) {
        statusLabel.setForeground(new Color(0, 140, 0));
        statusLabel.setText(msg);
    }

    private void showError(String msg) {
        statusLabel.setForeground(AppColors.RED);
        statusLabel.setText(msg);
    }

    private JButton buildDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(220, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        return btn;
    }
}
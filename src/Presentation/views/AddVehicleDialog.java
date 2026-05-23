package Presentation.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddVehicleDialog extends JDialog {

    private boolean confirmed = false;
    private JComboBox<String> typeCombo;

    private static final String[] VEHICLE_TYPES = {"car", "motorcycle", "Truck"};

    public AddVehicleDialog(Frame parent, String licensePlate) {
        super(parent, "Register Vehicle", true);
        buildUI(licensePlate);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void buildUI(String licensePlate) {
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(24, 28, 20, 28));
        root.setBackground(Color.WHITE);

        // Icon + message
        JPanel messagePanel = new JPanel(new BorderLayout(14, 0));
        messagePanel.setOpaque(false);

        JLabel icon = new JLabel("🚗");
        icon.setFont(icon.getFont().deriveFont(Font.PLAIN, 32f));
        icon.setVerticalAlignment(SwingConstants.TOP);
        messagePanel.add(icon, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Vehicle not registered");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "<html>Plate <b>" + licensePlate + "</b> is not registered in the system.<br>" +
                        "Would you like to add it to your account?</html>"
        );
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Vehicle type row
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeRow.setOpaque(false);

        JLabel typeLabel = new JLabel("Vehicle type: ");
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.PLAIN, 12f));

        typeCombo = new JComboBox<>(VEHICLE_TYPES);
        typeCombo.setFont(typeCombo.getFont().deriveFont(Font.PLAIN, 12f));

        typeRow.add(typeLabel);
        typeRow.add(typeCombo);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(typeRow);

        messagePanel.add(textPanel, BorderLayout.CENTER);
        root.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dispose());

        JButton addBtn = new JButton("Add Vehicle");
        addBtn.setPreferredSize(new Dimension(130, 32));
        addBtn.setBackground(new Color(21, 20, 20));
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSelectedType() {
        return (String) typeCombo.getSelectedItem();
    }
}
package Presentation.views;

import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class BaseManagePanel extends JPanel {

    protected void styleButton(JButton btn, boolean cancel) {
        btn.setBackground(cancel ? AppColors.RED : AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
    }

    protected JButton buildButton(String text) {
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

    protected JDialog createBaseDialog(String title, Dimension size, JPanel formPanel, JButton actionBtn, JButton cancelBtn) {
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

    protected void addField(JPanel panel, String label, JComponent field) {
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
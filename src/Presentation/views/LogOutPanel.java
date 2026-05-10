package Presentation.views;

import Presentation.controllers.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LogOutPanel extends JPanel {

    public LogOutPanel(MainWindow mainWindow, AuthController authController) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Account Options");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose an action below");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logOutBtn = createStyledButton("Log Out", new Color(30, 130, 230), Color.WHITE);
        logOutBtn.addActionListener(e -> {
            DeleteAccountDialog dialog = new DeleteAccountDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), false);
            if (dialog.isConfirmed()) {
                authController.logOut();
                mainWindow.switchTo(MainWindow.AUTH_SCREEN);
            }
        });

        JButton deleteBtn = createStyledButton("Delete Account", new Color(220, 53, 69), Color.WHITE);
        deleteBtn.addActionListener(e -> {
            DeleteAccountDialog dialog = new DeleteAccountDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), true);
            if (dialog.isConfirmed()) {
                authController.deleteAccount();
                mainWindow.switchTo(MainWindow.AUTH_SCREEN);
            }
        });

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 32)));
        card.add(logOutBtn);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(deleteBtn);

        add(card);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(260, 44));
        btn.setPreferredSize(new Dimension(260, 44));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
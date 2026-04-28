package Presentation.views;

import Presentation.controllers.AuthController;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainWindow mainWindow, AuthController authController) {
        setLayout(new BorderLayout());

        // 1. Cabecera de bienvenida
        JLabel welcomeLabel = new JLabel("Welcome to the Parking Dashboard!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // 2. Contenido central (puedes añadir estadísticas o botones luego)
        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("You have successfully logged in."));
        add(centerPanel, BorderLayout.CENTER);

        // 3. Botón de Logout para volver al inicio
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Aquí podrías llamar a authController.logOut() si lo necesitas
            mainWindow.switchTo(MainWindow.AUTH_SCREEN);
        });

        JPanel southPanel = new JPanel();
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);
    }
}
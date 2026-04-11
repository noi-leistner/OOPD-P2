package Presentation;

import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private JPanel contentArea;

    public AuthPanel(MainWindow app, AuthController authController) {
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        contentArea = new JPanel();
        add(contentArea, BorderLayout.CENTER);

        showRegister(app, authController);
    }

//    public void showLogin(MainWindow app, AuthController authController) {
//        contentArea.removeAll();
//        contentArea.add(new LoginForm(app, authController, this));
//        contentArea.revalidate();
//        contentArea.repaint();
//    }

    public void showRegister(MainWindow app, AuthController authController) {
        contentArea.removeAll();
        contentArea.add(new RegisterForm(app, authController, this));
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Parking");
        header.setOpaque(true);
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        return header;
    }
}

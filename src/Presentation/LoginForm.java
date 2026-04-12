package Presentation;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JPanel {

    public LoginForm(MainWindow mainWindow, AuthController auth, AuthPanel authPanel) {

        setLayout(new GridBagLayout());

        JPanel inner = new JPanel(new GridLayout(0, 1, 5, 5));
        inner.setPreferredSize(new Dimension(300, 220));

        JTextField email = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            boolean success = auth.logIn(email.getText(), new String(password.getPassword()));
        });

        JButton goToRegister = new JButton("Register if you do not have an account here");
        goToRegister.addActionListener(e -> authPanel.showRegister(mainWindow, auth));

        inner.add(new JLabel("Email"));
        inner.add(email);
        inner.add(new JLabel("Password"));
        inner.add(password);
        inner.add(loginButton);
        inner.add(goToRegister);

        add(inner);
    }
}
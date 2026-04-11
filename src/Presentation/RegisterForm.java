package Presentation;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JPanel {

    public RegisterForm(MainWindow app, AuthController auth, AuthPanel authPanel) {
        setLayout(new GridLayout(12, 1, 5, 5));

        JTextField nameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JPasswordField confirmField = new JPasswordField(20);

        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            boolean success = auth.register(nameField.getText(), surnameField.getText(), emailField.getText(), new String(passField.getPassword()));
            //if (success) app.switchTo("DASHBOARD");
            //else JOptionPane.showMessageDialog(this, "Registration failed");
        });

        JButton goToLogin = new JButton("Have an account? Login");
        //goToLogin.addActionListener(e -> authPanel.showLogin(app, auth));

        add(new JLabel("First name"));
        add(nameField);
        add(new JLabel("Last name"));
        add(surnameField);
        add(new JLabel("Email"));
        add(emailField);
        add(new JLabel("Password"));
        add(passField);
        add(new JLabel("Confirm password"));
        add(confirmField);
        add(registerBtn);
        add(goToLogin);
    }
}

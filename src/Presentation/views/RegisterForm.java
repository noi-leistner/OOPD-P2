package Presentation.views;

import Business.AuthResult;
import Business.Entities.User;
import Presentation.controllers.AuthController;

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
            String name     = nameField.getText().trim();
            String surname  = surnameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirm  = new String(confirmField.getPassword()).trim();

            if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = new User(name, surname, email, password);
            AuthResult result = auth.signUp(user);
            switch (result) {
                case SUCCESS -> app.switchTo(MainWindow.DASHBOARD_SCREEN);
                case EMAIL_ALREADY_EXISTS -> JOptionPane.showMessageDialog(this,
                        "An account with that email already exists.",
                        "Email taken",
                        JOptionPane.WARNING_MESSAGE);
                case DATABASE_ERROR -> JOptionPane.showMessageDialog(this,
                        "Something went wrong. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton goToLogin = new JButton("Have an account? Login");
        goToLogin.addActionListener(e -> authPanel.showLogin(app, auth));

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

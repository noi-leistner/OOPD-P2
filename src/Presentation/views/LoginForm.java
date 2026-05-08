package Presentation.views;

import Business.AuthResult;
import Presentation.controllers.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JPanel {

    public LoginForm(MainWindow mainWindow, AuthController auth, AuthPanel authPanel) {


        setLayout(new GridBagLayout());

        JPanel inner = new JPanel(new GridLayout(0, 1, 5, 5));
        inner.setPreferredSize(new Dimension(340, 200));

        JTextField email = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            AuthResult result = auth.logIn(email.getText(), new String(password.getPassword()));
            switch (result) {
                case AuthResult.SUCCESS -> {
                    mainWindow.getDashboard().refresh();
                    mainWindow.switchTo(MainWindow.DASHBOARD_SCREEN);
                    auth.checkAndShowCancelledNotification();
                }
                case AuthResult.EMPTY_FIELDS -> JOptionPane.showMessageDialog(LoginForm.this, "Please fill all the fields!");
                case AuthResult.INVALID_CREDENTIALS -> JOptionPane.showMessageDialog(LoginForm.this, "Invalid credentials!");
                case AuthResult.DATABASE_ERROR -> JOptionPane.showMessageDialog(LoginForm.this, "Something went wrong, please try again!");
                default -> JOptionPane.showMessageDialog(LoginForm.this, "Something went wrong!");
            }
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

//
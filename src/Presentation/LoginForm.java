package Presentation;

import javax.swing.*;
import java.awt.*;


public class LoginForm extends JPanel {

    public LoginForm(MainWindow mainWindow, AuthController auth, AuthPanel authPanel) {
        setLayout(new GridLayout(12,1,5,5));

        JTextField email = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            boolean success = auth.logIn(email.getText(), new String(password.getPassword()));
        });

        JButton goToRegister = new JButton("Register if you do not have an account here");
        //goToRegister.addActionListener(e -> {})

        add(new JLabel("Email"));
        add(email);
        add(new JLabel("Password"));
        add (password);
        add(loginButton);
        add(goToRegister);

    }

}
package Presentation.views;

import Business.AuthResult;
import Presentation.controllers.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import Presentation.theme.AppColors;

public class LoginForm extends BaseAuthForm {

    public LoginForm(MainWindow mainWindow, AuthController auth, AuthPanel authPanel) {
        setLayout(new GridLayout(1,2));
        setOpaque(false);
        add(buildImagePanel("/Presentation/theme/resources/image_login_1.jpg"),BorderLayout.WEST);
        add(buildCenterPanel(mainWindow, auth, authPanel), BorderLayout.CENTER);
    }

    // ── Login Panel (Center) ──────────────────────────────────────────────────
    private JPanel buildCenterPanel(MainWindow mainWindow, AuthController auth, AuthPanel authPanel) {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(10, 40, 10, 40));
        form.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));

        JLabel title = buildTitle("LOGIN");
        JLabel sub   = buildSubtitle("WELCOME BACK");
        JTextField     emailField = buildField("Email",    false);
        JPasswordField passField  = (JPasswordField) buildField("Password", true);
        JButton loginBtn = buildPrimaryButton("LOGIN");
        JLabel registerLink = buildLink("Don't have an account? Register",
                () -> authPanel.showRegister(mainWindow, auth));

        loginBtn.addActionListener(e -> {
            AuthResult result = auth.logIn(emailField.getText(), new String(passField.getPassword()));
            switch (result) {
                case SUCCESS -> {
                    mainWindow.getDashboard().refresh();
                    mainWindow.switchTo(MainWindow.DASHBOARD_SCREEN);
                    passField.setText("");

                }
                case EMPTY_FIELDS        -> JOptionPane.showMessageDialog(this, "Please fill all the fields!");
                case USER_NOT_FOUND      -> JOptionPane.showMessageDialog(this, "No account found with that email.");
                case INVALID_CREDENTIALS -> JOptionPane.showMessageDialog(this, "Wrong password. Please try again.");
                case DATABASE_ERROR      -> JOptionPane.showMessageDialog(this, "Something went wrong, please try again!");
                default                  -> JOptionPane.showMessageDialog(this, "Something went wrong!");
            }
        });

        JLabel divider = new JLabel("── Or Sign up with ──");
        divider.setFont(new Font("SansSerif", Font.PLAIN, 11));
        divider.setForeground(AppColors.TEXT_MUTED);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(title);
        form.add(Box.createVerticalStrut(2));
        form.add(sub);
        form.add(Box.createVerticalStrut(22));
        form.add(emailField);
        form.add(Box.createVerticalStrut(12));
        form.add(passField);
        form.add(Box.createVerticalStrut(6));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(20));
        form.add(divider);
        form.add(Box.createVerticalStrut(10));
        form.add(registerLink);

        right.add(form);
        return right;
    }
}
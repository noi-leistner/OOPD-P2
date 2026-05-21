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
        add(buildImagePanel("/Presentation/theme/resources/image_login_1.jpg"));
        add(buildCenterPanel(mainWindow, auth, authPanel));
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
                case INVALID_CREDENTIALS -> {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
                case DATABASE_ERROR      -> JOptionPane.showMessageDialog(this, "Something went wrong, please try again!");
                default                  -> JOptionPane.showMessageDialog(this, "Something went wrong!");
            }
        });

        JLabel divider = new JLabel("── Or Sign up with ──");
        divider.setFont(new Font("SansSerif", Font.PLAIN, 11));
        divider.setForeground(AppColors.TEXT_MUTED);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerLink.setFont(new Font("SansSerif", Font.PLAIN, 11));
        registerLink.setForeground(AppColors.BRAND_MID);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { authPanel.showRegister(mainWindow, auth); }
        });

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

    // ── Helpers ───────────────────────────────────────────────────────────────

    public JTextField buildField(String placeholder, boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField(20) : new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(new Color(60, 60, 70));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, AppColors.FIELD_BORDER),
                new EmptyBorder(8, 4, 8, 4)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (!isPassword) {
            field.setForeground(AppColors.TEXT_MUTED);
            field.setText(placeholder);
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) { field.setText(""); field.setForeground(new Color(60,60,70)); }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (field.getText().isBlank()) { field.setForeground(AppColors.TEXT_MUTED); field.setText(placeholder); }
                }
            });
        }
        return field;
    }

    public JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);

        // Set standard colors using your theme palette
        btn.setBackground(AppColors.ACCENT);
        btn.setForeground(Color.WHITE);

        // Fonts and Layout
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Behaviors and Focus styling
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}
package Presentation.views;

import Business.AuthResult;
import Business.Entities.User;
import Presentation.controllers.AuthController;
import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * The registration screen panel.
 * Splits the screen in half: a background picture on the left,
 * and the actual registration form fields on the right.
 */
public class RegisterForm extends BaseAuthForm {

    /**
     * Creates the register screen layout and sticks the image panel
     * and the form panel side-by-side.
     */
    public RegisterForm(MainWindow app, AuthController auth, AuthPanel authPanel) {
        setLayout(new GridLayout(1, 2));
        setOpaque(false); // let the parent background show; avoids gray bleed
        add(buildImagePanel("/resources/image_login_1.jpg"));
        add(buildCenterPanel(app, auth, authPanel));
    }

    /**
     * Builds the right side of the screen. Centres the signup form,
     * sets up all input fields, runs local data validation guards,
     * and handles talking to the backend controller when clicked.
     */
    private JPanel buildCenterPanel(MainWindow app, AuthController auth, AuthPanel authPanel) {
        // Outer panel: GridBagLayout with no constraints centres the form
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        // Inner form: BoxLayout stacks fields top-to-bottom
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(10, 40, 10, 40));
        form.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));

        JLabel title = buildTitle("REGISTER");
        JLabel sub = buildSubtitle("CREATE ACCOUNT");

        JTextField     nameField    = buildField("First name",        false);
        JTextField     surnameField = buildField("Last name",         false);
        JTextField     emailField   = buildField("Email",             false);
        JTextField usernameField = buildField("Username",            false);
        JPasswordField passField    = (JPasswordField) buildField("Password",         true);
        JPasswordField confirmField = (JPasswordField) buildField("Confirm password", true);

        JButton registerBtn = buildPrimaryButton("REGISTER");
        registerBtn.addActionListener(e -> {
            String name     = nameField.getText().trim();
            String surname  = surnameField.getText().trim();
            String email    = emailField.getText().trim();
            String username  = usernameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirm  = new String(confirmField.getPassword()).trim();

            // Guard: all fields must be filled
            if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                        "Missing fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Guard: passwords must match before even hitting the DB
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.",
                        "Password mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "USername cannot be empty.",
                        "Username empty", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = new User(name, surname, email, username, password, "user");
            AuthResult result = auth.signUp(user);
            switch (result) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    app.getDashboard().refresh();
                    app.switchTo(MainWindow.DASHBOARD_SCREEN);
                    nameField.setText("");
                    surnameField.setText("");
                    emailField.setText("");
                    surnameField.setText("");
                    passField.setText("");
                    confirmField.setText("");
                }
                case EMAIL_ALREADY_EXISTS -> JOptionPane.showMessageDialog(this,
                        "An account with that email already exists.",
                        "Email taken",
                        JOptionPane.WARNING_MESSAGE);
                case DATABASE_ERROR -> JOptionPane.showMessageDialog(this,
                        "Something went wrong. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                case WEAK_PASSWORD ->  JOptionPane.showMessageDialog(this,
                        "Password must contain 8 letters and a special character",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE);
                case INVALID_EMAIL ->  JOptionPane.showMessageDialog(this,
                        "Enter a real email address.",
                        "Invalid email",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        JLabel loginLink = buildLink("Already have an account? Login",
                () -> authPanel.showLogin(app, auth));

        form.add(title);
        form.add(Box.createVerticalStrut(2));
        form.add(sub);
        form.add(Box.createVerticalStrut(18));
        form.add(nameField);
        form.add(Box.createVerticalStrut(10));
        form.add(surnameField);
        form.add(Box.createVerticalStrut(10));
        form.add(emailField);
        form.add(Box.createVerticalStrut(10));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(10));
        form.add(passField);
        form.add(Box.createVerticalStrut(10));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(18));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(14));
        form.add(loginLink);

        right.add(form);
        return right;
    }
}
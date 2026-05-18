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

public class RegisterForm extends JPanel {

    public RegisterForm(MainWindow app, AuthController auth, AuthPanel authPanel) {
        setLayout(new GridLayout(1, 2));
        setOpaque(false); // let the parent background show; avoids gray bleed
        add(buildLeftPanel());
        add(buildCenterPanel(app, auth, authPanel));
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel() {
            private final Image bg = loadBackground();

            private Image loadBackground() {
                URL url = getClass().getResource("/Presentation/theme/resources/image_login_1.jpg");
                if (url == null) {
                    System.err.println("[RegisterForm] Background image not found. " +
                            "Mark the resources folder as Resources Root in IntelliJ.");
                    return null;
                }
                return new ImageIcon(url).getImage();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // Step 1 — solid blue base (shown when image is missing)
                g2.setColor(AppColors.BRAND_MID);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Step 2 — image at 50% opacity so the blue tints through
                if (bg != null) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        // CHANGE: no setPreferredSize — GridLayout ignores it and gives each column exactly 50%
        return left;
    }

    // ── Right panel ───────────────────────────────────────────────────────────
    // CHANGE: replaced the old GridLayout(14,1) flat list of labels+fields with
    //         the same GridBagLayout→BoxLayout structure LoginForm uses.
    //         This centres the form vertically and caps its width at 340px.
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

        // Title + subtitle — same font/colour pattern as LoginForm
        JLabel title = new JLabel("REGISTER");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(AppColors.BRAND_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("CREATE ACCOUNT");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(AppColors.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // CHANGE: all fields now go through buildField() for consistent styling.
        //         Previously each field was a raw new JTextField with no styling.
        JTextField     nameField    = buildField("First name",        false);
        JTextField     surnameField = buildField("Last name",         false);
        JTextField     emailField   = buildField("Email",             false);
        JPasswordField passField    = (JPasswordField) buildField("Password",         true);
        JPasswordField confirmField = (JPasswordField) buildField("Confirm password", true);

        // Role selector — styled to match field height
        String[] roles = {"Client", "Worker", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        roleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // CHANGE: replaced plain JButton with buildPrimaryButton() for the same
        //         rounded blue pill style as the LOGIN button.
        JButton registerBtn = buildPrimaryButton("REGISTER");
        registerBtn.addActionListener(e -> {
            String name     = nameField.getText().trim();
            String surname  = surnameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirm  = new String(confirmField.getPassword()).trim();
            String role     = roleCombo.getSelectedItem().toString().trim();

            // Guard: all fields must be filled
            if (name.isBlank() || surname.isBlank() || email.isBlank()
                    || password.isBlank() || confirm.isBlank()) {
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

            User user = new User(name, surname, email, password, role);
            AuthResult result = auth.signUp(user);
            switch (result) {
                case SUCCESS -> {JOptionPane.showMessageDialog(this,
                        "Creation successfull",
                        "Your account has been successfully created",
                        JOptionPane.INFORMATION_MESSAGE);
                        nameField.setText("");
                        surnameField.setText("");
                        emailField.setText("");
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

        // Back-to-login link
        JLabel loginLink = new JLabel("Already have an account? Login");
        loginLink.setFont(new Font("SansSerif", Font.PLAIN, 11));
        loginLink.setForeground(AppColors.BRAND_MID);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                authPanel.showLogin(app, auth);
            }
        });

        // Assembly — vertical struts control spacing between fields
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
        form.add(passField);
        form.add(Box.createVerticalStrut(10));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(10));
        form.add(roleCombo);
        form.add(Box.createVerticalStrut(18));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(14));
        form.add(loginLink);

        right.add(form);
        return right;
    }

    // ── Helpers — exact copies from LoginForm ─────────────────────────────────
    // CHANGE: these were missing entirely in RegisterForm; fields had no styling.

    /** Underline-style field with grey placeholder text (text fields only). */
    private JTextField buildField(String placeholder, boolean isPassword) {
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
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(new Color(60, 60, 70));
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (field.getText().isBlank()) {
                        field.setForeground(AppColors.TEXT_MUTED);
                        field.setText(placeholder);
                    }
                }
            });
        }
        return field;
    }

    /** Rounded blue pill button — same custom paintComponent as LoginForm. */
    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? AppColors.BRAND_DARK : AppColors.ACCENT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
package Presentation.views;

import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public abstract class BaseAuthForm extends JPanel {

    protected JPanel buildImagePanel(String imagePath) {
        JPanel background = new JPanel() {
            private final Image bg = loadBackground();

            private Image loadBackground() {
                URL url = getClass().getResource(imagePath);
                if (url == null) {
                    System.err.println("[BaseAuthForm] Background image not found: " + imagePath);
                    return null;
                }
                return new ImageIcon(url).getImage();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AppColors.BRAND_MID);
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (bg != null) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        return background;
    }

    protected JTextField buildField(String placeholder, boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField(20) : new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, AppColors.FIELD_BORDER),
                new EmptyBorder(8, 4, 8, 4)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBackground(Color.WHITE);
        field.setOpaque(true);

        if (isPassword) {
            JPasswordField passField = (JPasswordField) field;
            passField.setEchoChar((char) 0);
            passField.setText(placeholder);
            passField.setForeground(AppColors.TEXT_MUTED);

            passField.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (String.valueOf(passField.getPassword()).equals(placeholder)) {
                        passField.setText("");
                        passField.setEchoChar('•');
                        passField.setForeground(new Color(60, 60, 70));
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (passField.getPassword().length == 0) {
                        passField.setEchoChar((char) 0);
                        passField.setText(placeholder);
                        passField.setForeground(AppColors.TEXT_MUTED);
                    }
                }
            });
        } else {
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

    protected JButton buildPrimaryButton(String text) {
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

    protected JLabel buildTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(AppColors.BRAND_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    protected JLabel buildSubtitle(String text) {
        JLabel sub = new JLabel(text);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(AppColors.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        return sub;
    }

    protected JLabel buildLink(String text, Runnable onClick) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("SansSerif", Font.PLAIN, 11));
        link.setForeground(AppColors.BRAND_MID);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
        return link;
    }
}
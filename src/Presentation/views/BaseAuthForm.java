package Presentation.views;

import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * An abstract blueprint that provides shared UI builders for authentication forms.
 * It handles boilerplate design setups, including image panels with opacity tints,
 * interactive text fields with smart input placeholders, custom primary buttons,
 * and navigation web-links.
 */
public abstract class BaseAuthForm extends JPanel {

    /**
     * Builds an image panel used for side-branding. Loads an image file, paints
     * a solid background base using theme brand colors, and overlays the loaded
     * image with a 50% opacity blend configuration.
     *
     * @param imagePath the resource route path pointing to the background file
     * @return a configured, self-rendering image panel instance
     */
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

    /**
     * Generates a styled input text field or password field featuring built-in placeholder behaviors.
     * Attaches focus adapters to automatically clear out the placeholder text on cursor entry,
     * mask characters if it is an active password field, and restore default prompts if left blank.
     *
     * @param placeholder the text prompt shown inside the field when empty
     * @param isPassword set to true to create a masked password field; false for standard inputs
     * @return a text component armed with custom focus listeners and custom border styling
     */
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

    /**
     * Instantiates a standardized main submission button. Overrides default paint
     * rendering loops to implement custom anti-aliased solid fills that swap shade values
     * dynamically depending on active user click selections.
     *
     * @param text the label displayed inside the button body
     * @return a button set up with click-state color triggers and cursor adjustments
     */
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
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Simple label generator for large header titles.
     *
     * @param text the heading text string
     * @return a bold, centered header label instance
     */
    protected JLabel buildTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(AppColors.BRAND_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        return title;
    }

    /**
     * Simple label generator for smaller, secondary subheadings.
     *
     * @param text the subheading text string
     * @return a muted, centered descriptor label instance
     */
    protected JLabel buildSubtitle(String text) {
        JLabel sub = new JLabel(text);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(AppColors.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        return sub;
    }

    /**
     * Generates a clickable link label acting like a web hyperlink.
     * Attaches mouse adapters to trigger external navigation logic callbacks
     * whenever users register standard click actions on it.
     *
     * @param text the descriptive link message visible to users
     * @param onClick the execution block runner fired on mouse click
     * @return a text link component using hand cursors and theme accent coloring
     */
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
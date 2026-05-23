package Presentation.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * This class serves a dual purpose: it can either display an exit warning when a user
 * logs out, or a warning when a user attempts to permanently delete
 * their account from the system. It blocks interaction with the rest of the application
 * until the user explicitly confirms or cancels the action.
 */
public class DeleteAccountDialog extends JDialog {

    /** Tracks whether the user committed to the prompt (clicked Delete/LogOut) or backed away. */
    private boolean confirmed = false;

    /**
     * Spawns the confirmation window and holds processing control until the user makes a choice.
     *
     * @param parent     The main application frame to dim or center this modal against.
     * @param isDeleting True if we are running the destructive "Delete Account" routine;
     *                   false if we are merely confirming a standard "Log Out" sequence.
     */
    public DeleteAccountDialog(Frame parent, boolean isDeleting) {

        String text;
        if (isDeleting)  text = "Delete Account";
        else text = "Log Out";

        super(parent, text, true); // modal
        buildUI(isDeleting);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Constructs the UI layout elements. Adjusts warnings, messaging, and action button
     * text depending on the destructive nature of the requested action.
     *
     * @param isDeleting Dictates whether to inject permanent deletion warnings into the body panels.
     */
    private void buildUI(boolean isDeleting) {
        setResizable(false);
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(24, 28, 20, 28));
        root.setBackground(Color.WHITE);

        // Icon + message
        JPanel messagePanel = new JPanel(new BorderLayout(14, 0));
        messagePanel.setOpaque(false);

        JLabel icon = new JLabel("⚠");
        icon.setFont(icon.getFont().deriveFont(Font.PLAIN, 32f));
        icon.setForeground(new Color(220, 53, 69));
        icon.setVerticalAlignment(SwingConstants.TOP);
        messagePanel.add(icon, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        String title_text;
        String subtitle_text;
        String button_text;

        if (isDeleting) {
            title_text = "Are you sure you want to delete your account?";
            subtitle_text = "<html>This action is <b>permanent</b> and cannot be undone.<br>"
                    + "All your data will be permanently removed.</html>";
            button_text = "Delete";
        }
        else {
            title_text = "Are you sure you want to log out?";
            subtitle_text = "<html>You will be able to <b>log back in</b>, after going back to the authentification menu.</html>";
            button_text = "LogOut";
        }

        JLabel title = new JLabel(title_text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitle_text);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);


        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);
        messagePanel.add(textPanel, BorderLayout.CENTER);

        root.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 32));
        cancelBtn.addActionListener(e -> dispose());

        JButton deleteBtn = new JButton(button_text);
        deleteBtn.setPreferredSize(new Dimension(90, 32));
        deleteBtn.setBackground(new Color(21, 20, 20));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(deleteBtn);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /**
     * Checks whether the user clicked the affirmative action button to commit the operation.
     *
     * @return True if the user pressed 'Delete' or 'LogOut'; false if they clicked 'Cancel' or dismissed the window frame.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

}

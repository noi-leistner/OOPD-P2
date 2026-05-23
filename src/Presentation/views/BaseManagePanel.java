package Presentation.views;

import Presentation.theme.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * An abstract template that provides uniform UI-building blocks for management panels.
 * Offers shared utility functions to effortlessly structure horizontal button rows, style headers,
 * instantiate click-locked data tables, construct modal popups, and manage calendar time-merging.
 */
public abstract class BaseManagePanel extends JPanel {

    /**
     * Builds a standardized top toolbar containing a section title centered right
     * above an array of active administration command buttons.
     *
     * @param title   The bold section name displayed above the buttons.
     * @param buttons A variable-length array of buttons to be aligned horizontally in the row.
     * @return A styled wrapper panel configured with top-level spacing guidelines.
     */
    protected JPanel buildButtonArea(String title, JButton... buttons) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        wrapper.add(titleLabel);
        wrapper.add(Box.createVerticalStrut(15));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        for (JButton btn : buttons) {
            buttonRow.add(btn);
        }

        wrapper.add(buttonRow);
        wrapper.add(Box.createVerticalStrut(15));

        return wrapper;
    }

    /**
     * Tints and styles an existing button manually. Swaps the background colors to reflect
     * a cancel/destructive indicator or a welcoming action accent.
     *
     * @param btn    The button component targeted for a styling overhaul.
     * @param cancel Set to true for a cancellation warning look (Red); false for primary actions (Light Blue).
     */
    protected void styleButton(JButton btn, boolean cancel) {
        btn.setBackground(cancel ? AppColors.RED : AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Creates a new primary action button using the application's signature light blue tint,
     * complete with customized inner padding.
     *
     * @param text The string displayed inside the button body.
     * @return A styled, flat-design action button instance.
     */
    protected JButton buildButton(String text) {
        JButton btn = new JButton(text);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBackground(AppColors.LIGHT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    /**
     * Configures table row mechanics, sets up clean font constraints, limits row selections
     * to a single element at a time, and registers tracking listeners for row clicks.
     *
     * @param columns    An array of header strings defining table categories.
     * @param tableModel The backing structure managing table grid cells.
     * @param table      The raw JTable component target.
     * @param onSelect   A dynamic callback execution block fired immediately when a row selection finishes.
     * @return A container scroll pane wrapper ensuring overflow scroll bars appear smoothly when needed.
     */
    protected JScrollPane buildTable(String[] columns, DefaultTableModel tableModel, JTable table, Runnable onSelect) {
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                onSelect.run();
            }
        });

        return new JScrollPane(table);
    }

    /**
     * Instantiates a non-editable data table.
     *
     * @param columns An array of descriptive label strings naming the columns.
     * @return A read-only data layout model.
     */
    protected DefaultTableModel buildTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Spawns a structured, modal confirmation dialog. Stacks user data fields vertically over
     * a locked-width split button deck, instantly tying the cancel action button to an automatic dialog dismiss loop.
     *
     * @param title     The text header displayed along the window border frame.
     * @param size      The default dimensional size boundary constraints for the dialog popup window.
     * @param formPanel A populated configuration field panel containing user data options.
     * @param actionBtn The primary submission button that will execute database mutations.
     * @param cancelBtn The termination button used to safely back out of form editing.
     * @return A modal JDialog box.
     */
    protected JDialog createBaseDialog(String title, Dimension size, JPanel formPanel, JButton actionBtn, JButton cancelBtn) {
        JDialog dialog = new JDialog((Frame) null, title, true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        Dimension btnSize = new Dimension(100, 35);
        actionBtn.setPreferredSize(btnSize);
        cancelBtn.setPreferredSize(btnSize);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(actionBtn);
        buttonPanel.add(cancelBtn);

        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);
        return dialog;
    }

    /**
     * Drops an aligned form field row into a target panel layout. Left-aligns
     * a clean text tracker directly on top of the input component, normalizes width dimensions,
     * and adds spacing underneath.
     *
     * @param panel The target container panel being built.
     * @param label The text tag explaining what input value the user needs to enter.
     * @param field The interactive input element component (e.g., text field, combo box, date spinner).
     */
    protected void addField(JPanel panel, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension fieldSize = new Dimension(450, field.getPreferredSize().height);
        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);

        panel.add(jLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    /**
     * Combines distinct Calendar values from a split date selector spinner and a time selector spinner.
     * Merges the year, month, and day details from the first element with the hours and minutes
     * data parsed out of the second element, zeroing out trailing seconds and milliseconds for accuracy.
     *
     * @param dateSpinner The input spinner source providing calendar date parameters.
     * @param timeSpinner The input spinner source providing clock time configurations.
     * @return A consolidated, standard Date object containing the absolute timestamp merge match.
     */
    protected Date combineDateAndTime(JSpinner dateSpinner, JSpinner timeSpinner) {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) dateSpinner.getValue());

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime((Date) timeSpinner.getValue());

        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE,      timeCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
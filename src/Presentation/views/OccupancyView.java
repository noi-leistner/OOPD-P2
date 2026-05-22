package Presentation.views;

import Presentation.theme.AppColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OccupancyView extends JPanel {

    /**
     * Inner panel that renders the bar chart using Graphics2D.
     */
    private final ChartPanel chartPanel;

    /**
     * Manual refresh button that the controller can subscribe to.
     */
    private final JButton refreshBtn;

    /**
     * Constructs the view with all UI components initialized.
     *
     * <p>The view is created in a disconnected state. The controller must bind to it
     * after construction using {@link #addRefreshListener(ActionListener)}.</p>
     */
    public OccupancyView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Last Hour Occupancy");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        chartPanel = new ChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(AppColors.LIGHT_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Allows the controller to subscribe to refresh button events.
     *
     * <p>This follows the Observer/Listener pattern where the view does not know
     * who is listening. The controller can subscribe multiple times if needed.</p>
     *
     * <p><strong>Example usage:</strong></p>
     * <pre>{@code
     * view.addRefreshListener(e -> controller.fetchChartData());
     * }</pre>
     *
     * @param listener the action to execute when the refresh button is clicked
     */
    public void addRefreshListener(ActionListener listener) {
        refreshBtn.addActionListener(listener);
    }

    /**
     * Updates the chart with new occupancy data.
     *
     * <p>This follows the Push Model where the controller fetches data from the Business layer
     * and pushes it to the view. The view does not pull data itself.</p>
     *
     * <p><strong>Thread Safety:</strong> This method should be called from the Event Dispatch Thread (EDT).
     * The controller should use {@code SwingUtilities.invokeLater()} to ensure thread safety.</p>
     *
     * <p><strong>Data Format:</strong></p>
     * <pre>{@code
     * Map<Integer, Integer> data = {
     *     59 -> 5,   // 59 minutes ago: 5 vehicles
     *     58 -> 7,   // 58 minutes ago: 7 vehicles
     *     ...
     *     0 -> 12    // Now: 12 vehicles
     * }
     * }</pre>
     *
     * @param data map of minutes_ago (0-59) to vehicle count at that time
     */
    public void updateChart(Map<Integer, Integer> data) {
        chartPanel.setData(data);
    }

    /**
     * Custom JPanel that renders a bar chart using Graphics2D.
     *
     * <p>This component draws bars, axes, labels, and gridlines without using external charting libraries.
     * All rendering is done using pure AWT/Swing Graphics2D API.</p>
     *
     * <p><strong>Rendering Pipeline:</strong></p>
     * <ol>
     *   <li>Calculate chart area dimensions based on margins</li>
     *   <li>Draw white background</li>
     *   <li>Draw X and Y axes</li>
     *   <li>Draw Y-axis labels and horizontal gridlines</li>
     *   <li>Draw bars for each data point</li>
     *   <li>Draw X-axis labels (minutes ago)</li>
     *   <li>Draw axis titles ("Vehicles" and "Minutes ago")</li>
     * </ol>
     */
    private static class ChartPanel extends JPanel {

        /**
         * Chart data mapping minutes_ago to vehicle count.
         * Stored as TreeMap for automatic sorting by key (0, 1, 2, ..., 59).
         */
        private Map<Integer, Integer> data = new LinkedHashMap<>();

        /**
         * Left margin in pixels (space for Y-axis labels).
         */
        private static final int MARGIN_LEFT = 60;

        /**
         * Right margin in pixels.
         */
        private static final int MARGIN_RIGHT = 20;

        /**
         * Top margin in pixels.
         */
        private static final int MARGIN_TOP = 20;

        /**
         * Bottom margin in pixels (space for X-axis labels).
         */
        private static final int MARGIN_BOTTOM = 50;

        /**
         * Updates the chart data and triggers a repaint.
         *
         * <p>The input data is converted to a TreeMap to ensure chronological ordering
         * from oldest (59 minutes ago) to newest (0 minutes ago).</p>
         *
         * @param data new occupancy data to display
         */
        public void setData(Map<Integer, Integer> data) {
            this.data = new TreeMap<>(data);
            this.revalidate();
            this.repaint();
        }

        /**
         * Renders the entire bar chart using Graphics2D.
         *
         * <p>This method calculates all positions dynamically based on the panel size
         * and the range of data values. The Y-axis scales automatically to fit the data.</p>
         *
         * @param g the Graphics context provided by Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int chartX = MARGIN_LEFT;
            int chartY = MARGIN_TOP;
            int chartW = width - MARGIN_LEFT - MARGIN_RIGHT;
            int chartH = height - MARGIN_TOP - MARGIN_BOTTOM;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

            if (data == null || data.isEmpty()) {
                g2.setFont(new Font("Arial", Font.ITALIC, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("No data available for the last hour.",
                        chartX + chartW / 2 - 120,
                        chartY + chartH / 2);
                return;
            }

            int maxCount = data.values().stream().max(Integer::compareTo).orElse(1);
            int ySteps = Math.max(maxCount, 5);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= ySteps; i++) {
                int y = chartY + chartH - (int) ((double) i / ySteps * chartH);

                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(i), chartX - 25, y + 4);

                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(chartX, y, chartX + chartW, y);
            }

            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.setFont(new Font("Arial", Font.BOLD, 11));
            g2r.setColor(Color.DARK_GRAY);
            g2r.rotate(-Math.PI / 2, 15, chartY + chartH / 2);
            g2r.drawString("Vehicles", 15, chartY + chartH / 2);
            g2r.dispose();

            List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(data.entrySet());
            int barCount = entries.size();
            float spacing = (float) chartW / barCount;
            float barWidth = Math.max(2f, spacing * 0.7f);

            for (int i = 0; i < barCount; i++) {
                int minute = entries.get(i).getKey();
                int count = entries.get(i).getValue();

                int barH = (int) ((double) count / ySteps * chartH);
                int x = chartX + (int) (i * spacing);
                int y = chartY + chartH - barH;

                g2.setColor(new Color(70, 130, 200));
                g2.fillRect(x, y, (int) barWidth, barH);

                g2.setColor(new Color(50, 100, 170));
                g2.drawRect(x, y, (int) barWidth, barH);

                if (minute % 2 == 0 || barCount <= 24) {
                    g2.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(minute + "m", x - 5, chartY + chartH + 15);
                }
            }

            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Minutes ago", chartX + chartW / 2 - 30, height - 5);
        }
    }
}
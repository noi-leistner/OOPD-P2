package Presentation.views;

import Presentation.controllers.StatusController;
import Presentation.theme.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Displays a bar chart of parking occupancy over the last day.
 * Wired to StatusController.getOccupancyChartData().
 */
public class OccupancyPanel extends JPanel {

    private final StatusController statusController;
    private final ChartPanel chartPanel;

    public OccupancyPanel(StatusController statusController) {
        this.statusController = statusController;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Last Hour Occupancy");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Chart
        chartPanel = new ChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(AppColors.LIGHT_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.addActionListener(e -> loadData());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        // Load data immediately on creation
        loadData();
    }

    /** Fetches fresh data from StatusController and repaints the chart. */
    public void loadData() {
        Map<Integer, Integer> data = statusController.getOccupancyChartData();
        chartPanel.setData(data);
    }

    // ---------------------------------------------------------
    // Inner class — draws the bar chart with Graphics2D
    // ---------------------------------------------------------

    private static class ChartPanel extends JPanel {

        private Map<Integer, Integer> data = new LinkedHashMap<>();

        private static final int MARGIN_LEFT   = 60;
        private static final int MARGIN_RIGHT  = 20;
        private static final int MARGIN_TOP    = 20;
        private static final int MARGIN_BOTTOM = 50;

        public void setData(Map<Integer, Integer> data) {
            this.data = new TreeMap<>(data); // sort by minutes ago ascending
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width  = getWidth();
            int height = getHeight();

            int chartX = MARGIN_LEFT;
            int chartY = MARGIN_TOP;
            int chartW = width  - MARGIN_LEFT - MARGIN_RIGHT;
            int chartH = height - MARGIN_TOP  - MARGIN_BOTTOM;

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            // Axes
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);           // Y axis
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH); // X axis

            // No data
            if (data == null || data.isEmpty()) {
                g2.setFont(new Font("Arial", Font.ITALIC, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("No data available for the last 24 hours.",
                        chartX + chartW / 2 - 120, chartY + chartH / 2);
                return;
            }

            // Max value for Y scale
            int maxCount = data.values().stream().max(Integer::compareTo).orElse(1);
            int ySteps   = Math.max(maxCount, 5);

            // Y axis labels + gridlines
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= ySteps; i++) {
                int y = chartY + chartH - (int) ((double) i / ySteps * chartH);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(i), chartX - 25, y + 4);
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(chartX, y, chartX + chartW, y);
            }

            // Y axis label (rotated)
            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.setFont(new Font("Arial", Font.BOLD, 11));
            g2r.setColor(Color.DARK_GRAY);
            g2r.rotate(-Math.PI / 2, 15, chartY + chartH / 2);
            g2r.drawString("Vehicles", 15, chartY + chartH / 2);
            g2r.dispose();

            // Bars
            List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(data.entrySet());
            int   barCount = entries.size();
            float spacing  = (float) chartW / barCount;
            float barWidth = Math.max(2f, spacing * 0.7f);

            for (int i = 0; i < barCount; i++) {
                int minute = entries.get(i).getKey();
                int count  = entries.get(i).getValue();

                int barH = (int) ((double) count / ySteps * chartH);
                int x    = chartX + (int) (i * spacing);
                int y    = chartY + chartH - barH;

                // Bar
                g2.setColor(new Color(70, 130, 200));
                g2.fillRect(x, y, (int) barWidth, barH);
                g2.setColor(new Color(50, 100, 170));
                g2.drawRect(x, y, (int) barWidth, barH);

                // X label every 60 minutes
                if (minute % 2 == 0 || barCount <= 24) {
                    g2.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(minute + "m", x - 5, chartY + chartH + 15);
                }
            }

            // X axis label
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Minutes ago", chartX + chartW / 2 - 30, height - 5);
        }
    }
}
package Presentation.controllers;

import Business.ParkingLogManager;
import Business.SimulationManager;
import Presentation.views.OccupancyView;

import javax.swing.*;
import java.util.Map;


public class OccupancyController {

    /**
     * Business layer manager that provides occupancy data.
     * Used to fetch the last hour's parking occupancy statistics.
     */
    private final ParkingLogManager parkingLogManager;

    /**
     * Simulation manager that triggers periodic updates.
     * The controller subscribes to its tick callback for automatic chart updates.
     */
    private final SimulationManager simulationManager;

    /**
     * The view component that displays the occupancy chart.
     * The controller pushes data to this view via {@link OccupancyView#updateChart(Map)}.
     */
    private final OccupancyView view;

    /**
     * Constructs the controller and wires it to the view and business layer.
     *
     * <p>This constructor performs three initialization steps:</p>
     * <ol>
     *   <li>Subscribes to simulation tick events for automatic updates</li>
     *   <li>Subscribes to the view's refresh button for manual updates</li>
     *   <li>Performs an initial data load to populate the chart</li>
     * </ol>
     *
     * <p><strong>Event-Driven Architecture:</strong></p>
     * <p>The simulation tick callback is optimized to only fetch data when the view
     * is actually visible to the user, avoiding unnecessary database queries.</p>
     *
     * @param parkingLogManager the business layer manager for occupancy data
     * @param simulationManager the simulation manager that triggers updates
     * @param view the view component to control and update
     * @throws NullPointerException if any parameter is null
     */
    public OccupancyController(ParkingLogManager parkingLogManager, SimulationManager simulationManager, OccupancyView view) {
        this.parkingLogManager = parkingLogManager;
        this.simulationManager = simulationManager;
        this.view = view;

        this.simulationManager.setOnTickCallBack(() -> {
            if (this.view.isShowing()) {
                fetchChartData();
            }
        });

        this.view.addRefreshListener(e -> fetchChartData());

        fetchChartData();
    }

    /**
     * Fetches occupancy data from the Business layer and updates the view.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Spawns a background thread to fetch data (non-blocking)</li>
     *   <li>Calls {@link ParkingLogManager#getOccupancyLastHour()} on that thread</li>
     *   <li>Pushes the result to the view on the EDT using {@code SwingUtilities.invokeLater()}</li>
     * </ol>
     *
     * <p><strong>Thread Safety:</strong></p>
     * <p>The database query runs on a background thread to prevent UI freezing.
     * The view update is marshalled to the Event Dispatch Thread to ensure Swing
     * components are only modified from the EDT.</p>
     *
     * <p><strong>Performance Note:</strong></p>
     * <p>This method is called frequently (on every simulation tick), so it includes
     * an optimization that checks {@code view.isShowing()} before fetching data.
     * This prevents unnecessary database queries when the chart panel is not visible.</p>
     *
     * <p><strong>Error Handling:</strong></p>
     * <p>Database errors are handled by the {@link ParkingLogManager} layer.
     * If the query fails, an empty map is typically returned, which the view
     * will display as "No data available".</p>
     */
    private void fetchChartData() {
        new Thread(() -> {
            Map<Integer, Integer> data = parkingLogManager.getOccupancyLastHour();
            SwingUtilities.invokeLater(() -> view.updateChart(data));
        }).start();
    }

    /**
     * Returns the view component managed by this controller.
     *
     * This method is used by parent components (like {@code DashboardPanel})
     * to retrieve the view for layout purposes
     *
     * @return the {@link OccupancyView} instance managed by this controller
     */
    public OccupancyView getView() {
        return view;
    }

    /**
     * Manually triggers a chart refresh.
     *
     * <p>This method is typically called by parent components when they need to
     * force a chart update, such as when the dashboard refreshes or when the
     * simulation state changes.</p>
     *
     * <p>Calling this method is equivalent to the user clicking the refresh button,
     * but it can be triggered programmatically.</p>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * // In DashboardPanel.refresh():
     * statusController.setSimulationCallback(() -> {
     *     if (occupancyController != null) {
     *         occupancyController.refreshChart();
     *     }
     * });
     * }</pre>
     */
    public void refreshChart() {
        fetchChartData();
    }
}
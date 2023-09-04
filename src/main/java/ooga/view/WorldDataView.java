package ooga.view;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import ooga.model.gamestate.GameData;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.Observer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDataView implements Observer {

  private static final Logger LOG = LogManager.getLogger(WorldDataView.class);
  private static final String STYLESHEET = "/games/%s/stylesheets/GameDataView.css";
  private final Dialog dialog;
  private final BorderPane root;
  private final AreaChart<Number, Number> areaChart;
  private boolean hasInitialized = false;

  private GameData gameData;
  private final Map<String, Series<Number, Number>> populationSeries;

  /**
   * Creates the World Data popup view that displays the statistics of each population
   *
   * @param languageBundle language options
   * @param gameType       String: game type
   */
  public WorldDataView(ResourceBundle languageBundle, String gameType) {
    // setup popup
    this.dialog = new Dialog<>();
    dialog.setTitle(languageBundle.getString("WorldDataPopupTitle"));

    // setup dialog pane
    this.root = new BorderPane();
    DialogPane dialogPane = new DialogPane();
    dialogPane.setContent(root);
    dialog.setDialogPane(dialogPane);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    // Area chart initialization
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel(languageBundle.getString("WorldDataXAxisLabel"));
    yAxis.setLabel(languageBundle.getString("WorldDataYAxisLabel"));

    areaChart = new AreaChart<>(xAxis, yAxis);
    areaChart.getStylesheets()
        .add(getClass().getResource(String.format(STYLESHEET, gameType)).toExternalForm());
    areaChart.setTitle(languageBundle.getString("WorldDataPopupTitle"));

    populationSeries = new HashMap<>();

    root.setCenter(areaChart);
  }

  /**
   * Updates the chart values and the view
   */

  public void updateChart() throws PopulationNotFoundException {
    int day = gameData.getDay();
    Census census = gameData.getCensusAt(day);

    while (census.hasNext()) {
      String population = census.next();
      int percent = (int) Math.round(
          ((1.0 * census.getPopulation(population)) / census.getTotal()) * 100);

      populationSeries.get(population).getData().add(new Data<>(day, percent));
    }
    areaChart.requestLayout();
  }

  /**
   * Initializes the chart view and the Series data structure, which maintains the chart data for
   * each population.
   */
  private void initCharts() throws PopulationNotFoundException {
//    Census startingCensus = gameData.getCensusAt(0);
    LOG.debug("Initializing AreaChart");
    LOG.debug("Initializing at day {}", gameData.getDay());
    int startDay = gameData.getDay();

    Census census = gameData.getCensusAt(gameData.getDay());
    while (census.hasNext()) {
      String populationName = census.next();
      int percent = (int) Math.round(
          ((1.0 * census.getPopulation(populationName)) / census.getTotal()) * 100);

      LOG.debug("Series added:\t{}:\t{}", populationName, percent);
      // create new series for each population and initialize its view
      Series<Number, Number> series = new Series<>();
      series.setName(populationName);
      series.getData().add(new Data<>(startDay, percent));
      populationSeries.put(populationName, series);

      // add to areachart
      this.areaChart.getData().add(series);

    }
    hasInitialized = true;

  }

  public Dialog getDialog() {
    return this.dialog;
  }

  public void open() {
    dialog.show();
  }

  public void setGameData(GameData gameData) {
    this.gameData = gameData;
  }

  protected void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

  /**
   * A method that is triggered when an observable notifies the observer.
   */
  @Override
  public void update() {
    try {
      if (!hasInitialized) {
        initCharts();
      }
      this.updateChart();
    } catch (PopulationNotFoundException e) {
      showError(e);
    }
  }
}

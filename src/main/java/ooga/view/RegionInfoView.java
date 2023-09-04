package ooga.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ooga.controller.Controller;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.Observer;
import ooga.view.map.RegionView;

public class RegionInfoView implements Observer {

  private final Button regionButton;
  private final StartDialog startDialog;
  private final Dialog dialog;
  private Controller controller;
  private TableView<FactorText> factorTable;
  private TableView<PopulationText> populationTable;
  private RegionView regionView;
  private Label descriptionText;
  private Label populationTitle;
  private Label factorTitle;
  private PieChart pieChart;
  private final ResourceBundle languageBundle;

  public RegionInfoView(String styleSheet, ResourceBundle languageBundle) {
    this.languageBundle = languageBundle;
    regionButton = new Button();
    regionButton.setId("RegionButton");
    regionButton.getStyleClass().add("countrybutton");
    regionButton.setOnAction(event -> onClick());
    regionButton.setDisable(true);
    startDialog = new StartDialog(makeRunnable(), languageBundle);
    dialog = new Dialog();
    dialog.setDialogPane(makeDialogPane());
    dialog.getDialogPane().getStylesheets()
        .add(getClass().getResource(styleSheet).toExternalForm());
  }


  public void setController(Controller controller) {
    this.controller = controller;
  }

  private void onClick() {
    dialog.show();
  }

  public Button getRegionButton() {
    return regionButton;
  }

  public void updateRegionInfo(RegionView regionView)
      throws PopulationNotFoundException, FactorNotFoundException {
    if (this.regionView != null) {
      this.regionView.unselect();
    }
    regionButton.setDisable(false);
    this.regionView = regionView;
    regionView.select();
    String name = regionView.getName();
    dialog.setHeaderText(name);
    regionButton.setText(name);
    startDialog.updateName(name);
    factorTable.setItems(createFactorItems()); // creates all data
    descriptionText.setText(regionView.getDescription());
    populationTable.setItems(createPopulationItems());
    pieChart.setData(makePieChartData());
  }

  private void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

  private Runnable makeRunnable() {
    Runnable runnable = () -> {
      if (controller != null) {
        try {
          controller.selectRegion(regionView.getRegionID());
        } catch (PopulationNotFoundException e) {
          showError(e);
        }
      }
    };
    return runnable;
  }

  public void openStartDialog() {
    startDialog.open();
  }

  private DialogPane makeDialogPane() {
    DialogPane dialogPane = new DialogPane();
    descriptionText = new Label();
    factorTable = makeFactorTable();
    populationTable = makePopulationTable();
    populationTitle = new Label(languageBundle.getString("PopulationTableLabel"));
    factorTitle = new Label(languageBundle.getString("FactorTableLabel"));
    pieChart = makePieChart();
    VBox factorBox = new VBox(factorTitle, factorTable);
    VBox populationBox = new VBox(populationTitle, populationTable);
    HBox hbox = new HBox(factorBox, populationBox);
    VBox vbox = new VBox(descriptionText, hbox, pieChart);
    vbox.setSpacing(10);
    dialogPane.setContent(vbox);
    dialogPane.getButtonTypes().add(ButtonType.CLOSE);
    return dialogPane;
  }

  private TableView<FactorText> makeFactorTable() {
    TableView<FactorText> tableView = new TableView<FactorText>();
    TableColumn factorColumn = new TableColumn("Factor");
    factorColumn.setCellValueFactory(new PropertyValueFactory<>("factor"));
    TableColumn levelColumn = new TableColumn("Level");
    levelColumn.setCellValueFactory(new PropertyValueFactory("level"));
    tableView.getColumns().addAll(factorColumn, levelColumn);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    tableView.getStyleClass().add("regionfactortable");
    return tableView;
  }

  private ObservableList<FactorText> createFactorItems() throws FactorNotFoundException {
    Iterator<FactorText> iterator = regionView.getRegionFactorsIterator();
    List<FactorText> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    ObservableList<FactorText> data = FXCollections.observableList(list);
    return data;
  }

  private PieChart makePieChart() {
    PieChart pieChart = new PieChart();
    return pieChart;
  }

  private ObservableList<PopulationText> createPopulationItems()
      throws PopulationNotFoundException {
    Census census = regionView.getCensus();
    List<PopulationText> list = new ArrayList<>();
    while (census.hasNext()) {
      String name = census.next();
      String amount = Integer.toString(census.getPopulation(name));
      list.add(new PopulationText(name, amount));
    }
    ObservableList<PopulationText> data = FXCollections.observableList(list);
    return data;
  }

  private TableView<PopulationText> makePopulationTable() {
    TableView<PopulationText> tableView = new TableView<>();
    TableColumn nameColumn = new TableColumn("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    TableColumn amountColumn = new TableColumn("Amount");
    amountColumn.setCellValueFactory(new PropertyValueFactory("amount"));
    tableView.getColumns().addAll(nameColumn, amountColumn);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    tableView.getStyleClass().add("regionpopulationtable");
    return tableView;
  }

  private ObservableList<PieChart.Data> makePieChartData() throws PopulationNotFoundException {
    List<PieChart.Data> list = new ArrayList<>();
    Census census = regionView.getCensus();
    while (census.hasNext()) {
      String name = census.next();
      int percent = (int) Math.round(
          ((1.0 * census.getPopulation(name)) / census.getTotal()) * 100);
      list.add(new PieChart.Data(name, percent));
    }
    ObservableList<PieChart.Data> data = FXCollections.observableArrayList(list);
    return data;
  }

  private void updatePieChartData(ObservableList<PieChart.Data> data)
      throws PopulationNotFoundException {
    Census census = regionView.getCensus();
    int index = 0;
    while (census.hasNext()) {
      data.get(index).setPieValue(census.getPopulation(census.next()));
      index++;
    }
  }

  @Override
  public void update() {
    if (regionView != null) {
      try {
        populationTable.setItems(createPopulationItems());
        updatePieChartData(pieChart.getData());
      } catch (PopulationNotFoundException e) {
        showError(e);
      }
    }
  }
}

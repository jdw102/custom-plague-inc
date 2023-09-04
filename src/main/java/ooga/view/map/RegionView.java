package ooga.view.map;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import ooga.model.gamestate.GameState;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionModel;
import ooga.util.Observable;
import ooga.util.Observer;
import ooga.view.FactorText;
import ooga.view.RegionInfoView;
import ooga.view.ViewFactory;

public class RegionView implements Observer, Observable {

  private final int regionID;
  private final String name;
  private final String description;
  private final Shape regionShape;
  private final Shape backgroundShape;
  private final Group regionGraphic;
  private final Group portGraphics;
  private final String[] trackedPopulations;
  private final Tooltip tooltip;
  private final Collection<FactorText> factorTexts;
  private final Collection<Observer> observers;
  private final String antagonistMessage;
  private final RegionModel regionModel;
  private boolean selected;
  private boolean alertedUser;
  private final ResourceBundle settingsBundle;
  private final ResourceBundle languageBundle;
  private final GameState gameState;
  private final ViewFactory viewFactory;

  public RegionView(int regionID, String name, String description,
      Shape regionShape, Shape backgroundShape, Color color, RegionModel regionModel,
      ResourceBundle gameSettings, ResourceBundle languageBundle, GameState gameState) {
    this.gameState = gameState;
    this.regionID = regionID;
    settingsBundle = gameSettings;
    this.languageBundle = languageBundle;
    this.name = name;
    this.description = description;
    regionShape.setOnMouseEntered(event -> onHover());
    regionShape.setOnMouseExited(event -> offHover());
    this.regionShape = regionShape;
    this.backgroundShape = backgroundShape;
    this.regionModel = regionModel;
    this.portGraphics = new Group();
    this.trackedPopulations = createTrackedPopulations();
    tooltip = new Tooltip("");
    tooltip.setShowDuration(Duration.INDEFINITE);
    viewFactory = new ViewFactory();
    Tooltip.install(regionShape, tooltip);
    regionGraphic = createView(color);
    observers = new ArrayList<>();
    factorTexts = new ArrayList<>();
    antagonistMessage = languageBundle.getString("AntagonistMessage");
  }


  public void setOnAction(RegionInfoView regionInfoView) {
    regionShape.setOnMouseClicked(event -> {
      try {
        onMouseClicked(regionInfoView);
      } catch (PopulationNotFoundException | FactorNotFoundException e) {
        showError(e);
      }
    });
  }

  private void onMouseClicked(RegionInfoView regionInfoView)
      throws PopulationNotFoundException, FactorNotFoundException {
    regionInfoView.updateRegionInfo(this);
    if (!gameState.isRunning()) {
      regionInfoView.openStartDialog();
    }
  }

  private void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

  private String[] createTrackedPopulations() {
    return settingsBundle.getString("RegionTrackedPopulations").split(" ");
  }

  public void addPort(ImageView portIcon) {

    portGraphics.getChildren().add(portIcon);
  }

  private Group createView(Color color) {
    Group view = new Group();
    Color endColor = Color.valueOf(settingsBundle.getString("GradientEndColor"));
    RadialGradient gradient = viewFactory.createColorGradient(color, endColor, regionShape);
    regionShape.setFill(gradient);
    Color fillColor = Color.valueOf(settingsBundle.getString("RegionFillColor"));
    RadialGradient backgroundGradient = viewFactory.createColorGradient(fillColor, endColor,
        backgroundShape);
    backgroundShape.setFill(backgroundGradient);
    view.getChildren().addAll(backgroundShape, regionShape, portGraphics);
    view.setId("Region" + regionID);
    return view;
  }

  public Node getView() {
    return regionGraphic;
  }

  /**
   * A method that is triggered when an observable notifies the observer.
   */
  @Override
  public void update() {
    int amt = 0;
    if (regionModel.getCurrentState().hasAntagonist()) {
      displayAlert();
    }
    String text = "";
    Census census = getCensus();
    for (String trackedPop : trackedPopulations) {
      try {
        amt += census.getPopulation(trackedPop);
        text += trackedPop + ": " + NumberFormat.getNumberInstance(Locale.US)
            .format(census.getPopulation(trackedPop)) + "\n";
      } catch (PopulationNotFoundException e) {
        showError(e);
      }
    }
    tooltip.setText(text);
    double ratio = (1.0 * amt) / census.getTotal();
    regionShape.setOpacity(1 - calculateOpacity(ratio));
    notifyObservers();
  }

  public Census getCensus() {
    return regionModel.getCurrentState().census();
  }

  private double calculateOpacity(double ratio) {
    if (ratio == 0) {
      return 0;
    }
    return Math.log(1.4 * ratio + 1.3);
  }

  private void onHover() {
    adjustScale(0.98);
  }

  private void offHover() {
    if (!selected) {
      adjustScale(1.0);
    }
  }

  private void adjustScale(double amt) {
    regionShape.setScaleX(amt);
    regionShape.setScaleY(amt);
    for (Node n : regionGraphic.getChildren()) {
      if (n.getClass().isInstance(ImageView.class)) {
        n.setScaleX(amt);
        n.setScaleY(amt);
      }
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void addFactor(String factor, String level) {
    factorTexts.add(new FactorText(factor, level));
  }

  public Iterator<FactorText> getRegionFactorsIterator() throws FactorNotFoundException {
    Collection<FactorText> newCollection = new ArrayList<>();
    for (FactorText factorText : factorTexts) {
      String factorName = factorText.getFactor();
      newCollection.add(new FactorText(factorName, regionModel.getFactor(factorName)));
    }
    factorTexts.clear();
    factorTexts.addAll(newCollection);
    return factorTexts.iterator();
  }

  public int getRegionID() {
    return regionID;
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }

  public void select() {
    adjustScale(0.98);
    selected = true;
  }

  public void unselect() {
    adjustScale(1.0);
    selected = false;
  }

  private void displayAlert() {
    if (!alertedUser) {
      Dialog dialog = new Dialog();
      dialog.setHeaderText(String.format(antagonistMessage, name));
      dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
      dialog.show();
    }
    alertedUser = true;
  }
}

package ooga.view;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import ooga.model.gamestate.GameData;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.Observer;

public class LabelView implements Observer {

  private final Label label;
  private final String[] populations;
  private GameData gameData;
  private final ResourceBundle settingsBundle;
  private final ResourceBundle languageBundle;

  public LabelView(String key, ResourceBundle settingsBundle, ResourceBundle languageBundle) {
    this.languageBundle = languageBundle;
    this.settingsBundle = settingsBundle;
    Tooltip tooltip = new Tooltip(settingsBundle.getString(key));
    label = new Label();
    Tooltip.install(label, tooltip);
    populations = settingsBundle.getString(key).split(",");
  }

  public void setGameData(GameData gameData) {
    this.gameData = gameData;
  }

  public Node getNode() {
    return label;
  }

  private void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

  @Override
  public void update() {
    int count = 0;
    for (String subpopulation : populations) {
      try {
        count += gameData.getCensusAt(gameData.getDay()).getPopulation(subpopulation);
      } catch (PopulationNotFoundException e) {
        showError(e);
      }
    }
    label.setText(NumberFormat.getNumberInstance(Locale.US).format(count));
  }
}

package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import ooga.model.gamestate.Progressor;
import ooga.util.Observer;

public abstract class ProgressBarView implements Observer {

  private final String type;
  private final ResourceBundle gameSettings;
  private final ResourceBundle languageBundle;

  public ProgressBarView(String key, String type, ResourceBundle gameSettings,
      ResourceBundle languageBundle) {
    this.type = type;
    this.gameSettings = gameSettings;
    this.languageBundle = languageBundle;
  }

  protected ResourceBundle gameSettings() {
    return gameSettings;
  }

  protected ResourceBundle languageBundle() {
    return languageBundle;
  }

  public String getType() {
    return type;
  }

  public abstract void setProgressor(Progressor progressor);

  public abstract Node getNode();

  protected void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

}

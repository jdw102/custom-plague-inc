package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import ooga.model.gamestate.Progressor;
import ooga.model.region.PopulationNotFoundException;

public class TimeProgressBar extends ProgressBarView {

  private static final String MAX_STRING = "Max";
  private static final String DAY_STRING = "%s Days";
  private final javafx.scene.control.ProgressBar progressBar;
  private final Label label;
  private Progressor gameData;
  private final int max;
  private final StackPane group;


  public TimeProgressBar(String key, String type, ResourceBundle gameSettings,
      ResourceBundle languageBundle) {
    super(key, type, gameSettings, languageBundle);
    progressBar = new ProgressBar();
    progressBar.getStyleClass().add(String.format("%sprogressbar", key.toLowerCase()));
    max = Integer.parseInt(gameSettings.getString(String.format("%s%s", key, MAX_STRING)));
    label = new Label();
    group = new StackPane(progressBar, label);
    label.setId("TimeLabel");
  }

  @Override
  public void update() {
    double progress = 0;
    try {
      progress = gameData.getProgress();
    } catch (PopulationNotFoundException e) {
      showError(e);
    }
    progressBar.setProgress(progress / max);
    label.setText(String.format(DAY_STRING, (int) progress));
  }

  @Override
  public void setProgressor(Progressor progressor) {
    gameData = progressor;
  }

  @Override
  public Node getNode() {
    return group;
  }
}

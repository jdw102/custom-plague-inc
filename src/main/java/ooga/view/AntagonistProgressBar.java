package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ooga.model.gamestate.Progressor;
import ooga.model.region.PopulationNotFoundException;

public class AntagonistProgressBar extends ProgressBarView {

  private final javafx.scene.control.ProgressBar progressBar;
  private final int maximum;
  private final StackPane group;
  private final Label label;
  private Progressor antagonist;

  public AntagonistProgressBar(String key, String type, ResourceBundle gameSettings,
      ResourceBundle languageBundle) {
    super(key, type, gameSettings, languageBundle);
    progressBar = new javafx.scene.control.ProgressBar();
    progressBar.getStyleClass().add(String.format("%sprogressbar", key.toLowerCase()));
    maximum = Integer.parseInt(gameSettings.getString(String.format("%s%s", key, "Max")));
    label = new Label();
    group = new StackPane(progressBar, label);
  }

  @Override
  public void setProgressor(Progressor progressor) {
    this.antagonist = progressor;
    update();
  }

  @Override
  public Node getNode() {
    return group;
  }

  @Override
  public void update() {
    double ratio = 0;
    try {
      ratio = (antagonist.getProgress()) / maximum;
    } catch (PopulationNotFoundException e) {
      showError(e);
    }
    double percentage = Math.round(ratio * 100);
    label.setText(String.format("%s%%", percentage));
    progressBar.setProgress(ratio);
  }
}

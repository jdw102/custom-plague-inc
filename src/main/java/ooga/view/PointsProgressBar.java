package ooga.view;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ooga.model.gamestate.Progressor;
import ooga.model.region.PopulationNotFoundException;

public class PointsProgressBar extends ProgressBarView {

  private final double maximum;
  private final javafx.scene.control.ProgressBar progressBar;
  private final Label label;
  private final StackPane group;
  private Progressor userPoints;
  private final ResourceBundle languageBundle;

  public PointsProgressBar(String key, String type, ResourceBundle gameSettings,
      ResourceBundle languageBundle) {
    super(key, type, gameSettings, languageBundle);
    this.languageBundle = languageBundle;
    maximum = Double.parseDouble(gameSettings.getString(String.format("%s%s", key, "Max")));
    progressBar = new javafx.scene.control.ProgressBar();
    progressBar.getStyleClass().add(String.format("%sprogressbar", key.toLowerCase()));
    label = new Label();
    group = new StackPane(progressBar, label);
    label.setId("PointsLabel");
  }

  @Override
  public void setProgressor(Progressor progressor) {
    this.userPoints = progressor;
    update();
  }

  @Override
  public Node getNode() {
    return group;
  }

  @Override
  public void update() {
    double points = 0;
    try {
      points = userPoints.getProgress();
    } catch (PopulationNotFoundException e) {
      showError(e);
    }
    String text = String.format(languageBundle.getString("UserPoints"),
        NumberFormat.getNumberInstance(Locale.US).format((int) points));
    label.setText(text);
    double ratio = points / maximum;
    progressBar.setProgress(ratio);
  }

}

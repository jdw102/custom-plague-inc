package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import ooga.model.gamestate.Progressor;
import ooga.model.region.PopulationNotFoundException;

public class PopulationProgressBar extends ProgressBarView {

  private static final String MAX_STRING = "Max";
  private static final String POPULATIONS_STRING = "Populations";
  private final javafx.scene.control.ProgressBar progressBar;
  private final Label label;
  private Progressor gameData;
  private String[] populations;
  private final int max;
  private final StackPane group;


  public PopulationProgressBar(String key, String type, ResourceBundle gameSettings,
      ResourceBundle languageBundle) {
    super(key, type, gameSettings, languageBundle);
    try {
      populations = gameSettings.getString(String.format("%s%s", key, POPULATIONS_STRING))
          .split(" ");
    } catch (NullPointerException e) {
      populations = null;
    }
    progressBar = new ProgressBar();
    progressBar.getStyleClass().add(String.format("%sprogressbar", key.toLowerCase()));
    max = Integer.parseInt(gameSettings.getString(String.format("%s%s", key, MAX_STRING)));
    label = new Label();
    group = new StackPane(progressBar, label);
    label.setId("PopulationLabel");
  }

  @Override
  public void update() {
    double amt = 0;
    for (String s : populations) {
      try {
        amt += gameData.getProgress(s);
      } catch (PopulationNotFoundException e) {
        showError(e);
      }
    }
    System.out.println(amt / max);
    label.setText(Integer.toString((int) amt));
    progressBar.setProgress(amt / max);
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

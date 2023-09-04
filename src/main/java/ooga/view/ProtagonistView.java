package ooga.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.util.Observer;

public class ProtagonistView implements Observer {

  private static final String PATH = "games.%s.properties.ProtagonistMaxValues";
  private final Protagonist protagonist;
  private final String name;
  private final String description;
  private final ImageView image;
  private final HBox progressBarBox;
  private final Map<String, ProgressBar> progressBarMap;
  private final Map<String, Label> labelMap;
  private final Map<String, Double> maxValueMap;
  private final ResourceBundle resourceBundle;
  private String userName;


  public ProtagonistView(Protagonist protagonist, String name, String description, String imagePath,
      String gameType) {
    this.protagonist = protagonist;
    resourceBundle = ResourceBundle.getBundle(
        String.format(PATH, gameType));
    this.name = name;
    this.description = description;
    maxValueMap = new HashMap<>();
    progressBarMap = new HashMap<>();
    labelMap = new HashMap<>();
    InputStream stream;
    try {
      stream = new FileInputStream(getClass().getResource(imagePath).getPath());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    Image i = new Image(stream);
    image = new ImageView(i);
    progressBarBox = new HBox();
    progressBarBox.getStyleClass().add("progress-bar-box");
    createMaxValueMap();
    initializeProgressBars();
  }

  private void createMaxValueMap() {
    Iterator<String> iterator = resourceBundle.keySet().iterator();
    while (iterator.hasNext()) {
      String name = iterator.next();
      double val = Double.parseDouble(resourceBundle.getString(name));
      maxValueMap.put(name, val);
    }
  }

  private void initializeProgressBars() {
    Iterator<String> iterator = maxValueMap.keySet().iterator();
    while (iterator.hasNext()) {
      String name = iterator.next();
      ProgressBar progressBar = makeProgressBar(name);
      Label text = new Label();
      text.getStyleClass().add("protagonist-progress-bar-label");
      progressBarBox.getChildren().add(new StackPane(progressBar, text));
      progressBarMap.put(name, progressBar);
      labelMap.put(name, text);
    }
    update();
  }

  private ProgressBar makeProgressBar(String name) {
    ProgressBar progressBar = new ProgressBar();
    progressBar.getStyleClass().add("protagonist-progress-bar");
    Tooltip tooltip = new Tooltip(name);
    Tooltip.install(progressBar, tooltip);
    return progressBar;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ImageView getImage() {
    return image;
  }

  public int getId() {
    return protagonist.getId();
  }

  @Override
  public void update() {
    Modifiers core = protagonist.getCoreModifiers();
    Iterator<String> iterator = maxValueMap.keySet().iterator();
    while (iterator.hasNext()) {
      String name = iterator.next();
      ProgressBar progressBar = progressBarMap.get(name);
      Label label = labelMap.get(name);
      double amt = core.getModifier(name);
      label.setText(NumberFormat.getNumberInstance(Locale.US).format(amt));
      double ratio = amt / maxValueMap.get(name);
      progressBar.setProgress(ratio);
    }
  }

  public void updateUserName(String name) {
    this.userName = name;
  }

  public String getUserName() {
    return userName;
  }

  public HBox getProgressBars() {
    return progressBarBox;
  }
}

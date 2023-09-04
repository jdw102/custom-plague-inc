package ooga.view;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Author
 */

public class StartScreen {

  private static final String STYLESHEET = "/splash_stylesheets/StartScreen.css";
  private static final String SCENE_STYLE = "start-screen";
  private static final String BUTTON_STYLE = "optionButtons";
  private static final ResourceBundle DEFAULT_RESOURCE_PACKAGE = ResourceBundle.getBundle(
      "LabelsBundle");
  private final Stage myStage;
  private final ViewFactory myViewUtil;
  private final ResourceBundle myResource;
  private final Map<String, String> BUTTONS = new HashMap<String, String>(
      Map.of("Play", "playButton", "HowTo", "howtoButton", "Credits", "creditsButton"));
  private double HEIGHT;
  private double WIDTH;

  public StartScreen(Stage stage) {
    myStage = stage;
    myViewUtil = new ViewFactory();
    myResource = DEFAULT_RESOURCE_PACKAGE;
  }

  public Scene makeScene(Stage stage, EventHandler<ActionEvent> chooseOpen,
      EventHandler<ActionEvent> newWindow) {
    BorderPane root = new BorderPane();
    root.setCenter(mainButtons(chooseOpen, newWindow));
    Scene scene = new Scene(root);
    myViewUtil.adjustScene(root, SCENE_STYLE, getClass().getResource(STYLESHEET).toExternalForm());
    HEIGHT = stage.getHeight();
    WIDTH = stage.getWidth();
    myViewUtil.setMinWindow(stage, HEIGHT, WIDTH);
    return scene;
  }

  private VBox mainButtons(EventHandler<ActionEvent> chooseOpen,
      EventHandler<ActionEvent> newWindow) {
    VBox mainBox = new VBox();
    Button playButton = myViewUtil.createButton("Play", BUTTON_STYLE);
    playButton.setText(myResource.getString("playButton"));
    playButton.setOnAction(chooseOpen);
    Button newWindowButton = myViewUtil.createButton("NewWindow", BUTTON_STYLE);
    newWindowButton.setText(myResource.getString("NewWindowButton"));
    newWindowButton.setOnAction(newWindow);
    mainBox.getChildren()
        .addAll(playButton, newWindowButton);
    mainBox.setAlignment(Pos.CENTER);
    mainBox.setSpacing(30);
    return mainBox;
  }
}

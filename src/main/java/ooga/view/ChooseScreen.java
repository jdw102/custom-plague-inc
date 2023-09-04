package ooga.view;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChooseScreen {

  private static final String GAME_PATH = "/games";
  private static final String LANGUAGE_PATH = "/games/%s/properties/languages";
  private static final String STYLESHEET = "/splash_stylesheets/ChooseScreen.css";
  private static final String DISPLAY_STRING = "%s Inc.";
  private static final ResourceBundle DEFAULT_RESOURCE_PACKAGE = ResourceBundle.getBundle(
      "LabelsBundle");
  private final Stage stage;
  private final ViewFactory viewFactory;
  private String gameType;
  private String language;
  private ResourceBundle myResource;
  private Button nextButton;
  private boolean gameSelected;
  private boolean languageSelected;
  private String gamePrompt;
  private String languagePrompt;
  private Button loadButton;

  public ChooseScreen(Stage theStage) {
    stage = theStage;
    viewFactory = new ViewFactory();
    gameSelected = false;
    languageSelected = false;
  }

  public Scene makeScene(Stage stage, EventHandler<ActionEvent> goBack,
      EventHandler<ActionEvent> startGame, EventHandler<ActionEvent> openLoadScreen) {
    BorderPane root = new BorderPane();
    root.setTop(createCenter());
    root.setCenter(createBottom(goBack, startGame, openLoadScreen));
    Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
    viewFactory.adjustScene(root, "splash-screen",
        getClass().getResource(STYLESHEET).toExternalForm());
    return scene;
  }

  private HBox createCenter() {
    HBox centerBox = new HBox();
    VBox gameBox = new VBox();
    myResource = DEFAULT_RESOURCE_PACKAGE;
    languagePrompt = myResource.getString("SelectLanguage");
    gamePrompt = myResource.getString("ChooseGame");
    Label testGameType = viewFactory.createLabel(gameType, "TestGameType");
    ComboBox gameChooser = viewFactory.createComboBox(gamePrompt, List.of(), "GameChooser");
    gameChooser.getStyleClass().add("gamedropdown");
    makeGameSelector(gameChooser);
    gameBox.getChildren().addAll(gameChooser, testGameType);
    VBox languageBox = new VBox();
    Label testLanguage = viewFactory.createLabel(language, "TestLanguage");
    ComboBox languageChooser = viewFactory.createComboBox(languagePrompt, List.of(),
        "LanguageChooser");
    languageChooser.getStyleClass().add("languagedropdown");
    languageChooser.setOnAction(e -> {
      language = (String) languageChooser.getValue();
      testLanguage.setText(language);
      languageSelected = (language != null && language != "");
      attemptButtonEnable();
    });
    languageBox.getChildren().addAll(languageChooser, testLanguage);
    gameChooser.setOnAction(e -> {
      gameType = gameChooser.getValue().toString().split(" ")[0];
      testGameType.setText(gameType);
      updateLanguages(languageChooser, gameType);
      gameSelected = true;
      attemptButtonEnable();
    });
    centerBox.getChildren().addAll(gameBox, languageBox);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.getStyleClass().add("dropdownBox");
    return centerBox;
  }

  private void attemptButtonEnable() {
    nextButton.setDisable(!languageSelected || !gameSelected);
    loadButton.setDisable(!languageSelected);
  }

  private HBox createBottom(EventHandler<ActionEvent> goBack,
      EventHandler<ActionEvent> startGame, EventHandler<ActionEvent> openLoadScreen) {
    HBox bottomBox = new HBox();

    nextButton = viewFactory.createButton("Next", "transitionButton");
    nextButton.setText(myResource.getString("nextButton"));
    nextButton.setOnAction(startGame);
    nextButton.setDisable(true);

    loadButton = viewFactory.createButton("LoadDB", "transitionButton");
    loadButton.setText(myResource.getString("loadButton"));
    loadButton.setOnAction(openLoadScreen);
    loadButton.setDisable(true);

    Button backButton = viewFactory.createButton("Back", "transitionButton");
    backButton.setText(myResource.getString("backButton"));
    backButton.setOnAction(goBack);
    bottomBox.getChildren().addAll(backButton, nextButton, loadButton);
    bottomBox.setSpacing(50);

    bottomBox.setAlignment(Pos.CENTER);
    return bottomBox;
  }

  public String getGameType() {
    return gameType;
  }

  public String getLanguage() {
    return language;
  }

  private void makeGameSelector(ComboBox<String> comboBox) {
    File gameDirectory = new File(
        getClass().getResource(GAME_PATH).getPath());
    String[] languageFiles = gameDirectory.list();
    if (languageFiles != null) {
      for (String f : languageFiles) {
        String s = f.split("\\.")[0];
        comboBox.getItems().add(String.format(DISPLAY_STRING, s));
      }
    }
  }

  private void updateLanguages(ComboBox<String> comboBox, String gameType) {
    String name = String.format(LANGUAGE_PATH, gameType);
    File languageDirectory = new File(
        getClass().getResource(name).getPath());
    comboBox.getItems().removeAll(comboBox.getItems());
    String[] languageFiles = languageDirectory.list();
    if (languageFiles != null) {
      for (String f : languageFiles) {
        String s = f.split("\\.")[0];
        comboBox.getItems().add(s);
      }
    }
    languageSelected = false;
    attemptButtonEnable();
  }
}
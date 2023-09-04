package ooga.view;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import ooga.controller.Controller;

public class ProtagonistSelector {

  private static final String LANGUAGE = "games.%s.properties.languages.%s";
  private static final String SAVE_PATH = "/games/%s/save.json";
  private final String STYLESHEET = "/games/%s/stylesheets/ProtagonistSelection.css";
  private final VBox infoBox;
  private final Label name;
  private final Label description;
  private final ImageView image;
  private final ComboBox<ProtagonistView> protagonistComboBox;
  private final TextField textField;
  private final String gameType;
  private final String language;
  private final ViewFactory viewFactory;
  private final ResourceBundle resourceBundle;
  private final Map<Integer, ProtagonistView> protagonistViews;
  private Controller controller;
  private ProtagonistView protagonistView;
  private BorderPane root;
  private Stage stage;
  private Button button;
  private boolean selected;
  private boolean hasSave;
  private File saveFile;
  private LoadSavePopUp loadSavePopUp;

  public ProtagonistSelector(String gameType, String language) {
    this.gameType = gameType;
    this.language = language;
    resourceBundle = ResourceBundle.getBundle(
        String.format(LANGUAGE, gameType, language));
    viewFactory = new ViewFactory();
    protagonistViews = new HashMap<>();
    name = new Label("");
    name.setId("ProtagonistType");
    selected = false;
    name.setTextFill(Color.WHITE);
    description = new Label("");
    description.setId("ProtagonistDescription");
    description.setTextFill(Color.WHITE);
    image = new ImageView();
    image.setFitHeight(80);
    image.setFitWidth(80);
    protagonistComboBox = new ComboBox<>();
    protagonistComboBox.setId("ProtagonistComboBox");
    textField = new TextField();
    textField.setId("ProtagonistUserName");
    textField.setPromptText(resourceBundle.getString("EnterText"));
    textField.setOnKeyTyped(event -> adjustButton());
    textField.getStyleClass().add("protagonist-name-field");
    try {
      saveFile = new File(
          getClass().getResource(String.format(SAVE_PATH, gameType)).getPath());
      hasSave = true;
    } catch (NullPointerException e) {
      hasSave = false;
    }

    Callback<ListView<ProtagonistView>, ListCell<ProtagonistView>> cellFactory = new Callback<ListView<ProtagonistView>, ListCell<ProtagonistView>>() {

      @Override
      public ListCell<ProtagonistView> call(ListView<ProtagonistView> l) {
        return new ListCell<ProtagonistView>() {

          @Override
          protected void updateItem(ProtagonistView item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
              setGraphic(null);
            } else {
              setText(item.getName());
            }
          }
        };
      }
    };
    protagonistComboBox.setCellFactory(cellFactory);
    protagonistComboBox.setButtonCell(cellFactory.call(null));
    protagonistComboBox.setOnAction(event -> changeSelection(protagonistComboBox.getValue()));
    protagonistComboBox.setPromptText("Choose Protagonist");
    infoBox = new VBox(image, name, description, protagonistComboBox, textField);
    infoBox.getStyleClass().add("protagonist-combo-box");
    infoBox.setAlignment(Pos.CENTER);
  }

  public void checkSaveFile(EventHandler<ActionEvent> loadGame) {
    if (hasSave) {
      loadSavePopUp = new LoadSavePopUp(resourceBundle, loadGame);
      loadSavePopUp.open();
    }
  }

  private void adjustButton() {
    boolean empty = textField.getText().equals("");
    button.setDisable(empty || !selected);
  }

  public Scene makeScene(Stage stage, EventHandler<ActionEvent> startGame) {
    root = new BorderPane();
    this.stage = stage;
    root.getStyleClass().add("protagonist-screen");
    root.setCenter(infoBox);
    button = viewFactory.createButton("ProtagonistStartButton", "start-button");
    button.setDisable(true);
    button.setText(resourceBundle.getString("StartButton"));
    button.setOnAction(event -> {
      protagonistView.updateUserName(textField.getText());
      startGame.handle(event);
    });
    button.setAlignment(Pos.CENTER);
    infoBox.getChildren().add(button);
    Scene scene = new Scene(root, 1400, 800);
    String name = String.format(STYLESHEET, gameType);
    scene.getStylesheets()
        .add(getClass().getResource(name).toExternalForm());
    root.getStyleClass().add("protagonist-selector");
    return scene;
  }

  public void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.showAndWait();
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  private void changeSelection(ProtagonistView protagonistView) {
    this.protagonistView = protagonistView;
    name.setText(protagonistView.getName());
    description.setText(protagonistView.getDescription());
    image.setImage(protagonistView.getImage().getImage());
    selected = true;
    adjustButton();
    root.setTop(protagonistView.getProgressBars());
    controller.selectProtagonist(protagonistView.getId());
  }

  public void addProtagonist(ProtagonistView protagonistView) {
    protagonistViews.put(protagonistView.getId(), protagonistView);
    ObservableList<ProtagonistView> list = protagonistComboBox.getItems();
    list.add(protagonistView);
    protagonistComboBox.setItems(list);
  }

  public ProtagonistView getProtagonistView(int id) {
    return protagonistViews.get(id);
  }
}
package ooga.view.databaseScreen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ooga.controller.database.DatabaseConnection;
import ooga.controller.database.DatabaseException;
import ooga.view.ViewFactory;
import org.json.simple.JSONObject;

public class LoadRemoteScreen {

  private static final String STYLESHEET = "/splash_stylesheets/StartScreen.css";
  private static final String SCENE_STYLE = "start-screen";
  private static final String BUTTON_STYLE = "optionButtons";
  private static ResourceBundle DEFAULT_RESOURCE_PACKAGE;
  private final Stage myStage;
  private final ViewFactory vf = new ViewFactory();
  private final DatabaseConnection dbConn;

  private boolean selectedSave = false;
  private JSONObject saveData;
  private String selectedGameType;
  private final Text selectionShow = new Text("Selected: NONE");
  private VBox results;
  private Button confirmSaveToLoad;

  private Dialog gameSaveInfo;


  public LoadRemoteScreen(Stage stage, DatabaseConnection databaseConnection, String language) {
    try {
      String s = String.format("LabelsBundle%s",
          ((language.equals("English")) ? "" : String.format("_%s", language)));
      DEFAULT_RESOURCE_PACKAGE = ResourceBundle.getBundle(s);
    } catch (MissingResourceException e) {
      DEFAULT_RESOURCE_PACKAGE = ResourceBundle.getBundle(
          "LabelsBundle");
    }

    myStage = stage;
    dbConn = databaseConnection;
    saveData = new JSONObject();
  }

  public Scene makeScene(EventHandler<ActionEvent> selectSave, EventHandler<ActionEvent> goBack) {
    BorderPane root = new BorderPane();
    root.setCenter(makeButtons(selectSave, goBack));
    Scene scene = new Scene(root, 1400, 800);
    //vf.adjustScene(root, SCENE_STYLE, getClass().getResource(STYLESHEET).toExternalForm());
    return scene;

  }

  private VBox makeButtons(EventHandler<ActionEvent> selectSave, EventHandler<ActionEvent> goBack) {
    TextField usernameField = new TextField();
    results = new VBox();
    Text title = new Text(DEFAULT_RESOURCE_PACKAGE.getString("loadTitle"));
    title.setFont(new Font(20));
    usernameField.setPromptText(DEFAULT_RESOURCE_PACKAGE.getString("usernameField"));
    usernameField.setOnAction(e -> {
      results.getChildren().clear();
      results.getChildren().add(dbResults(usernameField.getText()));
    });
    confirmSaveToLoad = vf.createButton("confirmLoad", "start-button");
    confirmSaveToLoad.setText(DEFAULT_RESOURCE_PACKAGE.getString("loadButton"));
    confirmSaveToLoad.setDisable(true);
    confirmSaveToLoad.setOnAction(selectSave);
    Button goBackButton = new Button();
    goBackButton.setText(DEFAULT_RESOURCE_PACKAGE.getString("backButton"));
    goBackButton.setOnAction(goBack);
    VBox v = new VBox();
    v.getChildren()
        .addAll(title, usernameField, results, selectionShow, confirmSaveToLoad, goBackButton);
    return v;
  }

  private void confirmEnabler() {
    confirmSaveToLoad.setDisable(!selectedSave || saveData.isEmpty());
  }

  private VBox dbResults(String username) {
    VBox pseudoTable = new VBox();
    try {
      JSONObject userData = dbConn.getUser(username);
      for (Object game : userData.keySet()) {
        HBox h = new HBox();
        String gameName = game.toString();
        Text title = new Text(gameName);
        Button viewInfo = vf.createButton(String.format("viewSave%s", gameName), "start-button");
        viewInfo.setText(DEFAULT_RESOURCE_PACKAGE.getString("saveDetails"));
        viewInfo.setOnAction(e -> {
          showSaveInfo((JSONObject) userData.get(gameName), gameName);
        });
        Button selectSave = vf.createButton(String.format("selectSave%s", gameName),
            "start-button");
        selectSave.setText(DEFAULT_RESOURCE_PACKAGE.getString("selectSave"));
        selectSave.setOnAction(e -> {
          setSelfSaveJSON((JSONObject) userData.get(gameName), gameName);
        });
        h.getChildren().addAll(title, viewInfo, selectSave);
        pseudoTable.getChildren().add(h);
      }

    } catch (DatabaseException e) {
      Text notFound = new Text(
          String.format(DEFAULT_RESOURCE_PACKAGE.getString("unrecognizedUsername"), username));
      pseudoTable.getChildren().add(notFound);
      System.out.println(e.getMessage());
    }
    return pseudoTable;
  }

  private void showSaveInfo(JSONObject save, String gameName) {
    gameSaveInfo = new Dialog();
    gameSaveInfo.setTitle(
        String.format(DEFAULT_RESOURCE_PACKAGE.getString("saveinfoTitle"), gameName));
    gameSaveInfo.setHeaderText(null);

    try {
      ObjectMapper o = new ObjectMapper();
      JsonNode formatting = o.readTree(save.toJSONString());
      gameSaveInfo.setContentText(formatting.toPrettyString());
    } catch (JsonProcessingException e) {
      gameSaveInfo.setContentText(save.toJSONString());
    }
    gameSaveInfo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    gameSaveInfo.showAndWait();
  }


  private void setSelfSaveJSON(JSONObject save, String gamename) {
    JSONObject node = new JSONObject();
    node.put("saveData", save);
    if (saveData.toJSONString().equals(node.toJSONString())) {
      selectedSave = !selectedSave;
    } else {
      selectedSave = true;
    }

    saveData = node;
    selectedGameType = selectedSave ? gamename : "";
    selectionShow.setText(
        String.format("Selected: %s", (selectedGameType.isEmpty() ? "NONE" : selectedGameType)));
    confirmEnabler();
  }

  public String getSelectedGameType() {
    return this.selectedGameType;
  }

  public JSONObject getSaveData() {
    return this.saveData;
  }
}

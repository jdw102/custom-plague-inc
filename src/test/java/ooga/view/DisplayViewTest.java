package ooga.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ooga.controller.Controller;
import ooga.controller.database.DatabaseConnection;
import ooga.view.databaseScreen.LoadRemoteScreen;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class DisplayViewTest extends DukeApplicationTest {

  @Override
  public void start(Stage stage) {
    openStartScreen(stage);
  }

  public void openStartScreen(Stage stage) {
    StartScreen startScreen = new StartScreen(stage);
    stage.setScene(startScreen.makeScene(stage, event -> openChooseScreen(stage), event -> {
      start(new Stage());
    }));
    stage.setMaximized(true);
    stage.show();
  }

  public void openChooseScreen(Stage stage) {
    ChooseScreen chooseScreen = new ChooseScreen(stage);
    stage.setScene(chooseScreen.makeScene(stage, event -> openStartScreen(stage),
        event -> openProtagonistSelection(stage,
            chooseScreen.getGameType(), chooseScreen.getLanguage(), null),
        event -> openLoadScreen(stage, chooseScreen.getLanguage())
    ));
    stage.show();
    stage.setMaximized(true);
  }

  public void openLoadScreen(Stage stage, String language) {
    DatabaseConnection dbc = new DatabaseConnection();
    LoadRemoteScreen lrs = new LoadRemoteScreen(stage, dbc, language);
    stage.setScene(
        lrs.makeScene(event -> openProtagonistSelection(stage, lrs.getSelectedGameType(), language, lrs.getSaveData()),
            event -> backToMainMenu(stage)));
    stage.show();
  }


  public void openProtagonistSelection(Stage stage, String gameType, String language, JSONObject jsonObject) {
    Controller controller = new Controller();
    ProtagonistSelector protagonistScreen = new ProtagonistSelector(gameType, language);
    controller.setUpProtagonistSelection(gameType, protagonistScreen);
    if (jsonObject != null){
      controller.prepareRemoteSave(jsonObject);
      openDisplayView(stage, gameType, language, controller);
    }
    else{
      protagonistScreen.checkSaveFile(
          event -> openDisplayView(stage, gameType, language, controller));
      protagonistScreen.setController(controller);
      stage.setScene(protagonistScreen.makeScene(stage,
          event -> openDisplayView(stage, gameType, language, controller)));
    }
  }

  public void openDisplayView(Stage stage, String gameType, String language, Controller controller) {
    DisplayView displayView = new DisplayView(gameType, language);
    displayView.setController(controller);
    controller.setUpGame(displayView);
    stage.setScene(displayView.makeScene(stage, event -> backToMainMenu(stage)));
    stage.setTitle(gameType);
  }

  public void backToMainMenu(Stage stage) {
    stage.close();
    start(new Stage());
  }

  @Test
  void openDisplayView() {
    Button playButton = lookup("#Play").query();
    clickOn(playButton);
    ComboBox myGameChooser = lookup("#GameChooser").query();
    ComboBox myLanguageChooser = lookup("#LanguageChooser").query();
    select(myGameChooser, "Plague");
    select(myLanguageChooser, "English");
    Button nextButton = lookup("#Next").query();
    clickOn(nextButton);
    ComboBox<ProtagonistView> myProtagonistSelector = lookup("#ProtagonistComboBox").query();
    ProtagonistView myProtagonistView = myProtagonistSelector.getItems().get(0);
    selectpv(myProtagonistSelector, myProtagonistView);
    TextField myProtagonistName = lookup("#ProtagonistUserName").query();
    clickOn(myProtagonistName).write("TEST INPUT");
    Label myProtagonistType = lookup("#ProtagonistType").query();
    assertEquals(myProtagonistType.getText(), "Bacteria");
    Label myProtagonistDescription = lookup("#ProtagonistDescription").query();
    assertEquals(myProtagonistDescription.getText(), "Small but deadly!");
  }


}

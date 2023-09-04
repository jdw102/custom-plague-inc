package ooga;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import ooga.controller.Controller;
import ooga.controller.database.DatabaseConnection;
import ooga.controller.parsers.GameConfigParser;
import ooga.view.ChooseScreen;
import ooga.view.DisplayView;
import ooga.view.ProtagonistSelector;
import ooga.view.StartScreen;
import ooga.view.databaseScreen.LoadRemoteScreen;
import org.json.simple.JSONObject;

/**
 * Feel free to completely change this code or delete it entirely.
 */
public class Main extends Application {

  private static final ResourceBundle RNG_BUNDLE = ResourceBundle.getBundle("RngBundle");
  public static final Random GLOBAL_RNG = new Random(
      Integer.parseInt(RNG_BUNDLE.getString("RandomSeed")));

  public static void main(String[] args) {
    launch(args);
    GameConfigParser thing = new GameConfigParser();
    //thing.testJsonParse();
  }

  /**
   * A method to test (and a joke :).
   */
  public double getVersion() {
    return 0.001;
  }

  /**
   * Start of the program.
   */
  public void start(Stage stage) throws FileNotFoundException {
    openStartScreen(stage);
  }

  public void openStartScreen(Stage stage) {
    StartScreen startScreen = new StartScreen(stage);
    stage.setScene(startScreen.makeScene(stage, event -> openChooseScreen(stage), event -> {
      try {
        start(new Stage());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
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
        lrs.makeScene(event -> openProtagonistSelection(stage, lrs.getSelectedGameType(), language,
                lrs.getSaveData()),
            event -> backToMainMenu(stage)));
    stage.show();
  }


  public void openProtagonistSelection(Stage stage, String gameType, String language,
      JSONObject jsonObject) {
    Controller controller = new Controller();
    ProtagonistSelector protagonistScreen = new ProtagonistSelector(gameType, language);
    controller.setUpProtagonistSelection(gameType, protagonistScreen);
    if (jsonObject != null) {
      controller.prepareRemoteSave(jsonObject);
      openDisplayView(stage, gameType, language, controller);
    } else {
      protagonistScreen.checkSaveFile(
          event -> openDisplayView(stage, gameType, language, controller));
      protagonistScreen.setController(controller);
      stage.setScene(protagonistScreen.makeScene(stage,
          event -> openDisplayView(stage, gameType, language, controller)));
    }
  }

  public void openDisplayView(Stage stage, String gameType, String language,
      Controller controller) {
    DisplayView displayView = new DisplayView(gameType, language);
    displayView.setController(controller);
    controller.setUpGame(displayView);
    stage.setScene(displayView.makeScene(stage, event -> backToMainMenu(stage)));
    stage.setTitle(gameType);
  }

  public void backToMainMenu(Stage stage) {
    stage.close();
    try {
      start(new Stage());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}

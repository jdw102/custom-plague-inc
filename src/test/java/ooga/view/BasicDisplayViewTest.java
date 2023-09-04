package ooga.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ooga.controller.Controller;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class BasicDisplayViewTest extends DukeApplicationTest {
  private String language = "English";
  private String myGameType = "Plague";
  private DisplayView displayView;
  private Controller controller;

  @Override
  public void start(Stage stage) {
    displayView = new DisplayView(myGameType, language);
    controller = new Controller();
    displayView.setController(controller);
    stage.setScene(displayView.makeScene(stage, event -> backToMainMenu(stage)));
    stage.show();
  }

  public void backToMainMenu(Stage stage) {
    stage.close();
    start(new Stage());
  }

  @Test
  void hasPerkButton() {
    Button perkButton = lookup("#Perk").query();
    assertEquals(perkButton.getText(), "");
  }

  @Test
  void hasDataButton() {
    Button dataButton = lookup("#WorldData").query();
    assertEquals(dataButton.getText(), "");
  }


  @Test
  void hasLabels() {
    Label testUserSpread = lookup("#UserSpread").query();
    assertEquals(testUserSpread.getText(), "");
    Label testDeadSpread = lookup("#DeadPeople").query();
    assertEquals(testDeadSpread.getText(), "");
  }

  @Test
  void hasPlayPauseButtons() {
    Button playButton = lookup("#PlayButton").query();
    assertEquals(playButton.getText(), "");
    Button pauseButton = lookup("#PauseButton").query();
    assertEquals(pauseButton.getText(), "");
    Button fastForwardButton = lookup("#FastForwardButton").query();
    assertEquals(fastForwardButton.getText(), "");
  }

  @Test
  void hasProgressBars() {
    Node leftProgressBar = lookup("#UserPoints").query();
    assertEquals(leftProgressBar.isVisible(), true);
    Node rightProgressBar = lookup("#AntagonistProgress").query();
    assertEquals(rightProgressBar.isVisible(), true);
  }

  @Test
  void testGameInfoPopUp() {
    sleep(1000);
    DialogPane dp = lookup("#GameInfoPane").query();
    assertTrue(dp.isVisible());
  }
}

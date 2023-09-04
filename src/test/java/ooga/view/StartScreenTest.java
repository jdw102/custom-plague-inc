package ooga.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import ooga.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.WindowMatchers;
import util.DukeApplicationTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartScreenTest extends DukeApplicationTest {
  private StartScreen myStartScreen;
  private Scene myScene;
  private Stage myStage;

  @Override
  public void start(Stage stage) {
    myStartScreen = new StartScreen(stage);
    stage.setScene(myStartScreen.makeScene(stage, event -> System.out.println("test"), event -> System.out.println("test")));
    myScene = stage.getScene();
    myStage = stage;
    stage.show();
  }

  @Test
  void programStart(){
    FxAssert.verifyThat(window(myScene), WindowMatchers.isShowing());
  }
  @Test
  void testPlayButton(){
    String expected = "Play";
    Button button = lookup("#Play").query();
    Assertions.assertEquals(button.getText(), expected);
  }


  @Test
  void playButton() {
    Button myStartGameButton = lookup("#Play").query();
    clickOn(myStartGameButton);
  }
}

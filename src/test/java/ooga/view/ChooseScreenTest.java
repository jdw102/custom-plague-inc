package ooga.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class ChooseScreenTest extends DukeApplicationTest {

  private ChooseScreen myChooseScreen;

  private ComboBox myGameChooser;
  private ComboBox myLanguageChooser;
  private Button mySplashScreenNextButton;
  private Label myTestGameType;
  private Label myTestLanguage;

  @Override
  public void start(Stage stage) {
    myChooseScreen = new ChooseScreen(stage);

    stage.setScene(myChooseScreen.makeScene(stage, event ->  System.out.println("test"), event -> System.out.println("test"), event -> System.out.println("test")));
    stage.show();

    myGameChooser = lookup("#GameChooser").query();
    myLanguageChooser = lookup("#LanguageChooser").query();
    mySplashScreenNextButton = lookup("#Next").query();

    myTestGameType = lookup("#TestGameType").query();
    myTestLanguage = lookup("#TestLanguage").query();
  }

  @Test
  void testGameChooser() {
    String expected = "games/Trump";
    select(myGameChooser, expected);
    assertLabelText(expected, myTestGameType);
  }

  @Test
  void testLanguageChooser() {
    String expected = "Spanish";
    select(myLanguageChooser, expected);
    assertLabelText(expected, myTestLanguage);
  }

  @Test
  void inactiveNextButton() {
    assertEquals(mySplashScreenNextButton.disableProperty().get(), true);
  }

  @Test
  void activeNextButton() {
    select(myGameChooser, "Plague");
    select(myLanguageChooser, "English");
    assertEquals(mySplashScreenNextButton.disableProperty().get(), false);
  }
  private void assertLabelText(String expected, Label myLabel) {
    assertEquals(expected, myLabel.getText());
  }
}
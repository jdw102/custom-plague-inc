package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameInfoPopUp {

  private final Dialog dialog;

  public GameInfoPopUp(ResourceBundle languageBundle) {
    dialog = new Dialog();
    ButtonType startButton = new ButtonType(languageBundle.getString("StartButton"),
        ButtonData.OK_DONE);
    dialog.setHeaderText(languageBundle.getString("ExplanationHeader"));
    dialog.setContentText(languageBundle.getString("GameExplanation"));
    dialog.getDialogPane().getButtonTypes().add(startButton);
    dialog.getDialogPane().setId("GameInfoPane");
    JSONArray jsonArray = new JSONArray();
    jsonArray.add(new JSONObject());
  }

  public void open() {
    dialog.show();
  }

}

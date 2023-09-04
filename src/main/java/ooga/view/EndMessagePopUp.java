package ooga.view;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public class EndMessagePopUp {

  private final Dialog dialog;

  public EndMessagePopUp(ResourceBundle languageBundle, EventHandler<ActionEvent> goBack) {
    dialog = new Dialog();
    ButtonType exitButton = new ButtonType(languageBundle.getString("ExitButton"),
        ButtonData.FINISH);
    dialog.getDialogPane().getButtonTypes().add(exitButton);
    dialog.setResultConverter(createExitCallBack(goBack));
  }

  private Callback<ButtonType, Boolean> createExitCallBack(EventHandler<ActionEvent> goBack) {
    Callback<ButtonType, Boolean> cb = new Callback<ButtonType, Boolean>() {
      @Override
      public Boolean call(ButtonType param) {
        goBack.handle(new ActionEvent());
        return false;
      }
    };
    return cb;
  }

  public void open(String message) {
    dialog.setHeaderText(message);
    dialog.show();
  }

}

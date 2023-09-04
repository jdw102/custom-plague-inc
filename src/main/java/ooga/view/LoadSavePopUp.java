package ooga.view;


import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public class LoadSavePopUp {

  private final Dialog dialog;

  public LoadSavePopUp(ResourceBundle languageBundle, EventHandler<ActionEvent> loadGame) {
    ButtonType yesButton = new ButtonType(languageBundle.getString("YesButton"), ButtonData.YES);
    ButtonType noButton = new ButtonType(languageBundle.getString("NoButton"), ButtonData.NO);
    dialog = new Dialog();
    dialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
    dialog.setHeaderText(languageBundle.getString("LoadGamePopUp"));
    dialog.setResultConverter(createLoadCallBack(loadGame));
  }

  public void open() {
    dialog.show();
  }

  private Callback<ButtonType, Boolean> createLoadCallBack(EventHandler<ActionEvent> loadGame) {
    Callback<ButtonType, Boolean> cb = new Callback<ButtonType, Boolean>() {
      @Override
      public Boolean call(ButtonType param) {
        if (param.getButtonData() == ButtonData.YES) {
          loadGame.handle(new ActionEvent());
        } else {
          dialog.close();
        }
        return false;
      }
    };
    return cb;
  }

}

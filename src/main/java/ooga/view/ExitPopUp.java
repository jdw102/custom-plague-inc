package ooga.view;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;
import ooga.model.gamestate.GameState;

public class ExitPopUp {

  private static final String SAVE_PATH = "/games/%s/save.json";
  private final Dialog dialog;
  private Dialog saveDialog;
  private final GameState gameState;
  private String gameType;
  private final ResourceBundle rb;
  private String username;


  public ExitPopUp(ResourceBundle languageBundle, GameState gameState) {
    dialog = new Dialog();
    this.gameType = gameType;
    this.gameState = gameState;
    rb = languageBundle;
  }

  public void setActions(EventHandler<ActionEvent> deleteFile,
      EventHandler<ActionEvent> goBack, EventHandler<ActionEvent> saveGame) {
    ButtonType yesButton = new ButtonType(rb.getString("YesButton"), ButtonData.YES);
    ButtonType noButton = new ButtonType(rb.getString("NoButton"), ButtonData.NO);
    dialog.setHeaderText(rb.getString("ExitGameMessage"));
    dialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
    dialog.setResultConverter(createExitCallBack(goBack));
    saveDialog = new Dialog();
    saveDialog.setHeaderText(rb.getString("SaveGameMessage"));
    saveDialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
    saveDialog.setResultConverter(createSaveCallBack(goBack, saveGame, deleteFile));
  }

  public void open() {
    dialog.show();
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String name) {
    this.username = name;
  }

  /**
   * Creates the call back that is triggered when the dialog is closed. If save is clicked, the
   * InfoText record is updated. Else nothing is saved, either way the fields are disabled.
   *
   * @return Callback to be triggered when dialog closes
   */
  private Callback<ButtonType, Boolean> createSaveCallBack(EventHandler<ActionEvent> goBack,
      EventHandler<ActionEvent> saveGame, EventHandler<ActionEvent> deleteFile) {
    Callback<ButtonType, Boolean> cb = new Callback<ButtonType, Boolean>() {
      @Override
      public Boolean call(ButtonType param) {
        if (param.getButtonData() == ButtonData.YES) {
          saveGame.handle(new ActionEvent());
        } else {
          deleteFile.handle(new ActionEvent());
        }
        goBack.handle(new ActionEvent());
        return false;
      }
    };
    return cb;
  }

  /**
   * Creates the call back that is triggered when the dialog is closed. If save is clicked, the
   * InfoText record is updated. Else nothing is saved, either way the fields are disabled.
   *
   * @return Callback to be triggered when dialog closes
   */
  private Callback<ButtonType, Boolean> createExitCallBack(EventHandler<ActionEvent> goBack) {
    Callback<ButtonType, Boolean> cb = new Callback<ButtonType, Boolean>() {
      @Override
      public Boolean call(ButtonType param) {
        if (param.getButtonData() == ButtonData.YES) {
          dialog.close();
          if (gameState.isRunning()) {
            saveDialog.show();
          } else {
            goBack.handle(new ActionEvent());
          }
        } else if (param.getButtonData() == ButtonData.NO) {
          dialog.close();
        }
        return false;
      }
    };
    return cb;
  }
}

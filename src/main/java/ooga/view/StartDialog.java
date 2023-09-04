package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public class StartDialog {

  private final Dialog dialog;
  private final ResourceBundle languageBundle;

  public StartDialog(Runnable runnable, ResourceBundle languageBundle) {
    dialog = new Dialog();
    this.languageBundle = languageBundle;
    dialog.setResultConverter(createDialogCallback(runnable));
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
  }

  /**
   * Creates the call back that is triggered when the dialog is closed. If save is clicked, the
   * InfoText record is updated. Else nothing is saved, either way the fields are disabled.
   *
   * @return Callback to be triggered when dialog closes
   */
  private Callback<ButtonType, Boolean> createDialogCallback(Runnable runnable) {
    Callback<ButtonType, Boolean> cb = new Callback<ButtonType, Boolean>() {
      @Override
      public Boolean call(ButtonType param) {
        if (param == ButtonType.YES) {
          runnable.run();
        }
        return false;
      }
    };
    return cb;
  }

  public void open() {
    dialog.show();
  }

  public void updateName(String name) {
    String title = languageBundle.getString("StartDialogTitle");
    dialog.setContentText(String.format(title, name));
  }

}

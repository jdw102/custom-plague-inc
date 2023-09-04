package ooga.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import ooga.model.region.RandomEvent;
import ooga.util.Observer;

public class EventPopUp implements Observer {

  private final Collection<RandomEvent> randomEvents;
  private final Dialog dialog;

  public EventPopUp(ResourceBundle languageBundle) {
    dialog = new Dialog();
    ButtonType buttonType = new ButtonType(languageBundle.getString("CloseButton"),
        ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().add(buttonType);
    randomEvents = new ArrayList<>();
  }

  public void addEvent(RandomEvent e) {
    randomEvents.add(e);
  }

  @Override
  public void update() {
    for (RandomEvent e : randomEvents) {
      if (e.isAvailable() && e.isActivated()) {
        dialog.setHeaderText(e.getMessage());
        dialog.show();
      }
    }
  }
}

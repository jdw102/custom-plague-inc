package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ooga.controller.Controller;

public class PerkPurchaser {

  private final BorderPane borderPane;
  private final TextArea textArea;
  private final Button button;
  private final String purchaseText;
  private final TextField cost;
  private final String refundText;
  private final Label perkTitle;
  private final String fundsMessage;
  private PerkView perkView;
  private Controller controller;

  public PerkPurchaser(ResourceBundle languageBundle) {
    borderPane = new BorderPane();
    textArea = new TextArea();
    textArea.setWrapText(true);
    textArea.getStyleClass().add("perk-text-area");
    textArea.setDisable(true);
    purchaseText = languageBundle.getString("PurchasePerkButton");
    refundText = languageBundle.getString("RefundPerkButton");
    button = new Button(purchaseText);
    button.setId("PerkPurchaseButton");
    button.setDisable(true);
    button.setOnAction(event -> onClick());
    button.getStyleClass().add("buy-button");
    perkTitle = new Label();
    perkTitle.setId("PerkTitle");
    perkTitle.getStyleClass().add("perk-title");
    cost = new TextField();
    cost.setId("PerkCostLabel");
    cost.getStyleClass().add("cost");
    cost.setDisable(true);
    HBox hBox = new HBox(button, cost);
    hBox.getStyleClass().add("perk-bottom-box");
    VBox vBox = new VBox(perkTitle, textArea, hBox);
    vBox.getStyleClass().add("perk-box");
    borderPane.setCenter(vBox);
    fundsMessage = languageBundle.getString("InsufficientFunds");
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void setPerk(PerkView perkView) {
    if (this.perkView != null) {
      this.perkView.unselect();
    }
    perkView.select();
    cost.setText(perkView.getCost());
    this.perkView = perkView;
    textArea.setText(perkView.getDescription());
    perkTitle.setText(perkView.getName());
    updateButton(perkView.isActive(), perkView.isAvailable());
  }

  private void onClick() {
    boolean previous = perkView.isActive();
    controller.activatePerk(perkView.getId());
    boolean activated = perkView.isActive();
    if (!previous && !activated) {
      Alert alert = new Alert(AlertType.INFORMATION, fundsMessage);
      alert.showAndWait();
    }
    updateButton(perkView.isActive(), perkView.isAvailable());
  }

  private void updateButton(boolean active, boolean available) {
    if (active) {
      button.setText(refundText);
    } else {
      button.setText(purchaseText);
    }
    button.setDisable(!available);
  }

  public BorderPane getBorderPane() {
    return borderPane;
  }
}

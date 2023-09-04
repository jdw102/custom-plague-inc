package ooga.view;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ooga.model.gamestate.UserPoints;
import ooga.util.Observer;

public class PerkPopUpView implements Observer {

  private static final String STYLESHEET = "/games/%s/stylesheets/PerkView.css";
  private final ViewFactory viewFactory;
  private final PerkTreeView perkTreeView;
  private final BorderPane root;
  private final PerkPurchaser perkPurchaser;
  private final Dialog dialog;
  private ProtagonistView myProtagonistView;
  private final Label pointsLabel;
  private UserPoints userPoints;
  private final ResourceBundle languageBundle;


  public PerkPopUpView(PerkTreeView perkTreeView,
      PerkPurchaser perkPurchaser, ResourceBundle languageBundle, String gameType) {
    this.languageBundle = languageBundle;
    dialog = new Dialog();
    dialog.setTitle(languageBundle.getString("PerkPopUpTitle"));
    dialog.getDialogPane().setId("PerkPopUp");
    this.perkTreeView = perkTreeView;
    viewFactory = new ViewFactory();
    root = new BorderPane();
    DialogPane dialogPane = new DialogPane();
    dialogPane.setContent(root);
    dialogPane.getStylesheets()
        .add(getClass().getResource(String.format(STYLESHEET, gameType)).toExternalForm());
    dialog.setDialogPane(dialogPane);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    pointsLabel = viewFactory.createLabel("", "PointsLabel");
    pointsLabel.getStyleClass().add("labels");
    this.perkPurchaser = perkPurchaser;
    root.setRight(perkPurchaser.getBorderPane());
  }

  public void setProtagonist(ProtagonistView protagonist) {
    this.myProtagonistView = protagonist;
    Label nameLabel = viewFactory.createLabel(myProtagonistView.getUserName(), "NameLabel");
    nameLabel.getStyleClass().add("labels");
    Label typeLabel = viewFactory.createLabel(myProtagonistView.getName(), "TypeLabel");
    typeLabel.getStyleClass().add("labels");
    HBox infoBox = makeInfoBox(nameLabel, typeLabel);
    root.setBottom(infoBox);
  }

  public void open() {
    dialog.show();
  }

  public void displayPerks() {
    root.setLeft(perkTreeView.getTabPane());
  }


  private HBox makeInfoBox(Label nameLabel, Label typeLabel) {
    HBox infoBox = new HBox();
    infoBox.getStyleClass().add("info-view");
    VBox progressBarBox = new VBox();
    progressBarBox.getChildren().addAll(myProtagonistView.getProgressBars().getChildren());
    progressBarBox.getStyleClass().add("progress-bar-box");
    ImageView imageView = myProtagonistView.getImage();
    imageView.setFitHeight(100);
    imageView.setFitWidth(100);
    VBox protagonistBox = new VBox(nameLabel, imageView, typeLabel, pointsLabel);
    protagonistBox.setAlignment(Pos.CENTER);
    protagonistBox.getStyleClass().add("protagonist-box");
    infoBox.getChildren().addAll(progressBarBox, protagonistBox);
    return infoBox;
  }

  @Override
  public void update() {
    pointsLabel.setText(String.format(languageBundle.getString("UserPoints"),
        NumberFormat.getNumberInstance(Locale.US).format((int) userPoints.getProgress())));
  }

  public void setUserPoints(UserPoints userPoints) {
    this.userPoints = userPoints;
    pointsLabel.setText(
        NumberFormat.getNumberInstance(Locale.US).format((int) userPoints.getProgress()));
  }

  public PerkPurchaser getPerkPurchaser() {
    return perkPurchaser;
  }

  public PerkTreeView getPerkTreeView() {
    return perkTreeView;
  }

}

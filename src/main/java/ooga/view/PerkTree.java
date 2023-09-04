package ooga.view;

import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class PerkTree {

  private final Pane pane;
  private final BorderPane borderPane;
  private final String name;
  private final double dist;
  private final double spacing;
  private final double width;


  public PerkTree(String name, ResourceBundle gameSettings) {
    this.name = name;
    pane = new Pane();
    borderPane = new BorderPane();
    pane.getStyleClass().add("perk-tree-borderpane");
    dist = Double.parseDouble(gameSettings.getString("PerkSize"));
    spacing = Double.parseDouble(gameSettings.getString("PerkTreeSpacing"));
    width = Double.parseDouble(gameSettings.getString("PerkTreeWidth"));
  }

  public void addPerks(double x, double y, PerkTreeNode perkTreeNode, boolean start, int toggle) {
    if (perkTreeNode.value() == null) {
      return;
    }
    if (start) {
      x = width / 2;
      y = dist;
    }
    PerkView perkView = perkTreeNode.value();
    perkView.makeButton(x, y);
    Button button = perkView.getButton().getButton();
    pane.getChildren().add(button);
    button.toFront();
    double additional = toggle * dist;
    if (perkTreeNode.left() != null) {
      addPerks(x - spacing + additional, y + 2 * dist, perkTreeNode.left(), false, toggle + 1);
    }
    if (perkTreeNode.right() != null) {
      addPerks(x + spacing - additional, y + 2 * dist, perkTreeNode.right(), false, toggle + 1);
    }
  }

  public String getName() {
    return name;
  }

  public BorderPane getBorderPane() {
    borderPane.setCenter(pane);
    BorderPane.setAlignment(pane, Pos.CENTER);
    return borderPane;
  }
}

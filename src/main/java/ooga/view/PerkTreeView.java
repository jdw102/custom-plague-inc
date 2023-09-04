package ooga.view;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

/**
 * @author Eka
 */

public class PerkTreeView {

  private final GridPane gridPane;
  private final TabPane tabPane;
  private final List<Tab> tabs;

  public PerkTreeView() {
    gridPane = new GridPane();
    tabPane = new TabPane();
    gridPane.getStyleClass().add("perk-tree-grid");
    tabs = new ArrayList<>();
  }

  public TabPane getTabPane() {
    return tabPane;
  }

  public void addPerkTree(PerkTree perkTree) {
    Tab tab = new Tab(perkTree.getName(), perkTree.getBorderPane());
    tab.setClosable(false);
    tabPane.getTabs().add(tab);
  }

}

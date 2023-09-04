package ooga.view;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ooga.model.gamestate.GameData;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

public class WorldDataViewTest extends DukeApplicationTest {
  private String language = "English";
  private String myGameType = "Plague";
  private ViewFactory viewUtils = new ViewFactory();
  private static final String LANGUAGE = "games.%s.properties.languages.%s";
  private WorldDataView worldDataView;
  private Dialog worldDataDialog;

  @Override
  public void start(Stage stage){
    ResourceBundle languageBundle = ResourceBundle.getBundle(
        String.format(LANGUAGE, myGameType, language));

    worldDataView = new WorldDataView(languageBundle, myGameType);
    worldDataView.setGameData(new GameData(0));


    BorderPane root = new BorderPane();
    Button worldDataButton = viewUtils.createButton("WorldData", "worldButton");
    worldDataButton.getStyleClass().add("worldButton");
    worldDataButton.setText("World Data");
    worldDataDialog = worldDataView.getDialog();//
    worldDataButton.setOnAction(event -> worldDataDialog.showAndWait());

    root.setCenter(worldDataButton);
    Scene scene = new Scene(root, 300, 300);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void hasWorldDataButton(){
    Button worldDataButton = lookup("#WorldData").query();
    assertEquals(worldDataButton.getText(), "World Data");
  }

  @Test
  public void hasCloseButton(){
    String closeButtonText = worldDataDialog.getDialogPane().getButtonTypes().get(0).getText();
    assertEquals(closeButtonText, "Close");
  }

  @Test
  public void hasChart(){
    ObservableList<Node> nodes = worldDataDialog.getDialogPane().getChildren();
    for(Node node : nodes){
      if(node.getClass() == BorderPane.class){
        String children = ((BorderPane) node).getChildren().toString();
        assertTrue(children.contains("AreaChart"));
      }
    }
  }

}

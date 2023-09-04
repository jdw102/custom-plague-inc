package ooga.view.map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import ooga.view.RegionInfoView;

public class MapView {

  private final String IMAGE_PATH = "/games/%s/map_backgrounds/%s";
  private final BorderPane mapPane;
  private final Map<Integer, RegionView> regionViewMap;
  private final BorderPane progressPane;
  private final ImageView background;
  private final Group node;
  private final ResourceBundle mapSettings;


  public MapView(String gameType, ResourceBundle gameSettings) {
    mapSettings = gameSettings;
    mapPane = new BorderPane();
    regionViewMap = new HashMap<>();
    progressPane = new BorderPane();
    double width = Double.parseDouble(mapSettings.getString("MapWidth"));
    double height = Double.parseDouble(mapSettings.getString("MapHeight"));
    InputStream stream;
    try {
      String name = String.format(IMAGE_PATH, gameType, mapSettings.getString("BackgroundImage"));
      stream = new FileInputStream(getClass().getResource(name).getPath());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    background = new ImageView(new Image(stream));
    background.setFitWidth(width);
    background.setFitHeight(height);
    BorderPane borderPane = new BorderPane();
    borderPane.setCenter(new Group(progressPane, mapPane));
    node = new Group(borderPane);
    borderPane.getStyleClass().add("map-node");
  }

  public void addRegionView(int id, RegionView regionView) {
    regionViewMap.put(id, regionView);
    mapPane.getChildren().add(regionView.getView());
  }

  public void setUpOnClick(RegionInfoView regionInfoView) {
    for (RegionView regionView : regionViewMap.values()) {
      regionView.addObserver(regionInfoView);
      regionView.setOnAction(regionInfoView);
    }
  }

  public RegionView getRegionView(int id) {
    return regionViewMap.get(id);
  }

  /**
   * Creates the Map view by populating a BorderPane with each region shape.
   *
   * @return BorderPane with all shapes.
   */
  public BorderPane getMapPane() {
    return this.mapPane;
  }

  public Group getGroup() {
    return node;
  }

}

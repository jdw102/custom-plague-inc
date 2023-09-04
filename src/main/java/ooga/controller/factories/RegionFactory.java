package ooga.controller.factories;


import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import ooga.controller.parsers.paths.PortSymbolMap;
import ooga.controller.parsers.paths.RegionPortCoordinateMap;
import ooga.controller.parsers.regions.RegionPathRecord;
import ooga.controller.parsers.regions.RegionRecord;
import ooga.controller.parsers.regions.RegionsData;
import ooga.model.gamestate.GameState;
import ooga.model.region.Census;
import ooga.model.region.PathActivityMap;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.util.Coordinate;
import ooga.view.DisplayView;
import ooga.view.map.MapReader;
import ooga.view.map.RegionPortRecord;
import ooga.view.map.RegionView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// ** in the future i would love to put this within regions JSON--we only need to make intermediate records/data classes for stuff that is a dependency for other stuff/stuff that doesn't have all its vital information yet. but as of now, am sticking with an independent factory

/**
 * RegionFactory class that takes RegionData and creates each RegionModel
 */
public class RegionFactory {

  private static final Logger LOG = LogManager.getLogger(RegionView.class);
  //depends on
  private final RegionsData regionData;
  private final String gameType;
  private final RegionPortCoordinateMap regionPortCoordinateMap;


  /**
   * Constructor, takes RegionData tha has been parsed and validated
   *
   * @param regionJSONData
   */
  public RegionFactory(RegionsData regionJSONData, String gameType) {
    regionData = regionJSONData;
    this.gameType = gameType;
    regionPortCoordinateMap = new RegionPortCoordinateMap();
  }

  /**
   * Method called to activate factory. Hoping to make a factory abstraction and move this method
   * there.
   *
   * @return
   */
  public RegionMap make(DisplayView displayView, MapReader mapReader, PortSymbolMap portSymbolMap,
      Census startCensus, GameState gameState) {
//    System.out.println("REGION FACTORY MAKE CALLED");
//    System.out.println("REGIONDATA RECORDS:");
//    System.out.println(regionData.getformattedString());
    RegionMap regionMap = new RegionMap(startCensus);
    Map<Integer, Shape> backgroundShapeMap = mapReader.getRegionShapeMap();
    Map<Integer, Shape> regionShapeMap = mapReader.getRegionShapeMap();
    for (RegionRecord r : regionData.getValidRegions()) {
      RegionModel rm = makeRegionModel(r);
      int id = r.id();
      regionMap.addRegion(rm);
//      System.out.println(String.format("making region %d, color = %s", id, r.color()));
      Color color = Color.valueOf(r.color());
      RegionView regionView = new RegionView(id, r.name(), r.description(),
          regionShapeMap.get(id), backgroundShapeMap.get(id), color, rm,
          displayView.getSettingsBundle(), displayView.getLanguageBundle(), gameState);
      gameState.addObserver(regionView);
      rm.addObserver(regionView);
      if (id != 0) {
        addRegionPorts(id, regionView, mapReader.getRegionPorts(id), portSymbolMap,
            mapReader.getRectangleWidth(),
            mapReader.getRectangleHeight());
      }
      attachFactors(r.factors(), rm, regionView);
      rm.addObserver(regionView);
      displayView.addRegionView(id, regionView);
    }
    return regionMap;
  }

  private void addRegionPorts(int id, RegionView regionView, List<RegionPortRecord> portRecords,
      PortSymbolMap portSymbolMap, double width, double height) {
    for (RegionPortRecord regionPortRecord : portRecords) {
//      System.out.println(String.format("symbol = %s, x = %s, y = %s", regionPortRecord.portSymbol(), regionPortRecord.x(), regionPortRecord.y()));
      String symbol = regionPortRecord.portSymbol();
      List<String> type = portSymbolMap.getType(symbol);
      for (String t : type) {
        regionPortCoordinateMap.addPortCoordinate(id, t,
            new Coordinate(regionPortRecord.x(), regionPortRecord.y()));
      }
      ImageView portImage = createPortImage(portSymbolMap, regionPortRecord, width, height);
      regionView.addPort(portImage);
    }
  }

  private ImageView createPortImage(PortSymbolMap portSymbolMap, RegionPortRecord portRecord,
      double width, double height) {
    LOG.debug("Port symbol {}", portRecord.portSymbol());
    String path = String.format("games/%s/%s", gameType,
        portSymbolMap.getImagePath(portRecord.portSymbol()));
    // load the graphic using resources
    InputStream iconPath = RegionView.class.getClassLoader().getResourceAsStream(path);

    // set icon graphic appearances
    if (iconPath == null) {
      LOG.debug(portSymbolMap.getImagePath(portRecord.portSymbol()));
      throw new NullPointerException("Invalid icon path");
    }

    ImageView portIcon = new ImageView(new Image(iconPath));
    portIcon.setFitWidth(width);
    portIcon.setFitHeight(height);
    portIcon.setLayoutX(portRecord.x());
    portIcon.setLayoutY(portRecord.y());
    return portIcon;
  }

  private void attachFactors(Map<String, String> factorMap, RegionModel regionModel,
      RegionView regionView) {
    for (String key : factorMap.keySet()) {
      regionModel.addFactor(key, factorMap.get(key));
      regionView.addFactor(key, factorMap.get(key));
    }
  }


  /**
   * Method to create single RegionModel from a single RegionRecord
   *
   * @param r
   * @return
   */
  public RegionModel makeRegionModel(RegionRecord r) {
    PathActivityMap pa = makePathActivity(r);
    RegionState rs = makeRegionState(r);
    return new RegionModel(r.id(), rs, pa, r.closedThreshold(), r.detectionThreshold(),
        r.baseAntagonistRate());
  }

  /**
   * Makes PathActivity class for given Region, needed to construct RegionModel
   *
   * @param r
   * @return
   */
  private PathActivityMap makePathActivity(RegionRecord r) {
    //create pathactivity for this region AND create path objects and stick them in regionpaths
    PathActivityMap p = new PathActivityMap();
    for (RegionPathRecord pr : r.paths()) {
      p.addPath(pr.pathRecord().name(), pr.activity());
    }
    return p;
  }

  /**
   * Makes Census class for given Region, needed to construct RegionStateModel
   *
   * @param r
   * @return
   */
  private Census makeCensus(RegionRecord r) {
    Census c = new Census();
    c.addPopulation(regionData.getDefaultPop(), r.startingPopulation());
    for (String otherPops : regionData.getOtherSubpops()) {
      c.addPopulation(otherPops, 0);
    }
    return c;
  }

  /**
   * Makes RegionState class for given Region, needed to construct RegionModel
   *
   * @param r
   * @return
   */
  private RegionState makeRegionState(RegionRecord r) {
    Census c = makeCensus(r);
    return new RegionState(c, true, false);
  }

  public RegionPortCoordinateMap getRegionPortCoordinateMap() {
    return regionPortCoordinateMap;
  }
}

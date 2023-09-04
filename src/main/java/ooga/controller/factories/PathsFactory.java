package ooga.controller.factories;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ooga.controller.parsers.paths.PathRecord;
import ooga.controller.parsers.paths.RegionPortCoordinateMap;
import ooga.controller.parsers.paths.SubPopSpreadRecord;
import ooga.controller.parsers.regions.RegionPathRecord;
import ooga.controller.parsers.regions.RegionRecord;
import ooga.controller.parsers.regions.RegionsData;
import ooga.model.region.PathModel;
import ooga.model.region.PathPointsMap;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionPaths;
import ooga.util.Coordinate;
import ooga.view.DisplayView;
import ooga.view.PathView;
import ooga.view.map.MapReader;
import ooga.view.map.RegionView;

/**
 * Class that creates PathModels based on data parsed from JSON.
 */
public class PathsFactory {

  private final double PATH_IMAGE_RATIO = 0.5;
  //depends on
  private final RegionsData regionsData;
  //creates
  private final RegionPaths regionPaths;
  private final Map<String, PathPointsMap> pathPointsMap;
  private final RegionPortCoordinateMap regionPortCoordinateMap;
  private final String gameType;

  /**
   * Constructor that takes in necessary data classes the specific region paths depend on.
   *
   * @param regionsData
   */
  public PathsFactory(RegionsData regionsData,
      RegionPortCoordinateMap regionPortCoordinateMap, String gameType) {
    this.regionsData = regionsData;
    this.regionPaths = new RegionPaths();
    pathPointsMap = new HashMap<>();
    this.regionPortCoordinateMap = regionPortCoordinateMap;
    this.gameType = gameType;
  }

  /**
   * General method to make ReigonPaths class and all PathModels within it
   *
   * @return
   */

  //rethink as i figure out abstraction for factories
  public RegionPaths make(MapReader mapReader, DisplayView displayView, RegionMap regionMap) {
    double imageWidth = mapReader.getRectangleWidth();
    double imageHeight = mapReader.getRectangleHeight();
    for (RegionRecord r : regionsData.getValidRegions()) {
      RegionModel origin = regionMap.getRegionModelByID(
          r.id());  //add try catch for null pointer? shld never happen due to validation in RegionsJSON
      makeAdjacentPathModels(r, mapReader, regionMap, origin);
      for (RegionPathRecord rpr : r.paths()) {
        addAllPaths(
            makePathModels(origin, rpr, imageWidth, imageHeight, displayView,
                regionMap));
      }
    }
    return regionPaths;
  }

  private void makeAdjacentPathModels(RegionRecord record, MapReader mapReader, RegionMap regionMap,
      RegionModel origin) {
    Iterator<Integer> iterator = mapReader.getNeighborIterator(record.id());
    RegionPathRecord pathRecord = null;
    try {
      pathRecord = record.getRegionPathRecord(0);
    } catch (NullPointerException e) {
      return;
    }
    while (iterator.hasNext()) {
      RegionModel destination = regionMap.getRegionModelByID(iterator.next());
      PathModel pathModel = new PathModel(origin, destination, pathRecord.pathRecord().name(),
          createAdjacentPathPointsMap(pathRecord.pathRecord()),
          pathRecord.pathRecord().peopleSpread());
      regionPaths.addPath(pathModel);
    }
  }

  private PathPointsMap createAdjacentPathPointsMap(PathRecord record) {
    PathPointsMap pointsMap = new PathPointsMap();
    List<SubPopSpreadRecord> subPopSpreadRecords = record.subpopSpread();
    for (SubPopSpreadRecord subPopSpreadRecord : subPopSpreadRecords) {
      pointsMap.addPathPoints(subPopSpreadRecord.popName(), subPopSpreadRecord.pointsWorth());
    }
    return pointsMap;
  }

  /**
   * Helper method to make pathmodels for a given RegionPathRecord
   *
   * @param origin originating RegionModel, already found
   * @param rpr    RegionPathRecord containing information about all paths of this type that
   *               originate from this  region
   * @return
   */
  private List<PathModel> makePathModels(RegionModel origin, RegionPathRecord rpr,
      double imageWidth, double imageHeight, DisplayView displayView,
      RegionMap regionMap) {
    List<PathModel> lst = new ArrayList<>();
    for (int id : rpr.connections()) {
      RegionModel target = regionMap.getRegionModelByID(
          id);
      String type = rpr.pathRecord().name();
      PathPointsMap pathPointsMap = makePathPoints(rpr.pathRecord());
      int totalAmount = rpr.pathRecord().peopleSpread();
      PathModel pathModel = new PathModel(origin, target, type, pathPointsMap, totalAmount);
      if (rpr.pathRecord().id() != 0) {
        ImageView image = makePathImage(rpr.pathRecord().imgPath(), imageWidth, imageHeight);
        PathView pathView = makePathView(pathModel, rpr.pathRecord().animationType(), image, origin,
            target, displayView.getMapRoot(), displayView.getSettingsBundle());
        displayView.addPathView(pathView);
        pathModel.addObserver(pathView);
      }
      lst.add(pathModel);
    }
    return lst;
  }

  private ImageView makePathImage(String imagePath, double imageWidth, double imageHeight) {
    String path = String.format("games/%s/%s", gameType, imagePath);
    // load the graphic using resources
    InputStream iconPath = RegionView.class.getClassLoader().getResourceAsStream(path);
    ImageView imageView = new ImageView(new Image(iconPath));
    imageView.setFitWidth(imageWidth * PATH_IMAGE_RATIO);
    imageView.setFitHeight(imageHeight * PATH_IMAGE_RATIO);
    imageView.setVisible(false);
    return imageView;
  }

  private PathView makePathView(PathModel pathModel, String viewType, ImageView image,
      RegionModel origin,
      RegionModel destination, Group root, ResourceBundle gameSettings) {
    int originId = origin.getId();
    int destinationId = destination.getId();
    String type = pathModel.getType();
    Coordinate originCoordinate = regionPortCoordinateMap.getPortCoordinate(originId, type);
    Coordinate destinationCoordinate = regionPortCoordinateMap.getPortCoordinate(destinationId,
        type);
//    System.out.println(String.format("originid =  %d, destinationid = %d, type = %s", originId, destinationId, type));
//    System.out.println(String.format("originid =  %d, destinationid = %d, type = %s, origincoord = %s %s, destcoord = %s %s", originId, destinationId, type, originCoordinate.x(), originCoordinate.y(), destinationCoordinate.x(), destinationCoordinate.x()));
    Class<?> clazz;
    try {
      clazz = Class.forName(String.format("ooga.view.%sPathView", viewType));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    PathView pathView;
    try {
      pathView = (PathView) clazz.getDeclaredConstructor(Coordinate.class, Coordinate.class,
              ImageView.class, PathModel.class, Group.class, ResourceBundle.class)
          .newInstance(originCoordinate, destinationCoordinate, image, pathModel, root,
              gameSettings);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return pathView;
  }

  /**
   * Helper method to create PathPoints class for each Path
   *
   * @param pr
   * @return
   */
  private PathPointsMap makePathPoints(PathRecord pr) {
    if (pathPointsMap.containsKey(pr.name())) {
      return pathPointsMap.get(pr.name());
    }
    //TODO rethink this once new subpop population logic is set up, where it only spreads default or target pop
    PathPointsMap points = new PathPointsMap();
    for (SubPopSpreadRecord s : pr.subpopSpread()) {
      points.addPathPoints(s.popName(), s.pointsWorth());
    }
    pathPointsMap.put(pr.name(), points);
    return points;
  }

  /**
   * Adds every single path model in given list to the overall regionPaths path holder
   *
   * @param pms
   */
  private void addAllPaths(List<PathModel> pms) {
    for (PathModel pm : pms) {
      regionPaths.addPath(pm);
    }
  }


}

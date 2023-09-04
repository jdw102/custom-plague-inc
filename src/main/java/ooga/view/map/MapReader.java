package ooga.view.map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapReader {

  public static final String SETTINGS_PATH = "games.%s.properties.Settings";
  private static final Logger LOG = LogManager.getLogger(MapReader.class);
  private final int MARGIN = 10;
  private final String FILE_PATH = "/games/%s/map.csv";
  private final File mapFile;
  private int mapWidth;
  private int mapHeight;
  private List<List<String>> csvData;
  private List<List<Integer>> regionIDData;
  private List<Integer> regionsList;
  private Map<Integer, List<RegionPortRecord>> regionPortMap;
  private Map<Integer, List<Integer>> regionNeighbors;
  private int rowLength;
  private int colLength;
  private double rectangleWidth;
  private double rectangleHeight;
  private ResourceBundle mapSettings;


  public MapReader(String gameType) {
    mapFile = new File(getClass().getResource(String.format(FILE_PATH, gameType)).getPath());
    mapSettings = ResourceBundle.getBundle(String.format(SETTINGS_PATH, gameType));
    mapWidth = Integer.parseInt(mapSettings.getString("MapWidth"));
    mapHeight = Integer.parseInt(mapSettings.getString("MapHeight"));
    readAllCSVData();
    populateRegionIDData();
    populateRegionsList();
    findNeighbors();
    getPortData();
  }

  public MapReader(File file, int mapWidth, int mapHeight) {
//  public MapReader(File file) {
    mapFile = file;
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    readAllCSVData();
    populateRegionIDData();
    populateRegionsList();
    findNeighbors();
    getPortData();
  }


  public Map<Integer, Shape> getRegionShapeMap() {
    return generateRegionShapes();
  }

  public List<RegionPortRecord> getRegionPorts(int regionID) {
    return regionPortMap.get(regionID);
  }

  public Map<Integer, List<Integer>> getRegionNeighbors() {
    return regionNeighbors;
  }

  public Iterator<Integer> getNeighborIterator(int id) {
    return regionNeighbors.get(id).iterator();
  }


  /**
   * Obtains the neighbors of each region by iterating over each cell and recording the neighbors
   * encountered.
   */
  private void findNeighbors() {
    assert regionsList != null;

    // initialize neighbor management data structure
    regionNeighbors = new HashMap<>();
    for (int regionID : regionsList) {
      regionNeighbors.put(regionID, new ArrayList<>());
    }

    // find neighboring regions of each cell
    for (int y = 0; y < regionIDData.size(); y++) {
      for (int x = 0; x < regionIDData.get(y).size(); x++) {
        int regionID = regionIDData.get(y).get(x);
        regionNeighbors.get(regionID)
            .addAll(getAdjacentRegionCells(x, y)); // keep track of each cell's neighbors
      }
    }

    // Remove all duplicate neighbors collected in each region's running list of detected neighbors
    for (int regionID : regionNeighbors.keySet()) {
      Set<Integer> regionSet = new HashSet<>(
          regionNeighbors.get(regionID));  // set removes duplicates
      regionSet.removeIf(num -> num == 0 || num == regionID);     // remove "ocean" region, and self
      regionNeighbors.put(regionID,
          new ArrayList<>(regionSet));  // replace list with non-duplicated regions.
    }
  }

  /**
   * Gets a list of a cell's neighboring regions.
   *
   * @param x position of the region cell
   * @param y position of the region cell
   * @return a list of a cell's neighboring regions (excluding the ocean)
   */
  private List<Integer> getAdjacentRegionCells(int x, int y) {
    List<Integer> neighbors = new ArrayList<>();

    // record the neighbors of the cell.
    if (y > 0) {
      neighbors.add(regionIDData.get(y - 1).get(x));
    }
    if (x > 0) {
      neighbors.add(regionIDData.get(y).get(x - 1));
    }
    if (y < regionIDData.size() - 1) {
      neighbors.add(regionIDData.get(y + 1).get(x));
    }
    if (x < regionIDData.get(y).size() - 1) {
      neighbors.add(regionIDData.get(y).get(x + 1));
    }

    return neighbors;
  }

  /**
   * Generates a single Shape object for each region's view, and stores it in a Map that corresponds
   * to its region ID.
   *
   * @return a Map with each region's Shape
   */
  private Map<Integer, Shape> generateRegionShapes() {
    Map<Integer, List<Rectangle>> regionRectanglesMap = this.initializeRegionShapes();
    Map<Integer, Shape> regionShapesMap = new HashMap<>();
    for (int regionID : regionRectanglesMap.keySet()) {
      Shape regionShape = mergeRectangles(regionRectanglesMap.get(regionID));
      regionShapesMap.put(regionID, regionShape);
    }

    return regionShapesMap;
  }

  /**
   * Merges a list of rectangles into one Shape object
   *
   * @param regionRectangles a list of rectangles
   * @return a Shape object of the merged rectangles.
   */
  private Shape mergeRectangles(List<Rectangle> regionRectangles) {
    // initialize the region's Shape by merging the first rectangle with itself
    Rectangle firstShape = regionRectangles.get(0);
    Shape regionShape = Shape.union(firstShape, firstShape);
    for (Rectangle rectangle : regionRectangles) {
      regionShape = Shape.union(regionShape, rectangle);  // merge shape with remaining rectangles
    }
    regionShape.setSmooth(true);
    return regionShape;
  }

  /**
   * Initializes a map that contains a list of rectangles that make up each region's view. These
   * rectangles are not in a union.
   *
   * @return Hashmap containing each region's seperated rectangle objects.
   */
  private Map<Integer, List<Rectangle>> initializeRegionShapes() {
    int x;
    int y = 0;

    Map<Integer, List<Rectangle>> regionRectangles = new HashMap<>();

    for (int regionID : this.regionsList) {
      regionRectangles.put(regionID, new ArrayList<>());  // initialize hashmap arraylists
    }
//    regionRectangles.remove(0); // 0 is the "ocean" and NOT a region.
    double xMargin = (1.0 * MARGIN) / colLength;
    double yMargin = (1.0 * MARGIN) / rowLength;
    for (List<Integer> row : this.regionIDData) {
      x = 0;
      for (Integer cell : row) {
        List<Rectangle> rectanglesList = regionRectangles.get(cell);
        rectanglesList.add(
            new Rectangle(x * rectangleWidth, y * rectangleHeight + yMargin, // generate rectangle!
                rectangleWidth, rectangleHeight));
        x++;
      }
      y++;
    }

    return regionRectangles;
  }

  /**
   * Extracts all occurrences of special characters, which represent paths, while keeping them in
   * their corresponding position in the original CSV data list
   */
  private void getPortData() {
    regionPortMap = new HashMap<>();
    for (int regionID : this.regionsList) {
      regionPortMap.put(regionID, new ArrayList<>()); // generate key set and empty list
    }

    int y = 0;
    int x;
    for (List<String> row : this.csvData) {
      x = 0;
      for (String cell : row) {
        cell = cell.replaceAll("\\d", "");  // strip the cell's region ID
        if (!cell.equals("")) {
//          if(!viewProperties.getPortKeys().contains(cell)){
//            LOG.warn("Ignoring unkown port key \"{}\" at location ({}, {}).", cell, x, y);  // TODO: determine if we want to throw an exception...
//            break;
//          }

          int regionID = regionIDData.get(y).get(x);              // get region ID from 2D list
          List<RegionPortRecord> regionPortsList = regionPortMap.get(
              regionID); // obtain region's list
          regionPortsList.add(new RegionPortRecord(cell, x * rectangleWidth,
              y * rectangleHeight));  // create a new record
        }
        x++;
      }
      y++;
    }
  }

  /**
   * @return list of all the identified regions within the map file.
   */
  private void populateRegionsList() {
    Set<Integer> regionIDSet = new HashSet<>();

    for (List<Integer> row : regionIDData) {
      regionIDSet.addAll(row);
    }
    this.regionsList = regionIDSet.stream().toList();
  }

  /**
   * Combs through the parsed CSV data by removing any special characters and keeping only the
   * region IDs.
   */
  private void populateRegionIDData() {
    assert csvData != null;
    regionIDData = new ArrayList<>();

    int rowIndex = 0;
    for (List<String> row : this.csvData) {
      regionIDData.add(new ArrayList<>());
      for (String cell : row) {
        cell = cell.replaceAll("[^a-zA-Z0-9]", "");  // remove special chars

        try {
          int cellRegionID = Integer.parseInt(cell);
          regionIDData.get(rowIndex).add(cellRegionID); // parse the region ID
        } catch (NumberFormatException e) {
          regionIDData.get(rowIndex).add(0);            // default to "0" if parsing fails.
          LOG.warn(
              "Invalid region ID found in row index {}. Defaulting this region's ID to 0. Consider checking your map's data file located at {}",
              rowIndex, mapFile.toString());
        }

      }
      rowIndex++;
    }
  }

  public List<Integer> getRegionIDS() {
    List<Integer> lst = new ArrayList<>(this.regionsList);
    if (lst.contains(0)) {
      lst.remove(0);
    }
    return lst;
  }

  /**
   * Reads in all CSV data and stores it in a local 2D List of Strings. This ensures that the CSV
   * file only needs to be read once.
   */
  private void readAllCSVData() {
    this.csvData = new ArrayList<>();

    try {
      FileReader fileReader = new FileReader(this.mapFile);
      CSVReader csvReader = new CSVReader(fileReader);
      String[] firstRow = csvReader.readNext();

      List<String[]> parsedData = csvReader.readAll();

      int expectedColumnSize = parsedData.get(0).length;

      int rowIndex = 0;
      for (String[] row : parsedData) {       // iterate through each element (String)
        csvData.add(new ArrayList<>());     // of the CSV and store it in a 2D list

        if (row.length != expectedColumnSize) {
          throw new MapReaderException(String.format("Malformed map data CSV file. Expected %d "
              + "columns in row index %d but got %d.", expectedColumnSize, rowIndex, row.length));
        }

        for (String cell : row) {
          cell = cell.replaceAll(" ", "");
          csvData.get(rowIndex).add(cell);
        }
        rowIndex++;
      }
//      rowLength = 25;
//      colLength = 25;
      rowLength = csvData.size();
      colLength = csvData.get(0).size();
      rectangleHeight = mapHeight / rowLength;
      rectangleWidth = mapWidth / colLength;
    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }

    LOG.debug("CSV Data read in: {}", csvData.toString());
  }

  public double getRectangleWidth() {
    return rectangleWidth;
  }

  public double getRectangleHeight() {
    return rectangleHeight;
  }
}
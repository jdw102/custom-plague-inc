package ooga.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import ooga.view.map.MapReader;
import ooga.view.map.MapReaderException;
import ooga.view.map.RegionPortRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import util.DukeApplicationTest;
import static org.junit.jupiter.api.Assertions.*;

public class MapReaderTest extends DukeApplicationTest {
  private static final Logger LOG = LogManager.getLogger(MapReaderTest.class);

  private final String TEST_PATH = "ooga/view/testmap.csv";
  private final String BIG_MAP = "ooga/view/bigmap.csv";
  private final String SETTINGS =  "ooga/view/Settings.properties";
  private final String CENTRAL_AMERICA_MAP_PATH = "ooga/view/central_america_map.csv";
  private final String TEST_BADMAP_PATH = "ooga/view/test_bad_but_valid_map.csv";
  private final String TEST_MALFORMED_MAP_PATH =  "ooga/view/test_malformed_map.csv";


  private MapReader mapReader;
  private Map<Integer, Shape> regionShapeMap;
  private Map<Integer, List<Integer>> regionNeighbors;


//  private Executable initTestExecutable(String testPath, String ){
//    URL mapURL = MapReader.class.getClassLoader().getResource(testPath);
//    File mapFile = new File(mapURL.getPath());
//    mapReader = new MapReader(mapFile);
//    regionShapeMap = mapReader.getRegionShapeMap();
//    regionNeighbors = mapReader.getRegionNeighbors();
//    return null;
//  }

  @Test
  void testBigMap() {
    initTest(BIG_MAP);
    assertEquals(800/77, mapReader.getRectangleHeight());
    assertEquals(1400/82, mapReader.getRectangleWidth());
  }

  private void initTest(String testPath){
    URL mapURL = MapReader.class.getClassLoader().getResource(testPath);
    File mapFile = new File(mapURL.getPath());
    mapReader = new MapReader(mapFile, 1400, 800);
    regionShapeMap = mapReader.getRegionShapeMap();
    regionNeighbors = mapReader.getRegionNeighbors();
  }

  @Test
  public void mapReaderFileInputTest(){
    initTest(TEST_PATH);
  }

  private void testRegionShapeView(Stage stage){
    initTest(TEST_PATH);
    Map<Integer, Shape> regionShapeMap = mapReader.getRegionShapeMap();

    stage.setTitle("Region Shape View Test");
    BorderPane borderPane = new BorderPane();
    for(int regionID : regionShapeMap.keySet()){
      borderPane.getChildren().add(regionShapeMap.get(regionID));
    }

    Scene scene = new Scene(borderPane, 600, 600);
    stage.setScene(scene);
  }



  @Test
  public void testBadInputMapData(){
    initTest(TEST_BADMAP_PATH);

    // parsed values will still be good, even if formatted improperly
    // bad values will be ignored.
    List<Integer> expectedRegionIDList = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5));
    List<Integer> actualRegionIDList =  mapReader.getRegionNeighbors().keySet().stream().toList();
    assertEquals(expectedRegionIDList,actualRegionIDList);


    // neighbors work too!
    Map<Integer, List<Integer>> expectedNeighborsMap = new HashMap<>();
    expectedNeighborsMap.put(1, List.of(2));
    expectedNeighborsMap.put(2, List.of(1, 3));
    expectedNeighborsMap.put(3, List.of(2, 4));
    expectedNeighborsMap.put(4, List.of(3, 5));
    expectedNeighborsMap.put(5, List.of(4));
    mapReader.getRegionNeighbors().remove(0);
    assertEquals(expectedNeighborsMap, mapReader.getRegionNeighbors());


    Map<Integer, List<String>> expectedPortSymbols = new HashMap<>();
    expectedPortSymbols.put(1, null);
    expectedPortSymbols.put(2, List.of("*"));
    expectedPortSymbols.put(3, null);
    expectedPortSymbols.put(4, List.of("^"));
    expectedPortSymbols.put(5, null);

    for(int regionID : mapReader.getRegionNeighbors().keySet()){
      List<RegionPortRecord> regionPortRecordsList = mapReader.getRegionPorts(regionID);
      List<String> regionPortSymbols = new ArrayList<>();
      for(RegionPortRecord r : regionPortRecordsList){
        regionPortSymbols.add(r.portSymbol());
      }

      LOG.debug("Region {} ports: {}", regionID, regionPortRecordsList);
      if(expectedPortSymbols.get(regionID) != null){
        assertEquals(expectedPortSymbols.get(regionID), regionPortSymbols);
      }
    }
  }

  @Test
  public void testMalformedMapException(){
    MapReaderException thrown = assertThrows(MapReaderException.class,
        () -> initTest(TEST_MALFORMED_MAP_PATH),
        "Malformed map data CSV file. Expected 5 columns in row 1 but got 4.");

    assertTrue(thrown.getMessage().contains("Malformed map data CSV file."));
  }

  @Test
  public void testRegionNeighborsDetection(){
    initTest(TEST_PATH);
    System.out.println(mapReader.getRegionNeighbors());
  }

  @Override
  public void start(Stage stage) {
    testRegionShapeView(stage);
    stage.show();
    sleep(1000);
  }


  /*
  Neighbor testing
   */
  /**
   * Validates the output of the neighbor-finding functionality.
   */
  @Test
  public void testNeighborFinding(){
    initTest(TEST_PATH);
    // Checks if all region IDs have an entry in the neighbors map
    List<Integer> regionIDs = mapReader.getRegionNeighbors().keySet().stream().toList();
    assertEquals(regionIDs, regionNeighbors.keySet().stream().toList());

    for(int regionID : regionNeighbors.keySet()){
      assertFalse(regionNeighbors.get(regionID).contains(0));         // Checks if region 0, the "ocean", was properly omitted
      assertFalse(regionNeighbors.get(regionID).contains(regionID));  // Checks that each region does not contain itself as its neighbor
    }
  }

  /**
   * Checks that reported neighbors match the expected results.
   */
  @Test
  public void testNeighborFindingResults(){
    initTest(TEST_PATH);
    Map<Integer, List<Integer>> europeanNeighborsMap = new HashMap<>();
    europeanNeighborsMap.put(1, List.of());
    europeanNeighborsMap.put(2, List.of(3, 4, 5));
    europeanNeighborsMap.put(3, List.of(2, 5));
    europeanNeighborsMap.put(4, List.of(2));
    europeanNeighborsMap.put(5, List.of(2, 3));

    regionNeighbors.remove(0);  // remove ocean
    assertEquals(europeanNeighborsMap, regionNeighbors);

    initTest(CENTRAL_AMERICA_MAP_PATH);
    Map<Integer, List<Integer>> centralAmericaNeighborsMap = new HashMap<>();
    centralAmericaNeighborsMap.put(1, List.of(2, 3));
    centralAmericaNeighborsMap.put(2, List.of(1, 3, 4, 5));
    centralAmericaNeighborsMap.put(3, List.of(1, 2));
    centralAmericaNeighborsMap.put(4, List.of(2, 5));
    centralAmericaNeighborsMap.put(5, List.of(2, 4, 6));
    centralAmericaNeighborsMap.put(6, List.of(5, 7));
    centralAmericaNeighborsMap.put(7, List.of(6, 8));
    centralAmericaNeighborsMap.put(8, List.of(7, 9));
    centralAmericaNeighborsMap.put(9, List.of(8, 10));
    centralAmericaNeighborsMap.put(10, List.of(9, 11));
    centralAmericaNeighborsMap.put(11, List.of(10));
    centralAmericaNeighborsMap.put(12, List.of());
    centralAmericaNeighborsMap.put(13, List.of(14));
    centralAmericaNeighborsMap.put(14, List.of(13));
    centralAmericaNeighborsMap.put(15, List.of());
    centralAmericaNeighborsMap.put(16, List.of());

    regionNeighbors.remove(0);  // remove ocean
    assertEquals(centralAmericaNeighborsMap, regionNeighbors);
  }
}

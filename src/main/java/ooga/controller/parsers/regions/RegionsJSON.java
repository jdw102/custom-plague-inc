package ooga.controller.parsers.regions;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.math.SubPopulationData;
import ooga.controller.parsers.paths.PathData;
import ooga.controller.parsers.paths.PathRecord;
import ooga.controller.parsers.region_factors.RegionFactorData;
import ooga.controller.parsers.region_factors.RegionFactorRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parses all region data, which contains a region's ID, name, description, list of paths, factors,
 * population, thresholds, and other appearance information.
 */
public class RegionsJSON extends DataFileParser {

  public static final double DEFAULT_ANTAGONIST_BASERATE = 1;
  public static final double DEFAULT_DETECTION_THRESHOLD = 0.5;
  public static final double DEFAULT_CLOSE_THRESHOLD = 0.25;
  public static final int DEFAULT_STARTING_POPULATION = 100000;
  public static final double DEFAULT_PATH_ACTIVITY = 0.05;
  private static final Logger LOG = LogManager.getLogger(RegionsJSON.class);
  // parsed data for parsing verification.
  private final RegionFactorData validRegionFactorData;
  private final SubPopulationData validSubpopulationData;
  private final PathData validPathData;
  private final List<Integer> validRegionIDs;
  //internal parsed data for verification
  private final Set<Integer> idsAssociatedWithARegion;
  private final Map<Integer, Set<Integer>> regionsAllowingEachTypeOfPath;

  // data
  private RegionsData validRegionsData;

  /**
   * Instantiates Regions parser
   *
   * @param jsonNode               Top-most JSON node
   * @param validRegionFactorData  RegionsFactorData that contains valid parsed data
   * @param validSubpopulationData valid parsed suppopulation data
   * @param validPathData          valid parsed path data
   * @param validRegionIDsFromMap  list of valid region IDS from the map  reader
   */
  public RegionsJSON(JsonNode jsonNode, RegionFactorData validRegionFactorData,
      SubPopulationData validSubpopulationData,
      PathData validPathData, List<Integer> validRegionIDsFromMap) {
    super(jsonNode);
    this.validRegionFactorData = validRegionFactorData;
    this.validSubpopulationData = validSubpopulationData;
    this.validPathData = validPathData;
    this.validRegionIDs = validRegionIDsFromMap;
    idsAssociatedWithARegion = new HashSet<>();
    regionsAllowingEachTypeOfPath = setupPathMapChecker();
  }

  /**
   * Main method that executes the complete data parsing of a JSON file.
   */
  @Override
  public void parseAllData() throws ConfigJSONException {
    try {
      JsonNode regionsNode = getTopNode().get("regions");
      setTopNode(
          regionsNode);  //want to save specific regionFactorsNode as top node (ie, drop any _comments entries)
      this.collectModelData();
    } catch (NullPointerException e) {
      throw new ConfigJSONException(
          "Provided Region JSON does not contain a \"regions\" node");
    }
  }

  /**
   * Parses, collects, and stores ALL expected region data int0 a list, and then creates a
   * RegionsData object.
   */
  private void collectModelData() {
    JsonNode regions = this.topNode;

    List<RegionRecord> regionsDataList = new ArrayList<>();
    try {
      for (JsonNode regionEntryNode : regions) {
        try {
          RegionRecord record = createRegionRecord(regionEntryNode);
          idsAssociatedWithARegion.add(record.id());
          regionsDataList.add(record);
        } catch (ConfigJSONException j) {
          LOG.warn(j.getMessage());
        }
      }
    } catch (NullPointerException e) {
      LOG.warn(
          "Unable to parse data in regions.json, will create dummy region objects from Map IDs with default values");
    }
    regionsDataList.addAll(addMissingRegions());
    this.validRegionsData = new RegionsData(doPathValidation(regionsDataList),
        validSubpopulationData.getDefaultPopulation(),
        validSubpopulationData.getValidSubPops());
  }

  /**
   * Creates a single Region record, based off a single entry in the list of regions.
   *
   * @param regionNode Specific json element that contains the region's info
   * @return a parsed Region record
   */
  private RegionRecord createRegionRecord(JsonNode regionNode) {
    int regionID = getRegionID(regionNode);
    String regionName = ParserUtils.getStringDefault(regionNode, "name", s -> true,
        String.format("Region %d", regionID), "");
    String regionDescription = ParserUtils.getStringDefault(regionNode, "description", s -> true,
        "", "");
    List<RegionPathRecord> regionPaths = collectRegionPaths(regionNode, regionID);
    Map<String, String> regionFactors = getRegionFactors(regionNode, regionID);
    int regionStartingPopulation = ParserUtils.getIntValueDefault(regionNode,
        "startingPopulation", i -> (i >= 0), DEFAULT_STARTING_POPULATION,
        "starting population must be >= 0");
    double regionDetectionThreshold = ParserUtils.getDoubleValueDefault(regionNode,
        "detectionThresh", d -> (d >= 0 && d <= 1), DEFAULT_DETECTION_THRESHOLD,
        "detection threshold must be between 0 and 1");
    double regionClosedThreshold = ParserUtils.getDoubleValueDefault(regionNode, "closedThresh",
        d -> (d >= 0 && d <= 1), DEFAULT_CLOSE_THRESHOLD,
        "closed threshold must be between 0 and 1");
    String regionColor = ParserUtils.getStringDefault(regionNode, "color",
        s -> s.matches("#(?:[0-9a-fA-F]{3}){1,2}$"), randomColor(), "invalid color provided");
    double antagonistBaseVal = getRegionAntagonistContribution(regionNode, regionFactors);
    return new RegionRecord(regionID, regionName, regionDescription, regionPaths, regionFactors,
        regionStartingPopulation, regionDetectionThreshold, regionClosedThreshold, regionColor,
        antagonistBaseVal);
  }

  /**
   * Helper method to obtain some random color when the provided color is invalid.
   *
   * @return
   */
  private String randomColor() {
    Random r = new Random();
    int rand = r.nextInt(0xffffff + 1);
    return String.format("#%06x", rand);
  }

  /**
   * Method to get a region's ID.
   *
   * @param singleRegionNode
   * @return
   * @throws ConfigJSONException if provided Region is missing its ID or ID is otherwise invalid
   *                             (illegal number, duplicate, unrecognized id from map CSV). This
   *                             Region will be skipped.
   */
  private int getRegionID(JsonNode singleRegionNode) throws ConfigJSONException {
    try {
      int id = ParserUtils.getIntValue(singleRegionNode, "id");
      if (!validRegionIDs.contains(id)) {
        throw new ConfigJSONException(
            String.format("ID %d is not present on the Map CSV, meaning this region is invalid",
                id));
      }
      if (idsAssociatedWithARegion.contains(id)) {
        throw new ConfigJSONException(
            String.format("ID %d is already associated with a region, cannot be linked to another",
                id));
      }
      if (id < 1) {
        throw new ConfigJSONException(
            String.format("ID %d is invalid; region IDs cannot be 0 or negative", id));
      }
      return id;
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConfigJSONException(
          "A region in the Regions JSON is missing an \"id\" node with a unique integer value");
    }
  }

  /**
   * Parses and collects all region path records. Verifies Path ID and activity, but does not check
   * connection IDs yet (this happens after all regions have been parsed).
   *
   * @param jsonNode the jsonNode that contains the list of paths
   * @return a list of parsed path data
   */
  private List<RegionPathRecord> collectRegionPaths(JsonNode jsonNode, int thisRegionID) {
    List<RegionPathRecord> regionPathsList = new ArrayList<>();

    try {
      JsonNode pathsListNode = jsonNode.get("paths");
      for (JsonNode pathNode : pathsListNode) {
        RegionPathRecord rpr = parseRegionPathRecord(pathNode, thisRegionID);
        regionsAllowingEachTypeOfPath.get(rpr.pathRecord().id()).add(thisRegionID);
        regionPathsList.add(rpr);
      }
    } catch (ConfigJSONException e) {
      LOG.warn(e.getMessage());
    }
    return regionPathsList;
  }

  /**
   * Creates a RegionPath RECORD by parsing the jsonNode that holds the information.
   *
   * @param pathNode jsonNode where the region path element is at
   * @return a RegionPathRecord
   */

  private RegionPathRecord parseRegionPathRecord(JsonNode pathNode, int currentRegionID)
      throws ConfigJSONException {
    try {
      int id = getPathID(pathNode);
      Set<Integer> connections = new HashSet<>();
      if (id != 0) {
        connections = ParserUtils.makeIntValuesSet(pathNode, "connections");
      }
      double activity = ParserUtils.getDoubleValueDefault(pathNode, "activity",
          d -> (d >= 0 && d <= 1), DEFAULT_PATH_ACTIVITY, "path activity must be between 0 and 1");
      return new RegionPathRecord(id, connections, activity, validPathData.getPathRecordByID(id));
    } catch (ConfigJSONException e) {
      throw new ConfigJSONException(
          String.format("%s: skipping this path for region %d", e.getMessage(), currentRegionID));
    } catch (NullPointerException f) {
      throw new ConfigJSONException(
          String.format("Unable to parse a path node for region %d, skipping this region path",
              currentRegionID));
    }
  }

  /**
   * Method to get the ID of some path for a Region. Checks whether that ID is a valid path ID.
   *
   * @param singlePathNode
   * @return
   * @throws ConfigJSONException if the path ID is not a valid one
   */
  private int getPathID(JsonNode singlePathNode) throws ConfigJSONException {
    try {
      int pathID = ParserUtils.getIntValue(singlePathNode, "id");
      if (!validPathData.isValidPathID(pathID)) {
        throw new ConfigJSONException(
            String.format("Path ID %d is not a recognized path ID", pathID));
      }
      return pathID;
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConfigJSONException(
          "Illegal or missing arg for path \"id\" value");
    }
  }

  /**
   * Method to create the map of region factors for some region. Returns string map of RF type > RF
   * value.
   *
   * @param regionNode
   * @return
   */
  private Map<String, String> getRegionFactors(JsonNode regionNode, int currentRegionID) {
    Map<String, String> map = new HashMap<>();
    for (RegionFactorRecord rf : validRegionFactorData) {
      try {
        String level = ParserUtils.getNonBlankString(regionNode.get("factors"), rf.name());
        if (!rf.isValidValue(level)) {
          LOG.warn(String.format(
              "Region %d has illegal level %s for factor type %s, using default level %s",
              currentRegionID, level, rf.name(), rf.defaultLevel()));
          level = rf.defaultLevel();
        }
        map.put(rf.name(), level);
      } catch (NullPointerException e) {
        LOG.warn(
            String.format("Could not find RegionFactor %s for region %d, using default level %s",
                rf.name(), currentRegionID, rf.defaultLevel()));
        //System.out.println(regionNode.toPrettyString());
      }
    }
    return subRegionFactorDefaults(map);
  }

  /**
   * Helper method to add any missing regionfactors to a region's region factor map. Uses the
   * default level as the value.
   *
   * @param rawMap
   * @return
   */
  private Map<String, String> subRegionFactorDefaults(Map<String, String> rawMap) {
    for (RegionFactorRecord rf : validRegionFactorData) {
      if (!rawMap.containsKey(rf.name())) {
        rawMap.put(rf.name(), rf.defaultLevel());
      }
    }
    return rawMap;
  }

  /**
   * Gets this region's base antagonist contribution (an average of all of the antag base vals of
   * this region's regionfactors, times the region's explicitly stated baseval).
   *
   * @param regionNode
   * @param regionFactors existing map of this region's regionfactors
   * @return
   */
  private double getRegionAntagonistContribution(JsonNode regionNode,
      Map<String, String> regionFactors) {
    double baseVal = ParserUtils.getDoubleValueDefault(regionNode, "antagonistRate", d -> (d >= 0),
        DEFAULT_ANTAGONIST_BASERATE, "antagonist baserate must be between 0 and 1");
    return baseVal * getRegionFactorAntagFactor(regionFactors);
  }

  /**
   * Helper method to compute the Region-Factor-specific value for the antagonist base val.
   * Separated out to use with missing region creation.
   *
   * @param regionFactors
   * @return
   */
  private double getRegionFactorAntagFactor(Map<String, String> regionFactors) {
    double sum = 0;
    double numFactors = 0;
    for (Entry<String, String> entry : regionFactors.entrySet()) {
      double rfContribution = ((RegionFactorRecord) validRegionFactorData.getModelData(
          entry.getKey())).getAntagBaseVal(entry.getValue());
      sum += rfContribution;
      numFactors += 1;
    }
    return sum / numFactors;
  }

  /**
   * Helper method to initialize the map that checks the allowed Path types for each region.
   */
  private Map<Integer, Set<Integer>> setupPathMapChecker() {
    Map<Integer, Set<Integer>> map = new HashMap<>();
    for (PathRecord pr : validPathData.getValidPaths()) {
      map.put(pr.id(), new HashSet<>());
    }
    return map;
  }

  /**
   * Method to create RegionRecords for any region IDs obtained by the mapreader that do not have
   * valid entries in the Regions JSON. Ensures that a Region object will be created for all
   * expected regions. Uses default values for all values.
   *
   * @return
   */
  private List<RegionRecord> addMissingRegions() {
    List<Integer> notYetDone = new ArrayList<>(validRegionIDs);
    notYetDone.removeAll(idsAssociatedWithARegion);
    List<RegionRecord> lst = new ArrayList<>();
    for (int id : notYetDone) {
      List<RegionPathRecord> prLst = new ArrayList<>();
      for (PathRecord pr : validPathData.getValidPaths()) {
        prLst.add(new RegionPathRecord(id, new HashSet<>(), DEFAULT_PATH_ACTIVITY, pr));
        regionsAllowingEachTypeOfPath.get(pr.id()).add(id);
      }
      Map<String, String> rfs = subRegionFactorDefaults(new HashMap<>());
      RegionRecord rr = new RegionRecord(id, String.format("Region %d", id), "", prLst, rfs,
          DEFAULT_STARTING_POPULATION, DEFAULT_DETECTION_THRESHOLD, DEFAULT_CLOSE_THRESHOLD,
          randomColor(), getRegionFactorAntagFactor(rfs) * DEFAULT_ANTAGONIST_BASERATE);
      idsAssociatedWithARegion.add(id);
      lst.add(rr);
    }
    return lst;
  }

  /**
   * Helper method to go through the RegionPathRecords for each region AFTER all regions have been
   * created. Removes connections that involve regions that do not exist or regions that do not
   * allow for paths of that type.
   *
   * @param unverifiedRegionList
   * @return
   */
  private List<RegionRecord> doPathValidation(List<RegionRecord> unverifiedRegionList) {
    List<RegionRecord> overallList = new ArrayList<>();
    for (RegionRecord rr : unverifiedRegionList) {
      boolean mustUpdate = false;
      List<RegionPathRecord> pathRecordsForThisRegion = new ArrayList<>();
      for (RegionPathRecord rpr : rr.paths()) {
        Set<Integer> verifiedConnections = new HashSet<>();
        for (int c : rpr.connections()) {
          if (regionsAllowingEachTypeOfPath.get(rpr.pathRecord().id()).contains(c)
              && validRegionIDs.contains(c) && c != rr.id()) {
            verifiedConnections.add(c);
          }
        }
        if (verifiedConnections.size() == rpr.connections()
            .size()) {   //all connections for this path type were valid, no need to update
          pathRecordsForThisRegion.add(rpr);
        } else {
          pathRecordsForThisRegion.add(makeNewRegionPathRecord(rpr, verifiedConnections));
          mustUpdate = true;
        }
      }
      if (mustUpdate) {
        overallList.add(updateRegionRecordWithPaths(rr, pathRecordsForThisRegion));
      } else {
        overallList.add(rr);
      }
    }
    return overallList;
  }

  /**
   * Helper method to create a new RegionPathRecord based on a pre-existing one, only updating ids
   * in the Connections set
   *
   * @param old
   * @param connections
   * @return
   */
  private RegionPathRecord makeNewRegionPathRecord(RegionPathRecord old, Set<Integer> connections) {
    return new RegionPathRecord(old.id(), connections, old.activity(), old.pathRecord());
  }

  /**
   * Helper method to create a new RegionRecord with all values the same as a base one except with
   * new values for its RegionPathRecords.
   *
   * @param old
   * @param newPaths
   * @return
   */
  private RegionRecord updateRegionRecordWithPaths(RegionRecord old,
      List<RegionPathRecord> newPaths) {
    return new RegionRecord(old.id(), old.name(), old.description(), newPaths, old.factors(),
        old.startingPopulation(), old.detectionThreshold(), old.closedThreshold(), old.color(),
        old.baseAntagonistRate());
  }

  public RegionsData getRegionData() {
    return this.validRegionsData;
  }
}

package ooga.controller.parsers.paths;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.math.SubPopulationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Paths JSON parser.
 */

public class PathsJSON extends DataFileParser {

  private static final Logger LOG = LogManager.getLogger(PathsJSON.class);
  private static final List<String> validAnimationTypes = Arrays.asList(
      "Fade", "Line");
  //depends on
  private final SubPopulationData validSubPopulations;
  //personal
  private PathData validPaths;
  private final Set<Integer> existingPathIDs = new HashSet<>();  //to check for duplicates
  private final Set<String> existingPathNames = new HashSet<>();
  private final PortSymbolMap portSymbolMap;

  public PathsJSON(JsonNode jnode, SubPopulationData validSubPops) {
    super(jnode);
    validSubPopulations = validSubPops;
    portSymbolMap = new PortSymbolMap();
  }

  public PathData getValidPaths() {
    return validPaths;
  }


  /**
   * Main parsing function for PathsJSON. Sets top node as the "paths" node in the PathsJSON file
   * (so drops any extraneous nodes irrelevant for paths) and sets up PathData.
   *
   * @throws ConfigJSONException if no "paths" node provided
   */
  @Override
  public void parseAllData() throws ConfigJSONException {
    try {
      JsonNode pathNode = getTopNode().get("paths");
      setTopNode(pathNode);
      validPaths = setupPathRecords(pathNode);
    } catch (NullPointerException e) {
      throw new ConfigJSONException("Provided Paths JSON does not contain a \"paths\" node");
    }
  }

  /**
   * Iterates through entries in "paths" node, making a PathRecord for each valid one. Everything
   * for the adjacent/0 one is valid as well--will just have blank entries for its imgs and symbol
   * and "fade" for its animation type, and then its animation type will just never be called.
   *
   * @param pathNode
   * @return
   */
  private PathData setupPathRecords(JsonNode pathNode) {
    List<PathRecord> lst = new ArrayList<>();
    for (JsonNode path : pathNode) {
      try {
        PathRecord p = makePathRecord(path);
        existingPathIDs.add(p.id());
        existingPathNames.add(p.name());
        lst.add(p);
      } catch (ConfigJSONException e) {
        LOG.warn(e.getMessage());
      }
    }
    return new PathData(lst);
  }

  /**
   * Sets up a single PathRecord, called on one entry in the "paths" node array.
   *
   * @param singlePathNode
   * @return PathRecord data object from information held in this object
   * @throws ConfigJSONException if entry is missing valid data, ie the ID
   */
  private PathRecord makePathRecord(JsonNode singlePathNode) throws ConfigJSONException {
    int id = getPathID(singlePathNode);
    String name = ParserUtils.getStringDefault(singlePathNode, "name",
        s -> (!existingPathNames.contains(s)), String.format("Path %d", id),
        "path names must be unique");
    String transportImg = getImgFilepath(singlePathNode, "img");
    String portImg = getImgFilepath(singlePathNode, "portImg");
    String portSymbol = getPathSymbol(singlePathNode);
    portSymbolMap.addPort(portSymbol, portImg, name);
    String animationType = ParserUtils.getStringDefault(singlePathNode, "animationType",
        validAnimationTypes::contains, "Fade", "unrecognized animation type");
    List<SubPopSpreadRecord> subPopSpread = getSubpopTransmission(singlePathNode, id);
    Integer numSpread = ParserUtils.getIntValueDefault(singlePathNode, "numSpread", i -> true,
        10, "");
    return new PathRecord(id, name, transportImg, portImg, animationType, portSymbol, numSpread,
        subPopSpread);
  }

  /**
   * Gets ID of path. Checks if ID is valid, including if provided value is a non-integer or already
   * exists for another path.
   *
   * @param singlePathNode
   * @return
   * @throws ConfigJSONException if path has no ID entry or ID is invalid for some reason
   */
  private int getPathID(JsonNode singlePathNode) throws ConfigJSONException {
    try {
      int id = ParserUtils.getIntValue(singlePathNode, "id");
      if (id < 0 || existingPathIDs.contains(id)) {
        throw new ConfigJSONException(
            String.format("Path ID %d is invalid: may be negative or duplicate, skipping", id));
      }
      return id;
    } catch (NullPointerException e) {
      throw new ConfigJSONException("A Path object in Paths JSON has no entry for ID.");
    } catch (NumberFormatException g) {
      throw new ConfigJSONException(
          String.format("A Path object has non-int arg %s as a value for its ID",
              singlePathNode.get("id").asText()));
    }
  }


  /**
   * Gets string filepath provided. No error checking for this one past the blank value checking,
   * more validation of this file will happen later.
   *
   * @param singlePathNode
   * @param type           img or portImg
   * @return
   */
  private String getImgFilepath(JsonNode singlePathNode,
      String type) {   //I'm not going to add error handling for paths, let's let this happen when the path is actually attempted to be accessed. that way we could have one default img for img or portImg and decide that it will be used at that point--but that's more on the view side of things i think? or at least leave this until we decide what that might be
    try {
      return ParserUtils.getNonBlankString(singlePathNode, type);
    } catch (NullPointerException e) {
      return "";
    }
  }

  /**
   * Gets symbol that this path is represented by on the map csv. Removes invalid characters
   * (whitespace and numbers).
   *
   * @param singlePathNode
   * @return
   */
  private String getPathSymbol(JsonNode singlePathNode) {
    //i think we don't want symbols to have numbers (as represented on map csv) because the region IDs are numeric, and also no whitespace. otherwise, i'm fine w letting the symbol be anything else, even  multiple symbols.
    //if symbol entry is invalid, just going to return blank, and then use random port selector when creating regionViews
    //**This just means that when obtaining this value later, must use PathRecord.hasValidPortSymbol check before doing anything with it! (checks that it's not empty)
    try {
      String symbol = ParserUtils.getNonBlankString(singlePathNode, "symbol");
      //replace numbers and whitespace
      symbol = symbol.replaceAll("\\s+", "");
      symbol = symbol.replaceAll("[0-9]", "");
      if (symbol.isBlank()) {
        throw new NullPointerException();
      }
      return symbol;
    } catch (NullPointerException e) {
      return "";
    }
  }


  /**
   * Creates list of subpop this path transmits, num points user gains when it contributes to this
   * subpopulation of the target region for the first time, and total num people it can transmit. If
   * no/invalid subpopulation type is provided, it skips this entry. [undecided how we will handle
   * blank maps at this point, but it is allowed]
   *
   * @param singlePathNode
   * @return
   */
  private List<SubPopSpreadRecord> getSubpopTransmission(JsonNode singlePathNode, int pathID) {
    List<SubPopSpreadRecord> lst = new ArrayList<>();
    try {
      for (JsonNode entry : singlePathNode.get("subPopulationSpread")) {
        try {
          String subPop = ParserUtils.getNonBlankString(entry, "type");
          if (!validSubPopulations.isValidSubPopOrDefault(subPop)) {
            throw new NullPointerException();
          }
          int pointsWorth = ParserUtils.getIntValueDefault(entry, "pointsWorth", i -> (i >= 0), 1,
              "Path must be worth >=0 points");
          lst.add(new SubPopSpreadRecord(subPop, pointsWorth));
        } catch (NullPointerException a) {
          LOG.warn(String.format(
              "Missing or invalid subpop provided for path ID %d, skipping this subpop", pathID));
        }
      }
      if (lst.isEmpty()) {
        LOG.warn(String.format("Path ID %d does not transmit any subpops", pathID));
      }
    } catch (NullPointerException e) {
      LOG.warn(String.format(
          "Path ID %d does not have a valid array for its \"subPopulationSpread\" node and will not transmit any subpops",
          pathID));
    }
    return lst;
  }

  public PortSymbolMap getPortSymbolMap() {
    return portSymbolMap;
  }
}

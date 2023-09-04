package ooga.controller.parsers.region_factors;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that handles parsing and value checking of provided RegionFactorTypes JSON
 */

public class RegionFactorsJSON extends DataFileParser {

  private static final Logger LOG = LogManager.getLogger(RegionFactorsJSON.class);
  private final Set<String> existingRFTypes = new HashSet<>();

  private RegionFactorData validRegionFactors;

  public RegionFactorsJSON() {
    super();
  }

  public RegionFactorsJSON(JsonNode jnode) {
    super(jnode);
  }

  @Override
  public void parseAllData() throws ConfigJSONException {
    try {
      JsonNode regionFactorsNode = getTopNode().get("regionFactorTypes");
      setTopNode(
          regionFactorsNode);  //want to save specific regionFactorsNode as top node (ie, drop any _comments entries)
      validRegionFactors = setupRegionFactors(getTopNode());
    } catch (NullPointerException e) {
      //node has no regionFactorTypes node
      String msg = "Provided RegionFactors JSON does not contain a \"regionFactorTypes\" node";
      LOG.error(msg);
      throw new ConfigJSONException(String.format("%s: %s", msg, e.getMessage()));
    }
  }

  public RegionFactorData getValidRegionFactors() {
    return validRegionFactors;
  }

  /**
   * Goes thru each object in the regionFactors node and attempts to create a regionFactorRecord of
   * each of them. Skips regionFactors that do not have names provided.
   *
   * @param regionFactorsNode "regionFactorTypes" node in file
   * @return
   */
  private RegionFactorData setupRegionFactors(JsonNode regionFactorsNode) {
    List<RegionFactorRecord> lst = new ArrayList<>();
    for (JsonNode j : regionFactorsNode) {
      try {
        RegionFactorRecord r = makeRegionFactor(j);
        existingRFTypes.add(r.name());
        lst.add(r);
      } catch (ConfigJSONException e) {
        LOG.warn(e.getMessage());
      }
    }
    return new RegionFactorData(lst);
  }

  /**
   * Individual method to create one single RegionFactorRecord from an object in the
   * regionFactorTypes array
   *
   * @param rfObject single JsonNode object within array
   * @return
   */
  private RegionFactorRecord makeRegionFactor(JsonNode rfObject) {
    String name = getName(rfObject);
    Map<String, Double> values = makeValuesMap(rfObject, name);
    String defaultValue = getDefaultValue(rfObject, values);
    return new RegionFactorRecord(name, defaultValue, values);
  }

  /**
   * Method to create a Map of (level, antagonistBaseEffectiveness) corresponding to the entries in
   * the values array for a given RegionFactor node. Skips pairs that do not have a level provided.
   *
   * @param jnode the regionfactor node
   * @return
   * @throws ConfigJSONException if this regionfactor does not have a valid "values" entry or
   *                             "values" does not have any valid entries within it. Will skip
   *                             recognizing this regionfactor.
   */
  private Map<String, Double> makeValuesMap(JsonNode jnode, String name)
      throws ConfigJSONException {
    Map<String, Double> values = new HashMap<>();
    try {
      for (JsonNode val : jnode.get("values")) {
        try {
          String level = ParserUtils.getNonBlankString(val, "level");
          values.put(level,
              ParserUtils.getDoubleValueDefault(val, "antagonistBaseEffectiveness", d -> (d >= 0),
                  1, "val must be >= 0"));
        } catch (NullPointerException n) {
          LOG.warn(String.format(
              "RegionFactor named %s has an invalid entry for \"level\" in one of its values, skipping this level",
              name));
        }
      }
      if (values.isEmpty()) {
        throw new NullPointerException();
      }
      return values;
    } catch (NullPointerException e) {  //node has no values entry or entry is not an array
      throw new ConfigJSONException(String.format(
          "%s: RegionFactor named %s does not have a valid array in its \"values\" node, skipping this RegionFactor",
          e.getMessage(), name));
    }
  }

  /**
   * Gets name from given RegionFactors node. If no name is given, throws exception because region
   * factor cannot be created without one.
   *
   * @param j region factors node
   * @return
   * @throws ConfigJSONException
   */
  private String getName(JsonNode j) throws ConfigJSONException {
    try {
      String type = ParserUtils.getNonBlankString(j, "name");
      if (existingRFTypes.contains(type)) {
        throw new ConfigJSONException(String.format(
            "Duplicated name %s for RegionFactor found. RegionFactors should have unique names, skipping duplicate",
            type));
      }
      return type;
    } catch (NullPointerException e) {
      throw new ConfigJSONException(
          "A RegionFactor has no value for Name, skipping this RegionFactor");
    }
  }

  /**
   * Gets default value for some region factors node. Checks that given default value exists as a
   * given value. If not, or if no default is provided, picks a random value from provided values.
   *
   * @param j
   * @param map
   * @return
   */
  private String getDefaultValue(JsonNode j, Map<String, Double> map) {
    List<String> lst = new ArrayList<>(map.keySet());
    Random r = new Random();
    String default2use = lst.get(r.nextInt(lst.size()));
    return ParserUtils.getStringDefault(j, "defaultLevel", map::containsKey,
        default2use,
        String.format("default level must be one of known levels for that RegionFactor (%s)", lst));
  }
}

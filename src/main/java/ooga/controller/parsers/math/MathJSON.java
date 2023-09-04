package ooga.controller.parsers.math;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.region_factors.RegionFactorData;

/**
 * Class that executes parsing and value checking of Math JSON.
 */

public class MathJSON extends DataFileParser {

  private final RegionFactorData validRegionFactors;
  private SubPopulationData subpops;
  private MathData mathData;

  public MathJSON(JsonNode jnode, RegionFactorData regionFactors) {
    super(jnode);
    validRegionFactors = regionFactors;
  }

  /**
   * Overall method to parse Math JSON
   *
   * @throws ConfigJSONException if no "math" node is provided within this
   */
  @Override
  public void parseAllData() throws ConfigJSONException {
    try {
      JsonNode mathNode = getTopNode().get("math");
      setTopNode(mathNode);
      parseMathNode(mathNode);
    } catch (NullPointerException e) {
      //has no math node
      throw new ConfigJSONException("Provided Math JSON does not contain a \"math\" node");
    }
  }

  /**
   * Parses "math" node in Math JSON
   *
   * @param mathNode "math" node
   * @throws ConfigJSONException if no "populations" node is provided
   */
  private void parseMathNode(JsonNode mathNode) throws ConfigJSONException {
    String defaultPopulation = getDefaultPopulation(mathNode);
    Set<String> otherSubpops = allSubpopNames(mathNode);
    subpops = new SubPopulationData(otherSubpops, defaultPopulation);
    mathData = new MathData(subpops, makeUserPointsRecord(
        mathNode)); //currently unverified in terms of protag factors! to update once protag factors is read
  }

  /**
   * Used in parseMathNode to find single entry of the default population. If one is not given,
   * returns "Default" as the name.
   *
   * @param mathNode "math" node in MathJSON
   * @return name for default population
   */
  private String getDefaultPopulation(JsonNode mathNode) {
    try {
      String def = mathNode.get("defaultPopulation").asText();
      if (def.isBlank()) {
        throw new NullPointerException();
      }
      return def;
    } catch (NullPointerException e) {
      return "Default";   //create some resource bundle to make this language-configureable?
    }
  }


  /**
   * Helper method to initially look through objects in populations array and just get the  names of
   * all subpopulations. Used for determining whether sourcepops are valid later.
   *
   * @param mathNode
   * @return
   */
  private Set<String> allSubpopNames(JsonNode mathNode) throws ConfigJSONException {
    try {
      JsonNode populationsNode = mathNode.get("populations");
      Set<String> lst = new HashSet<>();
      for (JsonNode subpop : populationsNode) {
        try {
          lst.add(subpopName(subpop));
        } catch (ConfigJSONException e) {
          //skip
        }
      }
      return lst;
    } catch (NullPointerException e) {
      throw new ConfigJSONException("No \"populations\" node provided in mathJSON");
    }

  }

  /**
   * Returns name of subpopulation
   *
   * @param subpopObject single population object within "populations" node
   * @return subpop name
   * @throws ConfigJSONException if no subpopulation name is given
   */
  private String subpopName(JsonNode subpopObject) throws ConfigJSONException {
    try {
      return ParserUtils.getNonBlankString(subpopObject, "name");
    } catch (NullPointerException e) {
      throw new ConfigJSONException(
          "A subpopulation object in the Math JSON file is missing a \"name\" entry");
    }
  }

  /**
   * Fetches name of source population, the population from which new additions to the current
   * subpopulation should originate.
   *
   * @param subpopObject
   * @param validSourcePops known valid names for subpopulations, for checking if claimed sourcepop
   *                        is not a valid one
   * @param defaultPop      to add to known valid names
   * @return
   */
  private String sourcePop(JsonNode subpopObject, List<String> validSourcePops, String defaultPop) {
    try {
      List<String> sourcesAndDefault = new ArrayList<>(validSourcePops);
      sourcesAndDefault.add(defaultPop);
      String sourcePop = subpopObject.get("sourcePop").asText();
      if (sourcePop.isBlank() || !sourcesAndDefault.contains(sourcePop) || subpopName(
          subpopObject).equals(
          sourcePop)) { //assuming subpopName method will not throw errors here as it is already known to not throw errors by the fact that it is already called on this exact node immediately before calling this method
        throw new NullPointerException();
      }
      return sourcePop;
    } catch (NullPointerException e) {
      //use default as source pop if none specified
      return defaultPop;
    }
  }

  /**
   * Given a type of factors to look for, will return the list of Strings associated with it.
   * Ignores entries in arr that are not simple values (ie are nodes).
   *
   * @param subpopOrUserPointsNode object in math/populations array OR the math/userpoints object in
   *                               Math.json
   * @param type                   to look for (likely protagonistFactors or regionFactors for the
   *                               subpops, or protagonistParameters or populationParameters for the
   *                               userpoints)
   * @return
   */
  private List<String> getFactors(JsonNode subpopOrUserPointsNode, String type) {
    List<String> lst = new ArrayList<>();
    try {
      JsonNode params = subpopOrUserPointsNode.get(type);
      if (!params.isArray()) {
        throw new NullPointerException();
      }
      for (JsonNode arrItem : params) {
        if (arrItem.isValueNode()) {
          lst.add(arrItem.asText());
        }
      }
      return lst;
    } catch (NullPointerException e) {
      return lst;
    }
  }

  /**
   * Helper method called on a "regionFactors" array to remove any given regionFactors that are not
   * valid regionFactors.
   *
   * @param given regionFactors claimed to exist for some by math json file
   * @return
   */
  private List<String> getOnlyValidRegionFactors(List<String> given) {
    List<String> lst = new ArrayList<>();
    for (String s : given) {
      if (validRegionFactors.isValidField(s)) {
        lst.add(s);
      }
    }
    return lst;
  }

  /**
   * Mmethod to make single UserPoints record from given "math" node within Math JSON. If no node is
   * provided, a blank node will be used, essentially forcing default values to be used (name =
   * "Points", populationParameters = [], protagonistParameters = [])
   *
   * @param mathNode
   * @return
   */
  private MathUserPointsRecord makeUserPointsRecord(JsonNode mathNode) {
    JsonNode userPointsNode;
    try {
      userPointsNode = mathNode.get("userPoints");
    } catch (NullPointerException e) {
      //no userPoints entry provided--work with some blank node and use default values
      ObjectMapper mapper = new ObjectMapper();
      userPointsNode = mapper.createObjectNode();
    }
    String userPointsName = getUserPointsName(userPointsNode);
    List<String> protagParams = getFactors(userPointsNode, "protagonistParameters");
    List<String> subpopParams = getOnlyValidSubpopParams(
        getFactors(userPointsNode, "populationParameters"));
    return new MathUserPointsRecord(userPointsName, subpopParams, protagParams);
  }

  /**
   * Gets name of UserPoints as provided in UserPoints node. If no/invalid name, uses "Points" as
   * name
   *
   * @param userPointsNode
   * @return
   */
  private String getUserPointsName(JsonNode userPointsNode) {
    try {
      String name = userPointsNode.get("name").asText();
      if (name.isBlank()) {
        throw new NullPointerException();
      }
      return name;
    } catch (NullPointerException e) {
      //no name provided
      return "Points";
    }
  }

  /**
   * Helper method called on a "populationParams" array to remove any given subpopParams that are
   * not valid subpopulation types.
   *
   * @param given populationParam claimed to exist for some by math json file
   * @return
   */
  private List<String> getOnlyValidSubpopParams(List<String> given) {
    List<String> lst = new ArrayList<>();
    for (String s : given) {
      if (subpops.isValidSubPopOrDefault(s)) {
        lst.add(s);
      }
    }
    return lst;
  }

  public MathData getMathData() {
    return mathData;
  }
}

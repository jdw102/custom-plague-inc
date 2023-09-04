package ooga.controller.parsers.protagonist_type;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.paths.PathData;
import ooga.controller.parsers.region_factors.RegionFactorData;
import ooga.controller.parsers.region_factors.RegionFactorRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that handles parsing and value checking of ProtagonistTypes JSON
 */

public class ProtagonistTypesJSON extends DataFileParser {

  private static final Logger LOG = LogManager.getLogger(ProtagonistTypesJSON.class);
  private static final double DEFAULT_REGIONFACTOR_AMT = 1;
  private static final double DEFAULT_PATH_AMT = 1;
  private static final double DEFAULT_CORE_AMT = 0;

  //depends on
  private final RegionFactorData validRegionFactors;
  private final PathData validPaths;
  //personal
  private ProtagonistTypesData allProtagonistTypesData;
  private Set<String> expectedCoreFactors;
  private final Set<Integer> existingProtagonistIDs;

  //later: probably want to add duplication checking for path names and regionfactor types bc that is how the modifiers are identified in the protag config file

  /**
   * Constructor that provides data files that data in this JSON depends on (for value checking)
   *
   * @param filenode
   * @param regionFactors
   * @param paths
   */
  public ProtagonistTypesJSON(JsonNode filenode, RegionFactorData regionFactors, PathData paths) {
    super(filenode);
    validRegionFactors = regionFactors;
    validPaths = paths;
    existingProtagonistIDs = new HashSet<>();
  }

  /**
   * Main method that executes the complete data parsing of a JSON file.
   */
  @Override
  public void parseAllData() throws ConfigJSONException {
    try {
      JsonNode protTypesNode = getTopNode().get("protagonistTypes");
      this.setTopNode(protTypesNode);
      this.collectModelData();
    } catch (NullPointerException e) {
      throw new ConfigJSONException(
          "Provided ProtagonistTypes JSON file does not contain a \"protagonistTypes\" node.");
    }

  }

  public ProtagonistTypesData getProtagonistTypesData() {
    return this.allProtagonistTypesData;
  }

  /**
   * Reads "expectedCoreFactors" node and creates set of these factors. Used for parsing protagonist
   * types to know what core factors should exist.
   *
   * @param protagTypesNode entire "protagonistTypes" node in file
   * @return
   */
  private Set<String> getExpectedCoreFactors(JsonNode protagTypesNode) {
    Set<String> lst = new HashSet<>();
    try {
      JsonNode corefactors = protagTypesNode.get("expectedCoreFactors");
      if (!corefactors.isArray()) {
        throw new NullPointerException();
      }
      for (JsonNode factor : corefactors) {
        if (factor.isValueNode() && !factor.asText().isBlank()) {
          lst.add(factor.asText());
        }
      }
      if (lst.isEmpty()) {
        throw new NullPointerException();
      }
    } catch (NullPointerException e) {
      LOG.warn("No valid values for \"expectedCoreFactors\"");
    }
    return lst;
  }

  /**
   * Collects and stores ALL JSON entries of interest within ProtagonistTypes. Skips invalid
   * protagonist types.
   *
   * @throws ConfigJSONException if protagonistTypes node is missing or not an array
   */
  private void collectModelData() throws ConfigJSONException {
    expectedCoreFactors = getExpectedCoreFactors(this.topNode);
    //now, actual protagonist options parsing
    List<ProtagonistTypesRecord> recordsList = new ArrayList<>();
    try {
      JsonNode protagOptions = topNode.get("protagonistOptions");
      for (JsonNode node : protagOptions) {
        try {
          ProtagonistTypesRecord record = createModelRecord(node);
          existingProtagonistIDs.add(record.id());
          recordsList.add(record);
        } catch (ConfigJSONException e) {
          LOG.warn(e.getMessage());
        }
      }
      if (recordsList.isEmpty()) {
        throw new NullPointerException();
      }
      this.allProtagonistTypesData = new ProtagonistTypesData(recordsList,
          new ArrayList<>(expectedCoreFactors));
    } catch (NullPointerException e) {
      String msg = "No \"protagonistOptions\" node or no valid protagonists found in protagonists.json";
      LOG.error(msg);
      throw new ConfigJSONException(msg);
    }
  }

  /**
   * Creates a ProtagonistTypesRecord object, which stores the JSON data.
   *
   * @param singleProtagonistNode
   * @return
   * @throws ConfigJSONException if provided protagonist type does not contain a valid name
   *                             (originates from getID)
   */
  private ProtagonistTypesRecord createModelRecord(JsonNode singleProtagonistNode)
      throws ConfigJSONException {
    int id = getID(singleProtagonistNode);
    String name = ParserUtils.getStringDefault(singleProtagonistNode, "name", s -> true,
        String.format("Protagonist %d", id), "");
    String description = ParserUtils.getStringDefault(singleProtagonistNode, "description",
        s -> true, "", "");
    String imagePath = ParserUtils.getStringDefault(singleProtagonistNode, "image",
        s -> !s.isBlank(), "", String.format("Protagonist %d missing img", id));
    List<ModifierRecord> allfactors = new ArrayList<>();
    allfactors.addAll(
        validateAndSupplement(getFactors(singleProtagonistNode, id, "Core", "Core"), "Core",
            new ArrayList<>(expectedCoreFactors), DEFAULT_CORE_AMT));
    allfactors.addAll(
        validateAndSupplement(getFactors(singleProtagonistNode, id, "Path", "Path"), "Path",
            validPaths.getValidPathNames(), DEFAULT_PATH_AMT));
    allfactors.addAll(validateAndSupplementRF(getRegionFactorModifiers(singleProtagonistNode, id)));
    return new ProtagonistTypesRecord(id, name, description, imagePath, makeMap(allfactors));
  }

  private int getID(JsonNode singleProtagonistNode) throws ConfigJSONException {
    try {
      int id = ParserUtils.getIntValue(singleProtagonistNode, "id");
      if (existingProtagonistIDs.contains(id)) {
        throw new ConfigJSONException(
            String.format("Protagonist ID %d is duplicated, skipping", id));
      }
      return id;
    } catch (NullPointerException | NumberFormatException e) {
      throw new ConfigJSONException(
          "Illegal or missing argument for a protagonist ID, skipping this protagonist");
    }
  }

  /**
   * Gets list of ModifierRecords for some given node that is an array of factors
   *
   * @param jnode node to look in
   * @param type  key of factor array
   * @return
   */
  private List<ModifierRecord> getFactors(JsonNode jnode, int currentID, String key, String type) {
    List<ModifierRecord> lst = new ArrayList<>();
    try {
      JsonNode factorsNode = jnode.get(key);
      if (factorsNode.isArray()) {
        for (JsonNode factorNode : factorsNode) {
          try {
            lst.add(makeModifierRecord(factorNode, type));
          } catch (NullPointerException e) {
            LOG.warn(String.format(
                "Could not parse a modifier in Protagonist %d %s factors, will use preset default val",
                currentID, type));
          }
        }
      }
    } catch (NullPointerException e) {
      LOG.warn(String.format(
          "Could not find %s factors for Protagonist %d, will use preset default val for all factors of this type",
          type, currentID));
    }
    return lst;
  }

  /**
   * Creates modifierRecord from single entry within an array of factors.
   *
   * @param factorNode entry for some factor
   * @param type       category for this modifier (Path/Core/Climate etc)
   * @return
   * @throws NullPointerException if name not a string or val is not a double
   */
  private ModifierRecord makeModifierRecord(JsonNode factorNode, String type)
      throws NullPointerException {
    try {
      String name = ParserUtils.getNonBlankString(factorNode, "name");
      double val = ParserUtils.getDoubleValueExceptions(factorNode, "amount");
      return new ModifierRecord(type, name, val);
    } catch (NumberFormatException e) {
      throw new NullPointerException();   //to be skipped, will be set in validateAndSupplement
    }
  }

  /**
   * Helper method to check rawlist of specified modifiers for some type against the full list of
   * those factors that should exist. Ignores factors with an unrecognized name and creates factors
   * with amount = provided default value for expected factors that are missing.
   *
   * @param rawList      initial list of modifiers, created directly from protagonist file
   * @param factorType   category of factor: should be "Core"/"Path"/one of the region factor
   *                     categories ("climate"/"temperature" etc)
   * @param validSource  list of valid modifier names. Should be expectedCoreFactors when checking
   *                     the raw core factors, validPaths.getValidPathNames() when checking raw path
   *                     modifiers, and some regionFactorRecord.getValuesForType() when checking raw
   *                     regionFactor modifiers for that region factor type.
   * @param defaultValue value to use as the amount when creating some modifierRecord that was not
   *                     included in the protagonistTypes JSON/raw list. In Plague case, is 0 for
   *                     coreFactors and 1 for path/region modifiers.
   * @return validated list of all modifiers for that category. Includes only modifiers for each
   * value in provided validSource, using given amount in config file if it was specified and valid,
   * and defaultValue as amount if that modifier was unspecified or otherwise invalid.
   */
  private List<ModifierRecord> validateAndSupplement(List<ModifierRecord> rawList,
      String factorType, List<String> validSource, double defaultValue) {
    List<ModifierRecord> finalList = new ArrayList<>();
    List<String> notYetDone = new ArrayList<>(
        validSource);   //keeps track of modifiers that *should* exist but *don't*

    //validating ones in raw list--removing ones of an unexpected name (such as "temperate" for a list of temperature regionFactor modifiers
    for (ModifierRecord p : rawList) {
      String name = p.name();
      if (validSource.contains(name) && p.factor().equals(factorType)) {
        finalList.add(p);
        notYetDone.remove(name);
      }
    }
    //now that we've removed invalid ones, need to add expected valid ones that don't exist. Use default value for value.
    for (String s : notYetDone) {
      finalList.add(new ModifierRecord(factorType, s, defaultValue));
    }
    return finalList;
  }

  /**
   * Helper method to set up the List of RegionFactorsModifiersWrapper that the
   * ProtagonistTypeRecord expects for its list of regionFactor modifiers. This must be parsed
   * separately because of the sub-categories within regionFactors.
   *
   * @param singleProtagonistNode
   * @return
   */
  private List<RegionFactorsModifiersWrapper> getRegionFactorModifiers(
      JsonNode singleProtagonistNode, int currentID) {
    List<RegionFactorsModifiersWrapper> lst = new ArrayList<>();
    try {
      JsonNode regionModifiersNode = singleProtagonistNode.get("Region");
      if (!regionModifiersNode.isArray()) {
        throw new NullPointerException();
      }
      for (JsonNode entryForRF : regionModifiersNode) {
        try {
          String type = ParserUtils.getNonBlankString(entryForRF, "type");
          List<ModifierRecord> factorsForRF = getFactors(entryForRF, currentID, "factors",
              type);   //makes list of modifierRecords for this specific regionFactor type, just like how overall Core and Path modifiers are made
          lst.add(new RegionFactorsModifiersWrapper(type,
              factorsForRF));   //creates wrapper class for that subcategory
        } catch (NullPointerException e) {
          LOG.warn(String.format(
              "Error parsing a subset of RegionFactors for Protagonist ID %d, will use preset default val for any value affected",
              currentID));
        }
      }
    } catch (NullPointerException e) {
      LOG.warn(String.format(
          "Could not find any RegionFactors for Protagonist ID %d, will use preset default val for all",
          currentID));
    }
    return lst;
  }

  /**
   * Helper method to remove unrecognized regionFactors and add missing but expected ones. Parallels
   * the validateAndSupplement method for the individual ModifierRecord lists, but does this for
   * each list of this type for each category of regionFactors. Removes
   * RegionFactorsModifiersWrapper for unrecognized regionFactor types and creates these for missing
   * but expected regionFactor types. Calls the validateAndSupplement method on each PMR list within
   * the RegionFactorModifiersWrapper. Note that the RegionFactorsModifiersWrapper is only used for
   * passing these values between the inital creation of these regionfactors modifiers lists to this
   * method--this is just for ease of checking the categories
   *
   * @param rawList unverified  list of each regionFactor type & corresponding modifiers as
   *                specified in config file
   * @return fully validated list of all expected regionFactors, categorized by type
   */
  private List<ModifierRecord> validateAndSupplementRF(
      List<RegionFactorsModifiersWrapper> rawList) {
    List<ModifierRecord> finalList = new ArrayList<>();
    List<String> notBeenDone = new ArrayList<>(validRegionFactors.getRegionFactorTypes());

    //validating ones in raw list--removing ones of an unexpected type (such as "governmentType" for Plague)
    for (RegionFactorsModifiersWrapper rfmw : rawList) {
      String type = rfmw.type();
      if (validRegionFactors.isValidField(type)) {
        List<ModifierRecord> entry = validateAndSupplement(rfmw.modifiers(), type,
            ((RegionFactorRecord) validRegionFactors.getModelData(type)).getValuesForType(),
            DEFAULT_REGIONFACTOR_AMT);    //gets validated list of modifiers for that specific regionFactor type
        finalList.addAll(entry);
        notBeenDone.remove(type);
      }
    }

    //now that we've removed invalid ones, need to add expected valid ones that don't exist. Use 1 for value.
    for (String unsetType : notBeenDone) {
      List<ModifierRecord> entry = validateAndSupplement(new ArrayList<>(), unsetType,
          ((RegionFactorRecord) validRegionFactors.getModelData(unsetType)).getValuesForType(), 1);
      finalList.addAll(entry);
    }
    return finalList;
  }

  /**
   * Helper method to create finalized map of modifier records, organized by type
   * (climate/Path/wealth/Core etc). Input lists are fully validated.
   *
   * @param allMRlists
   * @return
   */

  private Map<String, List<ModifierRecord>> makeMap(List<ModifierRecord> allMRlists) {
    Map<String, List<ModifierRecord>> m = new HashMap<>();
    for (ModifierRecord mr : allMRlists) {
      if (!m.containsKey(mr.factor())) {
        m.put(mr.factor(), new ArrayList<>());
      }
      m.get(mr.factor()).add(mr);
    }
    return m;
  }

  /**
   * Returns unmodifiable list of immutable objects.
   *
   * @return
   */
  public List<String> getCoreFactors() {
    return allProtagonistTypesData.getValidCoreFactors();
  }
}
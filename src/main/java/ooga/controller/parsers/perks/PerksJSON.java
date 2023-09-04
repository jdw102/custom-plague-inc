package ooga.controller.parsers.perks;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that parses and value checks Perks JSON
 */

public class PerksJSON extends DataFileParser {

  private static final Logger LOG = LogManager.getLogger(PerksJSON.class);

  //depends on
  private final ProtagonistTypesData protagData;
  //personal
  private final Set<Integer> existingIDs = new HashSet<>();
  private PerkData perkData;

  public PerksJSON(JsonNode jnode, ProtagonistTypesData protagonistData) {
    super(jnode);
    protagData = protagonistData;
  }

  /**
   * Overall parse method
   *
   * @throws ConfigJSONException if provided PerksJSON does not contain a "perks" node
   */
  @Override
  public void parseAllData() {
    try {
      JsonNode perksNode = getTopNode().get("perks");
      setTopNode(perksNode);
      perkData = parsePerksNode(perksNode);
      if (perkData.getValidPerks().isEmpty()) {
        throw new NullPointerException();
      }
    } catch (NullPointerException | ConfigJSONException e) {
      LOG.warn("No valid perks found in perks.json");
      perkData = new PerkData(new ArrayList<>());
    }
  }

  /**
   * Logic to parse "perks" node in Perks JSON. Goes through each entry in "perks" node and attempts
   * to create a PerkRecord from each (skipping invalid ones), then validating that provided prereqs
   * are valid perk IDs and creating PerkData.
   *
   * @param perksNode
   * @return
   * @throws ConfigJSONException if "perks" node is not array
   */
  private PerkData parsePerksNode(JsonNode perksNode) throws ConfigJSONException {
    if (!perksNode.isArray()) {
      throw new ConfigJSONException("Provided perks node does not contain an array of perks");
    }
    List<PerkRecord> unverifiedPerks = new ArrayList<>();
    for (JsonNode perk : perksNode) {
      try {
        PerkRecord rec = makePerk(perk);
        existingIDs.add(rec.id());
        unverifiedPerks.add(rec);
      } catch (ConfigJSONException e) {
        LOG.warn(e.getMessage());
      }
    }
    return new PerkData(validatePrereqs(unverifiedPerks));
  }

  /**
   * Creates single PerkRecord off of an entry within the "perks" array.
   *
   * @param singlePerkNode
   * @return
   * @throws ConfigJSONException if perk does not have a name or has an empty factorsModified
   *                             array--creation of this perk is skipped
   */
  private PerkRecord makePerk(JsonNode singlePerkNode) throws ConfigJSONException {
    int id = getID(singlePerkNode);
    int groupId = ParserUtils.getIntValueDefault(singlePerkNode, "group", i -> true, 0, "");
    String imagePath = ParserUtils.getStringDefault(singlePerkNode, "imagePath", s -> true, "", "");
    String name = ParserUtils.getStringDefault(singlePerkNode, "name", s -> true,
        String.format("Perk %d", id), "");
    String description = ParserUtils.getStringDefault(singlePerkNode, "description", s -> true, "",
        "");
    String actorName = ParserUtils.getStringDefault(singlePerkNode, "actorName", s -> true,
        "Protagonist", "");
    Integer cost = ParserUtils.getIntValueDefault(singlePerkNode, "cost", i -> true, 1, "");
    Set<Integer> prereqs = getPrereqIDs(singlePerkNode, id);
    List<ModifierRecord> effects = getFactors(singlePerkNode, id);
    return new PerkRecord(id, groupId, name, description, cost, actorName, prereqs, effects,
        imagePath);
  }

  /**
   * Gets ID of provided perk
   *
   * @param singlePerkNode
   * @return
   * @throws ConfigJSONException if no/invalid (duplicate/negative/non-int) id
   */
  private int getID(JsonNode singlePerkNode) throws ConfigJSONException {
    try {
      Integer id = ParserUtils.getIntValue(singlePerkNode, "id");
//      if (id == 0) {
//        return 0;
//      }
      if (existingIDs.contains(id) || id < 0) {
        throw new ConfigJSONException(
            String.format(
                "A perk has invalid id arg %d. This may be duplicated or negative, skipping", id));
      }
      return id;
    } catch (NumberFormatException a) {
      throw new ConfigJSONException(
          "A perk in the Perks JSON has a non-int arg as its id, skipping");
    } catch (NullPointerException e) {
      throw new ConfigJSONException("Perk has no id field, skipping");
    }
  }

  /**
   * Create list of ids that some perk claims it depends on. That these IDs are valid perk IDs are
   * verified later.
   *
   * @param singlePerkNode
   * @return
   */
  private Set<Integer> getPrereqIDs(JsonNode singlePerkNode, int perkID) {
    Set<Integer> lst = new HashSet<>();
    try {
      JsonNode prereqs = singlePerkNode.get("prereqID");
      for (JsonNode val : prereqs) {
        try {
          int i = Integer.parseInt(val.asText());
          lst.add(i);
        } catch (NullPointerException | NumberFormatException a) {
          LOG.warn(String.format("Invalid arg in prereqs for Perk ID %d", perkID));
        }
      }
    } catch (NullPointerException e) {  //no prereqID node
      LOG.warn(String.format("Could not parse prereqs for Perk ID %d", perkID));
    }
    return lst;
  }

  /**
   * Creates list of ModifierRecords given in "factorsModified" array for some given perk. Checks
   * that names for modifiers are valid ones and skips making invalid ones.
   *
   * @param singlePerkNode
   * @return
   * @throws ConfigJSONException if factorsModified node does not exist/is not array or if resulting
   *                             list of factorsModified is blank--creation of this perk will be
   *                             skipped (no use for a perk that does nothing...)
   */
  private List<ModifierRecord> getFactors(JsonNode singlePerkNode, int perkID)
      throws ConfigJSONException {
    List<ModifierRecord> lst = new ArrayList<>();
    try {
      for (JsonNode factor : singlePerkNode.get("factorsModified")) {
        try {
          String type = ParserUtils.getNonBlankString(factor, "factor");
          String name = ParserUtils.getNonBlankString(factor, "name");
          Double amt = ParserUtils.getDoubleValueExceptions(factor, "effect");
          if (protagData.isValidValueForField(type, name)) {
            lst.add(new ModifierRecord(type, name, amt));
          } else {
            LOG.warn(String.format(
                "Factor %s name %s is not a known factor, skipping this modifier for Perk ID %d",
                type, name, perkID));
          }
        } catch (NullPointerException | NumberFormatException e) {
          LOG.warn(String.format(
              "Could not parse an entry in factorsModified for Perk ID %d, skipping this modifier",
              perkID));
        }
      }
      if (lst.isEmpty()) {
        throw new NullPointerException();
      }
      return lst;
    } catch (NullPointerException f) {
      throw new ConfigJSONException(String.format(
          "Perk ID %d does not modify any known factors or has an invalid \"factorsModified\" array, skipping creation of this perk",
          perkID));
    }
  }

  /**
   * Helper method to validate the list prereq ids for each perk. Called after each perk is created
   * so program knows what values exist for valid perk IDs. Clears out invalid values, including the
   * perk's own ID if present.
   *
   * @param unvalidatedPerks
   * @return list of perks, each of which only contain valid perk IDs in their prereqPerks list
   */
  private List<PerkRecord> validatePrereqs(List<PerkRecord> unvalidatedPerks) {
    List<PerkRecord> lst = new ArrayList<>();
    for (PerkRecord mr : unvalidatedPerks) {
      if (mr.prereqPerks().isEmpty()) {
        lst.add(mr);
      } else {
        Set<Integer> prereqs = new HashSet<>(mr.prereqPerks());
        prereqs.retainAll(existingIDs);
        prereqs.remove(mr.id());
        lst.add(
            new PerkRecord(mr.id(), mr.groupId(), mr.name(), mr.description(), mr.cost(),
                mr.actorName(), prereqs,
                mr.factorModifiers(), mr.imagePath()));
      }
    }
    return lst;
  }

  public PerkData getPerkData() {
    return perkData;
  }
}

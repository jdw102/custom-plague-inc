package ooga.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import ooga.controller.factories.InvalidPerkException;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.ParserUtils;
import ooga.model.actor.Antagonist;
import ooga.model.actor.PerkTreeModel;
import ooga.model.gamestate.GameData;
import ooga.model.gamestate.UserPoints;
import ooga.model.region.Census;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import org.json.simple.JSONObject;

/**
 * Class that reads in JSON file or object and updates the appropriate game data.
 */
public class SaveFileLoader {

  private static final String SAVE_PATH = "/games/%s/save.json";
  private final JsonNode dataNode;

  public SaveFileLoader(String gameType) {
    File file = new File(getClass().getResource(String.format(SAVE_PATH, gameType)).getPath());
    JsonNode topNode = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      topNode = mapper.readTree(file);
    } catch (IOException e) {
      throw new ConfigJSONException("");
    }
    dataNode = topNode.get("saveData");
  }

  public SaveFileLoader(JSONObject saveData) {
    JsonNode topNode = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      topNode = mapper.readTree(saveData.toJSONString());
    } catch (IOException e) {
      throw new ConfigJSONException("");
    }
    dataNode = topNode.get("saveData");
  }


  public int getProtagonistId() {
    return ParserUtils.getIntValue(dataNode, "protagonistId");
  }

  public String getProtagonistName() {
    return ParserUtils.getNonBlankString(dataNode, "protagonistName");
  }

  /**
   * Loads data into appropriate classes.
   *
   * @param perkTreeModel has perks updated
   * @param gameData      has time updated
   * @param regionMap     has populations updated
   * @param userPoints    has points updated
   * @param antagonist    has progress updated
   */
  public void loadSave(PerkTreeModel perkTreeModel, RegionMap regionMap, GameData gameData,
      UserPoints userPoints, Antagonist antagonist) throws InvalidPerkException {
    configureAntagonist(dataNode.get("antagonistProgress"), antagonist);
    configurePoints(dataNode.get("points"), userPoints);
    configureRegions(dataNode.get("regionStates"), regionMap);
    configureTime(dataNode.get("days"), gameData);
    try {
      configurePerks(dataNode.get("unlockedPerks"), perkTreeModel);
    } catch (NullPointerException e) {
      //user had no unlocked perks from before, that's fine
    }

  }

  private void configureAntagonist(JsonNode node, Antagonist antagonist) {
    antagonist.updateAmount(node.asDouble());
  }

  private void configurePoints(JsonNode node, UserPoints userPoints) {
    userPoints.adjustPoints(node.asDouble());
  }

  private void configureRegions(JsonNode regionsArray, RegionMap regionMap) {
    for (JsonNode regionNode : regionsArray) {
      adjustRegion(regionNode, regionMap);
    }
  }

  private void adjustRegion(JsonNode stateNode, RegionMap regionMap) {

    int id = stateNode.get("id").asInt();
    boolean isOpen = stateNode.get("isOpen").asBoolean();
    boolean hasAntagonist = stateNode.get("hasAntagonist").asBoolean();
    RegionModel region = regionMap.getRegionModelByID(id);
    Census census = createCensus(stateNode.get("census"), region);
    RegionState newState = new RegionState(census, isOpen, hasAntagonist);
    region.setNextState(newState);
    region.updateState();
  }

  private Census createCensus(JsonNode node, RegionModel regionModel) {
    Census census = regionModel.getCurrentState().census();
    Census newCensus = new Census();
    while (census.hasNext()) {
      String name = census.next();
      int amt = ParserUtils.getIntValue(node, name);
      newCensus.addPopulation(name, amt);
    }
    return newCensus;
  }

  private void configureTime(JsonNode node, GameData gameData) {
    int day = node.asInt();
    for (int i = 0; i < day; i++) {
      gameData.updateDay();
    }
  }

  private void configurePerks(JsonNode idArray, PerkTreeModel perkTreeModel)
      throws InvalidPerkException {
    for (JsonNode id : idArray) {
      perkTreeModel.getPerk(id.asInt()).setActiveState();
    }
    perkTreeModel.preparePerks();
  }
}

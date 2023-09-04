package ooga.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import ooga.model.actor.Antagonist;
import ooga.model.actor.PerkModel;
import ooga.model.actor.PerkTreeModel;
import ooga.model.gamestate.GameData;
import ooga.model.gamestate.UserPoints;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Class that creates save file for game state.
 */
public class SaveFileWriter {

  private static final String SAVE_PATH = "/games/%s";
  private final String gameType;
  private FileWriter fileWriter;
  private final RegionMap regionMap;
  private final GameData gameData;
  private final UserPoints userPoints;
  private final Antagonist antagonist;

  public SaveFileWriter(String gameType, RegionMap regionMap, GameData gameData,
      UserPoints userPoints, Antagonist antagonist) {
    this.gameType = gameType;
    this.regionMap = regionMap;
    this.gameData = gameData;
    this.userPoints = userPoints;
    this.antagonist = antagonist;
  }

  /**
   * Creates JSONObject for game state of appropriate classes.
   *
   * @param protagonistName the entered username
   * @param protagonistId   the id of the protagonist
   */
  public JSONObject saveFile(String protagonistName, int protagonistId,
      PerkTreeModel perkTreeModel)
      throws IOException, PopulationNotFoundException {
//    String path = getClass().getResource(String.format(SAVE_PATH, gameType)).getPath();
//    File file = new File(String.format(path + "\\save.json"));
//    System.out.println(path + file);
//    file.createNewFile();
//    fileWriter = new FileWriter(file);
    JSONObject jsonObject = new JSONObject();
    JSONObject topNode = new JSONObject();
    topNode.put("protagonistId", protagonistId);
    topNode.put("protagonistName", protagonistName);
    topNode.put("antagonistProgress", antagonist.getAmount());
    topNode.put("days", gameData.getDay());
    topNode.put("points", userPoints.getPoints());
    topNode.put("regionStates", createRegionStates(regionMap));
    topNode.put("unlockedPerks", createPerksUnlockedIDs(perkTreeModel));
    jsonObject.put("saveData", topNode);
//    fileWriter.write(jsonObject.toJSONString());
//    fileWriter.close();
    return jsonObject;
  }

  private JSONArray createRegionStates(RegionMap regionMap) throws PopulationNotFoundException {
    JSONArray stateArray = new JSONArray();
    while (regionMap.hasNext()) {
      RegionModel regionModel = regionMap.next();
      stateArray.add(createRegionState(regionModel));
    }
    return stateArray;
  }

  private JSONObject createRegionState(RegionModel regionModel) throws PopulationNotFoundException {
    JSONObject state = new JSONObject();
    RegionState regionState = regionModel.getCurrentState();
    state.put("id", regionModel.getId());
    state.put("isOpen", regionState.isOpen());
    state.put("hasAntagonist", regionState.hasAntagonist());
    state.put("census", createCensus(regionState.census()));
    return state;
  }

  private JSONObject createCensus(Census census) throws PopulationNotFoundException {
    JSONObject object = new JSONObject();
    while (census.hasNext()) {
      String name = census.next();
      object.put(name, census.getPopulation(name));
    }
    return object;
  }

  private JSONArray createPerksUnlockedIDs(PerkTreeModel perkTreeModel) {
    JSONArray array = new JSONArray();
    Iterator<Integer> iterator = perkTreeModel.getIdIterator();
    while (iterator.hasNext()) {
      PerkModel perkModel = perkTreeModel.getPerk(iterator.next());
      if (perkModel.isActive()) {
        array.add(perkModel.getId());
      }
    }
    return array;
  }

}

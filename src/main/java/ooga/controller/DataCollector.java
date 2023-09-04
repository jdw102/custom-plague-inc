package ooga.controller;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import ooga.controller.factories.CalculatorFactory;
import ooga.controller.parsers.GameConfigParser;
import ooga.controller.parsers.end_conditions.EndConditionsJSON;
import ooga.controller.parsers.end_conditions.InvalidConditionException;
import ooga.controller.parsers.events.EventRecordsCollection;
import ooga.controller.parsers.events.EventsJSON;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.paths.PathData;
import ooga.controller.parsers.paths.PathsJSON;
import ooga.controller.parsers.paths.PortSymbolMap;
import ooga.controller.parsers.perks.GroupNameMap;
import ooga.controller.parsers.perks.GroupsJSON;
import ooga.controller.parsers.perks.PerkData;
import ooga.controller.parsers.perks.PerksJSON;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesData;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesJSON;
import ooga.controller.parsers.region_factors.RegionFactorData;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import ooga.controller.parsers.regions.RegionsData;
import ooga.controller.parsers.regions.RegionsJSON;
import ooga.model.gamestate.EndConditions;
import ooga.model.gamestate.UserPoints;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.region.Census;
import ooga.view.map.MapReader;


/**
 * Class that uses parsers and calculator factory to parse all config files and to allow for setup
 * in the controller.
 */
public class DataCollector {

  private static final String GAME_PATH = "/games/";
  private final String gameType;
  private final RegionFactorData regionFactorData;
  private GameConfigParser gameConfigParser;
  private final PathData pathData;
  private final ProtagonistTypesData protagonistData;
  private final PerkData perkData;
  private final RegionsData regionsData;
  private final PortSymbolMap portSymbolMap;
  private final GroupNameMap groupNameMap;
  private final EventRecordsCollection eventRecordsCollection;
  private final MapReader mapReader;
  private final CalculatorFactory calculatorFactory;


  public DataCollector(String gameType) throws InvalidFactorException, InvalidOperationException {
    this.mapReader = new MapReader(gameType);
    this.gameType = gameType;
    gameConfigParser = new GameConfigParser();
    regionFactorData = createRegionFactorData();
//    subPopulationData = createSubPopulationData();
    calculatorFactory = new CalculatorFactory(getNode("growth"),
        getNode("spread"), getNode("points"));
    PathsJSON pathsJSON = new PathsJSON(getNode("paths"), calculatorFactory.getSubPopulationData());
    pathsJSON.parseAllData();
    pathData = pathsJSON.getValidPaths();
    gameConfigParser = new GameConfigParser();
    protagonistData = createProtagonistData();
    JsonNode groupsNode = getNode("groups");
    GroupsJSON groupsJSON = new GroupsJSON(groupsNode);
    groupNameMap = groupsJSON.getGroupNameMap();
    perkData = createPerkData(groupsJSON);
    portSymbolMap = pathsJSON.getPortSymbolMap();
    regionsData = createRegionsData();
    EventsJSON eventsJSON = new EventsJSON(getNode("events"));
    eventsJSON.parseAllData();
    eventRecordsCollection = eventsJSON.getEventRecordsCollection();
  }

  public EventRecordsCollection getEventRecordsCollection() {
    return eventRecordsCollection;
  }

  private PerkData createPerkData(GroupsJSON groupsJSON) {
    JsonNode perksNode = getNode("perks");
    groupsJSON.parseAllData();
    PerksJSON perksJSON = new PerksJSON(perksNode, protagonistData);
    perksJSON.parseAllData();
    return perksJSON.getPerkData();
  }

  private RegionFactorData createRegionFactorData() {
    JsonNode regionFactorsNode = getNode("factors");
    RegionFactorsJSON regionFactorsJSON = new RegionFactorsJSON(regionFactorsNode);
    regionFactorsJSON.parseAllData();
    RegionFactorData regionFactorData = regionFactorsJSON.getValidRegionFactors();
    return regionFactorData;
  }

  private ProtagonistTypesData createProtagonistData() {
    JsonNode protagonistNode = getNode("protagonists");
    ProtagonistTypesJSON protagonistTypesJSON = new ProtagonistTypesJSON(protagonistNode,
        getRegionFactorData(), getPathData());
    protagonistTypesJSON.parseAllData();
    ProtagonistTypesData protagonistData = protagonistTypesJSON.getProtagonistTypesData();
    return protagonistData;
  }

  public JsonNode getNode(String fileName) {
    String file = String.format("%s%s/%s.json", GAME_PATH, gameType, fileName);
    return gameConfigParser.parseJSONConfig(
        new File(getClass().getResource(file).getPath()));
  }

  public RegionFactorData getRegionFactorData() {
    return regionFactorData;
  }

  public PathData getPathData() {
    return pathData;
  }

  public ProtagonistTypesData getProtagonistData() {
    return protagonistData;
  }

  public PerkData getPerkData() {
    return perkData;
  }

  public RegionsData getRegionsData() {
    return regionsData;
  }

  public GroupNameMap getGroupNameMap() {
    return groupNameMap;
  }

  private RegionsData createRegionsData() {
    JsonNode regionsNode = getNode("regions");
    RegionsJSON regionsJSON = new RegionsJSON(regionsNode, getRegionFactorData(),
        calculatorFactory.getSubPopulationData(),
        getPathData(), mapReader.getRegionIDS());
    regionsJSON.parseAllData();
    return regionsJSON.getRegionData();
  }

  public EndConditions createEndConditions(String conditionType) throws InvalidConditionException {
    JsonNode node = getNode(conditionType);
    EndConditionsJSON endConditionsJSON = new EndConditionsJSON(node);
    endConditionsJSON.parseAllData();
    EndConditions conditions = endConditionsJSON.getEndConditions();
    return conditions;
  }

  public PortSymbolMap getPortSymbolMap() {
    return portSymbolMap;
  }

  public MapReader getMapReader() {
    return mapReader;
  }

  public UserPoints getUserPoints() {
    return calculatorFactory.createUserPoints();
  }

  public Census getStartCensus() {
    return calculatorFactory.createStartCensus();
  }

  public GrowthCalculator getGrowthCalculator() {
    return calculatorFactory.createGrowthStateCalculator();
  }

  public SpreadCalculator getSpreadCalculator() {
    return calculatorFactory.createSpreadStateCalculator();
  }

  public PointsCalculator getPointsCalculator() {
    return calculatorFactory.createPointsCalculator();
  }
}

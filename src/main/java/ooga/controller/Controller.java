package ooga.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ooga.controller.database.DatabaseConnection;
import ooga.controller.database.DatabaseException;
import ooga.controller.factories.AntagonistFactory;
import ooga.controller.factories.EventFactory;
import ooga.controller.factories.InvalidActorException;
import ooga.controller.factories.InvalidPerkException;
import ooga.controller.factories.PathsFactory;
import ooga.controller.factories.PerkFactory;
import ooga.controller.factories.ProtagonistFactory;
import ooga.controller.factories.RegionFactory;
import ooga.controller.parsers.end_conditions.InvalidConditionException;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.PerkTreeModel;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.Conditionals;
import ooga.model.gamestate.EndConditions;
import ooga.model.gamestate.GameData;
import ooga.model.gamestate.GameModel;
import ooga.model.gamestate.GameState;
import ooga.model.gamestate.Progressor;
import ooga.model.gamestate.UserPoints;
import ooga.model.gamestate.statehandler.DefaultGrowthModel;
import ooga.model.gamestate.statehandler.DefaultPointsModel;
import ooga.model.gamestate.statehandler.GrowthModel;
import ooga.model.gamestate.statehandler.PointsModel;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.InvalidEventException;
import ooga.model.region.PathNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionPaths;
import ooga.view.DisplayView;
import ooga.view.PerkPopUpView;
import ooga.view.ProgressBarView;
import ooga.view.ProtagonistSelector;
import ooga.view.ProtagonistView;
import org.json.simple.JSONObject;

/**
 * A controller that allows communication from the user to the backend through the DisplayView. The
 * controller also sets up all the backend models by reading in data from the config files using the
 * DataCollector class. It controls the game start, perk selection, and updates the model as the
 * animation plays in the front end.
 */
public class Controller {

  private String gameType;
  private GameModel gameModel;
  private DisplayView displayView;
  private DataCollector dataCollector;
  private RegionFactory regionFactory;
  private PathsFactory pathFactory;
  private PerkFactory perkFactory;
  private EventFactory eventFactory;
  private AntagonistFactory antagonistFactory;
  private ProtagonistFactory protagonistFactory;
  private PerkTreeModel perkTreeModel;
  private final ProtagonistMap protagonistMap;
  private int protagonistId;
  private final Map<String, Progressor> progressorMap;
  private SaveFileWriter saveFileWriter;
  private SaveFileLoader saveFileLoader;
  private ProtagonistSelector protagonistSelector;
  private boolean loadRemote;


  public Controller() {
    progressorMap = new HashMap<>();
    protagonistMap = new ProtagonistMap();
  }

  /**
   * Sets up the DataCollector and ProtagonistFactory to allow for user selection of a protagonist.
   *
   * @param gameType
   * @param protagonistSelector the view class that allows for selection
   */
  public void setUpProtagonistSelection(String gameType, ProtagonistSelector protagonistSelector) {
    this.gameType = gameType;
    try {
      dataCollector = new DataCollector(gameType);
    } catch (InvalidFactorException | InvalidOperationException e) {
      protagonistSelector.showError(e);
    }
    this.protagonistSelector = protagonistSelector;
    protagonistFactory = new ProtagonistFactory(
        dataCollector.getProtagonistData(), gameType);
    protagonistFactory.make(protagonistSelector, protagonistMap);
  }

  /**
   * Instantiates the region and path factories.
   */
  private void initializeRegionFactories()
      throws InvalidFactorException, InvalidOperationException {
    regionFactory = new RegionFactory(dataCollector.getRegionsData(), gameType);
    pathFactory = new PathsFactory(dataCollector.getRegionsData(),
        regionFactory.getRegionPortCoordinateMap(), gameType);
  }

  /**
   * Initializes SaveFileLoader with SaveData and selects the appropriate protagonist id.
   *
   * @param saveData the JSON node that contains all the save data
   */
  public void prepareRemoteSave(JSONObject saveData) {
    saveFileLoader = new SaveFileLoader(saveData);
    int id = saveFileLoader.getProtagonistId();
    String name = saveFileLoader.getProtagonistName();
    protagonistId = id;
    protagonistSelector.getProtagonistView(id).updateUserName(name);
    loadRemote = true;
  }

  /**
   * Sets up all backend classes using factories and links them to the front end through the
   * DisplayView.
   *
   * @param displayView primary view class
   */
  public void setUpGame(DisplayView displayView) {
    ProtagonistView protagonistView = protagonistSelector.getProtagonistView(protagonistId);
    this.displayView = displayView;
    displayView.setProtagonistView(protagonistView);
    try {
      initializeRegionFactories();
      antagonistFactory = new AntagonistFactory(dataCollector.getNode("antagonist"));
    } catch (InvalidFactorException | InvalidOperationException e) {
      displayView.showError(e);
    }
    Protagonist protagonist = protagonistMap.getProtagonist(protagonistId);
    Antagonist antagonist = antagonistFactory.createAntagonist(dataCollector.getRegionFactorData());
    UserPoints userPoints = dataCollector.getUserPoints();
    GameData gameData = new GameData(0);
    GameState gameState = null;
    try {
      gameState = createGameState(gameData);
    } catch (InvalidConditionException e) {
      displayView.showError(e);
    }
    RegionMap regionMap = regionFactory.make(displayView, dataCollector.getMapReader(),
        dataCollector.getPortSymbolMap(), dataCollector.getStartCensus(), gameState);
    perkFactory = new PerkFactory(dataCollector.getPerkData(), protagonist, antagonist, userPoints,
        dataCollector.getGroupNameMap(), gameType, regionMap);
    perkTreeModel = createPerkTreeModel(userPoints, displayView);
    try {
      gameModel = createGameModel(antagonist, userPoints, protagonist, gameState,
          gameData, regionMap);
    } catch (InvalidPerkException e) {
      displayView.showError(e);
    }
    eventFactory.make(dataCollector.getEventRecordsCollection(), displayView, gameModel);
    linkProgressBars();
    if (loadRemote) {
      gameModel.toggleGameState();
    }
  }

  /**
   * Instantiates the GameModel.
   *
   * @param antagonist
   * @param userPoints
   * @param protagonist
   * @param gameState
   * @param gameData
   * @param regionMap
   * @return the game model
   */
  private GameModel createGameModel(Antagonist antagonist, UserPoints userPoints,
      Protagonist protagonist, GameState gameState, GameData gameData,
      RegionMap regionMap) throws InvalidPerkException {
    eventFactory = new EventFactory(regionMap);
    RegionPaths regionPaths = pathFactory.make(dataCollector.getMapReader(), displayView,
        regionMap);
    linkGameDataDisplays(gameData);
    displayView.setGameData(gameData);
    initializeAllCheckers(antagonist, userPoints, gameData, gameState);
    displayView.setGameState(gameState);
    gameState.addObserver(displayView);
    GameModel gameModel = new GameModel(createGrowthModel(), createPointsModel(userPoints),
        protagonist,
        antagonist,
        gameState, regionMap, regionPaths);
    saveFileWriter = new SaveFileWriter(gameType, regionMap, gameData, userPoints, antagonist);
    if (loadRemote) {
      saveFileLoader.loadSave(perkTreeModel, regionMap, gameData, userPoints, antagonist);
    }
    return gameModel;
  }

  /**
   * Links the display views progress bars to their correct progressors.
   */
  private void linkProgressBars() {
    linkProgressBar(displayView.getLeftProgressBar());
    linkProgressBar(displayView.getRightProgressBar());
  }

  /**
   * Creates the model that handles points growth.
   *
   * @param userPoints class tracks points
   */
  private PointsModel createPointsModel(UserPoints userPoints) {
    PointsCalculator pointsCalculator = dataCollector.getPointsCalculator();
    PointsModel pointsModel = new DefaultPointsModel(pointsCalculator, userPoints);
    return pointsModel;
  }

  /**
   * Creates the model that handles population growth.
   */
  private GrowthModel createGrowthModel() {
    GrowthCalculator growthCalculator = dataCollector.getGrowthCalculator();
    SpreadCalculator spreadCalculator = dataCollector.getSpreadCalculator();
    GrowthModel growthModel = new DefaultGrowthModel(growthCalculator, spreadCalculator);
    return growthModel;
  }

  /**
   * Creates the primary game state.
   *
   * @param gameData the class that tracks populations over time
   * @return game state instance
   */
  private GameState createGameState(GameData gameData) throws InvalidConditionException {
    EndConditions winConditions = dataCollector.createEndConditions("win");
    EndConditions loseConditions = dataCollector.createEndConditions("lose");
    GameState gameState = new GameState(gameData, winConditions, loseConditions);
    return gameState;
  }

  /**
   * Selects region to start game in.
   *
   * @param id the region id
   */
  public void selectRegion(int id) throws PopulationNotFoundException {
    gameModel.startGame(id);
  }

  /**
   * Updates the game model.
   */
  public void update() {
    try {
      gameModel.update();
    } catch (ModifierNotFoundException | PopulationNotFoundException | PathNotFoundException |
             FactorNotFoundException | InvalidEventException e) {
      displayView.showError(e);
    }
  }

  /**
   * Activates selected perk.
   *
   * @param id the perk id
   */
  public void activatePerk(int id) {
    try {
      perkTreeModel.togglePerkActivation(id);
    } catch (InvalidPerkException e) {
      displayView.showError(e);
    }
  }

  /**
   * Creates the perk tree model that handles perk acquisition and links it to the front end.
   *
   * @param userPoints
   * @param displayView
   * @return perk tree model
   */
  public PerkTreeModel createPerkTreeModel(UserPoints userPoints, DisplayView displayView) {
    PerkPopUpView perkPopUpView = displayView.getPerkPopUpView();
    try {
      perkFactory.make(displayView, displayView.getSettingsBundle(),
          dataCollector.getEventRecordsCollection());
    } catch (InvalidActorException | InvalidPerkException e) {
      displayView.showError(e);
    }
    perkTreeModel = perkFactory.getPerkTreeModel();
    perkFactory.addPerkTrees(perkPopUpView.getPerkTreeView(), displayView.getSettingsBundle());
    displayView.displayPerks();
    displayView.setUserPoints(userPoints);
    userPoints.addObserver(displayView.getPerkPopUpView());
    return perkTreeModel;
  }

  /**
   * Sets the condition checkers of the game state.
   *
   * @param antagonist checks antagonist conditions
   * @param userPoints checks points conditions
   * @param gameData   checks time and population conditions
   * @param gameState  has condition checkers added to it
   */
  private void initializeAllCheckers(Antagonist antagonist, UserPoints userPoints,
      GameData gameData, GameState gameState) {
    initializeConditionChecker(antagonist, Conditionals.ANTAGONIST, gameState);
    initializeConditionChecker(userPoints, Conditionals.POINTS, gameState);
    initializeConditionChecker(gameData, Conditionals.TIME, gameState);
    initializeConditionChecker(gameData, Conditionals.POPULATION, gameState);
    initializeProgressMap(antagonist, userPoints, gameData);
  }

  /**
   * Creates map of progressors for initialization of progress bars in the front end.
   *
   * @param antagonist for antagonist progress
   * @param userPoints for points progress
   * @param gameData   for time and population progress
   */
  private void initializeProgressMap(Antagonist antagonist, UserPoints userPoints,
      GameData gameData) {
    progressorMap.put("AntagonistProgressBar", antagonist);
    progressorMap.put("PointsProgressBar", userPoints);
    progressorMap.put("PopulationProgressBar", gameData);
    progressorMap.put("TimeProgressBar", gameData);
  }

  /**
   * Game state has progressor added as condition checker.
   *
   * @param progressor  acts as condition checker
   * @param conditional the enum representing the type of condition
   * @param gameState   has condition checker added to it
   */
  private void initializeConditionChecker(Progressor progressor,
      Conditionals conditional, GameState gameState) {
    gameState.addProgressor(progressor, conditional);
  }

  /**
   * Links all the data displays related to population growth to the game data class
   *
   * @param gameData the game data class containing population over time
   */
  private void linkGameDataDisplays(GameData gameData) {
    gameData.addObserver(displayView.getRightCounter());
    gameData.addObserver(displayView.getLeftCounter());
    gameData.addObserver(displayView.getCalendarView());
    gameData.addObserver(displayView.getWorldDataView());
  }

  /**
   * Links progress bar view class to the appropriate progressor.
   *
   * @param progressBarView
   */
  private void linkProgressBar(ProgressBarView progressBarView) {
    String type = progressBarView.getType();
    Progressor progressor = progressorMap.get(type);
    progressBarView.setProgressor(progressor);
    progressor.addObserver(progressBarView);
  }

  /**
   * Sets the correct protagonist id by user selection in the front end.
   *
   * @param id the protagonist id
   */
  public void selectProtagonist(int id) {
    this.protagonistId = id;
  }

  /**
   * Saves the relevant data to a save file.
   *
   * @param protagonistName the user inputted name
   * @param protagonistId   the id of the protagonist type
   * @param userName        the username
   */

  public void saveGame(String protagonistName, int protagonistId, String userName) {
    try {
      JSONObject save = saveFileWriter.saveFile(protagonistName, protagonistId, perkTreeModel);
      try {
        DatabaseConnection dc = new DatabaseConnection();
        dc.putSave(userName, gameType, save);
      } catch (DatabaseException f) {
        displayView.showError(f);
      }
    } catch (IOException | PopulationNotFoundException e) {
      displayView.showError(e);
    }
  }
}


package ooga.model.gamestate;

import static ooga.Main.GLOBAL_RNG;

import java.util.ArrayList;
import java.util.Collection;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.GrowthModel;
import ooga.model.gamestate.statehandler.PointsModel;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.InvalidEventException;
import ooga.model.region.PathNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RandomEvent;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionPaths;

/**
 * Primary game model class that updates all the major model components.
 */

public class GameModel {

  private final GrowthModel defaultGrowthModel;
  private final PointsModel defaultPointsModel;
  private final Antagonist antagonist;
  private final Protagonist protagonist;
  private final GameState gameState;
  private final RegionMap regionMap;
  private final RegionPaths regionPaths;
  private final Collection<RandomEvent> randomEvents;

  public GameModel(GrowthModel growthModel, PointsModel pointsModel, Protagonist protagonist,
      Antagonist antagonist, GameState gameState, RegionMap regionMap, RegionPaths regionPaths) {
    this.defaultGrowthModel = growthModel;
    this.defaultPointsModel = pointsModel;
    this.gameState = gameState;
    this.protagonist = protagonist;
    this.antagonist = antagonist;
    this.regionMap = regionMap;
    this.regionPaths = regionPaths;
    randomEvents = new ArrayList<>();
  }

  /**
   * Starts the game by calling the spread start census method in the selected region.
   */

  public void startGame(int regionId) throws PopulationNotFoundException {
    regionMap.spreadStartingCensus(regionId);
    gameState.toggleRunning();
  }

  public void toggleGameState() {
    gameState.toggleRunning();
  }

  public void addEvent(RandomEvent randomEvent) {
    randomEvents.add(randomEvent);
  }

  /**
   * Updates data using the growth and points models.
   */

  public void update()
      throws ModifierNotFoundException, PopulationNotFoundException, PathNotFoundException, FactorNotFoundException, InvalidEventException {
    if (gameState.isRunning()) {
      defaultGrowthModel.determineStates(protagonist, antagonist, regionMap, regionPaths);
      defaultPointsModel.updatePoints(protagonist, regionMap, regionPaths);
      defaultGrowthModel.updateStates(regionMap);
      checkEvents();
      gameState.update(regionMap);
    }
  }

  /**
   * Randomly triggers events.
   */

  private void checkEvents() throws InvalidEventException {
    double rand = GLOBAL_RNG.nextDouble();
    for (RandomEvent e : randomEvents) {
      if (e.getProbability() >= rand && e.isAvailable()) {
        e.activate();
      }
    }
  }
}

package ooga.model.gamestate.statehandler;

import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.UserPoints;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionPaths;

/**
 * The model that handles point growth by comparing the censuses of a region's current state to its
 * next state.
 */

public abstract class PointsModel {

  private final PointsCalculator pointsCalculator;
  private final UserPoints userPoints;

  public PointsModel(PointsCalculator pointsCalculator, UserPoints userPoints) {
    this.pointsCalculator = pointsCalculator;
    this.userPoints = userPoints;
  }

  protected PointsCalculator pointsCalculator() {
    return pointsCalculator;
  }

  protected UserPoints userPoints() {
    return userPoints;
  }

  public abstract void updatePoints(Protagonist protagonist, RegionMap regionMap,
      RegionPaths regionPaths)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException;
}

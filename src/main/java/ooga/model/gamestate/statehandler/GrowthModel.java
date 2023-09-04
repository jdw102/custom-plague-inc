package ooga.model.gamestate.statehandler;

import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionPaths;

/**
 * The model that determines the next states of the region through growth and spread calculations
 * and then updates them when called.
 */

public abstract class GrowthModel {

  private final GrowthCalculator growthCalculator;
  private final SpreadCalculator spreadCalculator;

  public GrowthModel(GrowthCalculator growthCalculator, SpreadCalculator spreadCalculator) {
    this.growthCalculator = growthCalculator;
    this.spreadCalculator = spreadCalculator;
  }

  public abstract void determineStates(Protagonist protagonist, Antagonist antagonist,
      RegionMap regionMap, RegionPaths regionPaths)
      throws PopulationNotFoundException, PathNotFoundException, ModifierNotFoundException, FactorNotFoundException;

  public abstract void updateStates(RegionMap regionMap);

  protected GrowthCalculator growthCalculator() {
    return growthCalculator;
  }

  protected SpreadCalculator spreadCalculator() {
    return spreadCalculator;
  }

}

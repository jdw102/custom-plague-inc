package ooga.model.gamestate.statehandler.calculators;

import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionModel;

/**
 * Calculates points produced by growth and spread of target and spreadable populations.
 */
public interface PointsCalculator {

  /**
   * @param region
   * @param protagonist
   * @return 0 or 1 - random user points (DNA points in the Plague) by calculating the probability
   * of getting a point and then uses an RNG to determine whether the user accrues those points. We
   * may change this to private calculateProbability and public calculatePoints methods
   */
  int calculateGrowthPoints(RegionModel region, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException;

  /**
   * Calculate the amount of points produced by spreading to a region.
   *
   * @param path        the path model
   * @param protagonist
   * @return points produced
   */
  int calculateSpreadPoints(PathModel path, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException;

}

package ooga.model.gamestate.statehandler.calculators;

import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.TargetPopulation;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;

/**
 * Calculates the growth of target populations.
 */

public interface GrowthCalculator {

  /**
   * @param region
   * @param protagonist
   * @param antagonist
   * @return calculates RegionState for a given region and the protagonist. This includes updating
   * the antagonist rate of the region and checking threshold values for the hasAntagonist and
   * isClosed boolean.
   */
  RegionState determineNextState(RegionModel region, Protagonist protagonist,
      Antagonist antagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException;

  void addTargetPopulation(TargetPopulation targetPopulation);

  /**
   * Calculates the progress of an antagonist in one iteration.
   *
   * @param region
   * @param antagonist
   * @return the amount to be added
   */
  double calculateAntagonistProgress(RegionMap region, Antagonist antagonist)
      throws ModifierNotFoundException;

}

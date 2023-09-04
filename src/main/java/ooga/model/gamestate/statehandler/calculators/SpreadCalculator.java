package ooga.model.gamestate.statehandler.calculators;

import ooga.model.actor.Antagonist;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.region.Census;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;

/**
 * Handles creation of transfer census that has probability to infect other regions.
 */

public interface SpreadCalculator {

  void addSpreadablePopulation(SpreadablePopulation population);

  /**
   * Generates a census to be transferred between one region to another.
   *
   * @param path        model that contains the origin and destination region
   * @param protagonist
   * @param antagonist
   */
  Census generateTransferCensus(PathModel path, Protagonist protagonist, Antagonist antagonist)
      throws PopulationNotFoundException;
}

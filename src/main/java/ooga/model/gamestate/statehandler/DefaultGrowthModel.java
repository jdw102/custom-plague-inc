package ooga.model.gamestate.statehandler;

import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathModel;
import ooga.model.region.PathNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionPaths;

/**
 * Handles all population changes within a region
 */
public class DefaultGrowthModel extends GrowthModel {

  public DefaultGrowthModel(GrowthCalculator growthCalculator, SpreadCalculator spreadCalculator) {
    super(growthCalculator, spreadCalculator);
  }

  @Override
  public void determineStates(Protagonist protagonist, Antagonist antagonist, RegionMap regionMap,
      RegionPaths regionPaths)
      throws PopulationNotFoundException, PathNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    while (regionMap.hasNext()) {
      RegionModel region = regionMap.next();
      region.setNextState(growthCalculator().determineNextState(region, protagonist, antagonist));
    }
    spreadPopulations(protagonist, antagonist, regionPaths);
    antagonist.updateAmount(growthCalculator().calculateAntagonistProgress(regionMap, antagonist));
  }

  @Override
  public void updateStates(RegionMap regionMap) {
    while (regionMap.hasNext()) {
      regionMap.next().updateState();
    }
  }

  private void spreadPopulations(Protagonist protagonist, Antagonist antagonist,
      RegionPaths regionPaths)
      throws PopulationNotFoundException, PathNotFoundException {
    while (regionPaths.hasNext()) {
      PathModel path = regionPaths.next();
      Census transferredPeople = spreadCalculator().generateTransferCensus(path, protagonist,
          antagonist);
      path.transferPeople(transferredPeople);
    }
  }
}

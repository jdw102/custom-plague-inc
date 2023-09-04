package ooga.model.gamestate.statehandler.calculators;

import static ooga.Main.GLOBAL_RNG;

import java.util.ArrayList;
import java.util.List;
import ooga.model.actor.Antagonist;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.region.Census;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;

/**
 * Kevin Tu
 */

public class DefaultSpreadCalculator implements SpreadCalculator {

  private final List<SpreadablePopulation> spreadablePopulations;


  public DefaultSpreadCalculator() {
    spreadablePopulations = new ArrayList<>();
  }

  @Override
  public void addSpreadablePopulation(SpreadablePopulation spreadablePopulation) {
    spreadablePopulations.add(spreadablePopulation);
  }

  @Override
  public Census generateTransferCensus(PathModel path, Protagonist protagonist,
      Antagonist antagonist) throws PopulationNotFoundException {
    Census originCensus = path.getOrigin().getCurrentState().census();
    int total = Math.min(path.getTotalAmount(), originCensus.getTotal());
    Census transferCensus = new Census();
    while (originCensus.hasNext()) {
      transferCensus.addPopulation(originCensus.next(), 0);
    }
    int i = 0;
    while (i < total) {
      adjustSpreadablePopulations(transferCensus, originCensus, protagonist, path);
      i++;
    }
    while (transferCensus.hasNext()) {
      String name = transferCensus.next();
      transferCensus.addPopulation(name,
          Math.min(transferCensus.getPopulation(name), originCensus.getPopulation(name)));
    }
    return transferCensus;
  }

  /**
   * Adjusts the spreadable populations in the transfer census.
   *
   * @param transferCensus
   * @param originCensus
   * @param protagonist
   * @param path
   */
  private void adjustSpreadablePopulations(Census transferCensus, Census originCensus,
      Protagonist protagonist, PathModel path)
      throws PopulationNotFoundException {
    double rand = GLOBAL_RNG.nextDouble();
    for (SpreadablePopulation population : spreadablePopulations) {
      double modifier = population.operateOnFactor(protagonist.getTransmissionModifiers()
          .getModifier(path.getType()));
      double probability =
          ((1.0 * originCensus.getPopulation(population.getName())) / originCensus.getTotal())
              * modifier;
      if (probability >= rand) {
        transferCensus.adjustPopulation(population.getName(), 1);
      } else {
        transferCensus.adjustPopulation(population.getAlternatePopulation(), 1);
      }
    }
  }


}

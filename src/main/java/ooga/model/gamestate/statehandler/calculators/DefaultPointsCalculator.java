package ooga.model.gamestate.statehandler.calculators;

import ooga.Main;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.PointsParameters;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionModel;

public class DefaultPointsCalculator implements PointsCalculator {

  private final PointsParameters pointsParameters;


  public DefaultPointsCalculator(PointsParameters pointsParameters) {
    this.pointsParameters = pointsParameters;
  }


  @Override
  public int calculateGrowthPoints(RegionModel region, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    double totalPoints = 0;
    Census currentCensus = region.getCurrentState().census();
    Census nextCensus = region.getNextState().census();
    while (pointsParameters.hasNext()) {
      String name = pointsParameters.next();
      int currAmt = currentCensus.getPopulation(name);
      int nextAmt = nextCensus.getPopulation(name);
      double points = pointsParameters.calculatePoints(currAmt, nextAmt);
      totalPoints += points;
    }
    double modifier = pointsParameters.getGrowthFactors().totalMod(protagonist, region);
    return rngRound(totalPoints * (1 + modifier));
  }

  private int rngRound(double unrounded) {
    double iPart = unrounded - (int) unrounded;
    if (Main.GLOBAL_RNG.nextDouble() < iPart) {
      return (int) unrounded + 1;
    }
    return (int) unrounded;
  }

  /**
   * @param path@return the number of DNA points generated for infecting a new country
   */
  @Override
  public int calculateSpreadPoints(PathModel path, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    int ret = 0;
    RegionModel region = path.getDestination();
    while (pointsParameters.hasNext()) {
      String name = pointsParameters.next();
      if (!region.getCurrentState().hasPopulation(name) && region.getNextState()
          .hasPopulation(name)) {
        ret += path.getPoints(name);
      }
    }
    double modifier = pointsParameters.getSpreadFactors().totalMod(protagonist, region);
    return (int) Math.round(ret * (1 + modifier));
  }
}

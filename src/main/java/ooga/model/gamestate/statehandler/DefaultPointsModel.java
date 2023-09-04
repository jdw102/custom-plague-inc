package ooga.model.gamestate.statehandler;

import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.UserPoints;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionPaths;

/**
 * Handles point generation.
 */
public class DefaultPointsModel extends PointsModel {


  public DefaultPointsModel(PointsCalculator pointsCalculator, UserPoints userPoints) {
    super(pointsCalculator, userPoints);
  }

  @Override
  public void updatePoints(Protagonist protagonist, RegionMap regionMap, RegionPaths regionPaths)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    int growthPoints = calculateGrowthPoints(regionMap, protagonist);
    int spreadPoints = calculateSpreadPoints(regionPaths, protagonist);
    userPoints().adjustPoints(growthPoints + spreadPoints);
  }

  private int calculateSpreadPoints(RegionPaths regionPaths, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    int totalSpreadPoints = 0;
    for (RegionPaths it = regionPaths; it.hasNext(); ) {
      PathModel path = it.next();
      totalSpreadPoints += pointsCalculator().calculateSpreadPoints(path, protagonist);
    }
    return totalSpreadPoints;
  }

  private int calculateGrowthPoints(RegionMap regionMap, Protagonist protagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    int totalGrowthPoints = 0;
    for (RegionMap it = regionMap; it.hasNext(); ) {
      RegionModel region = it.next();
      totalGrowthPoints += pointsCalculator().calculateGrowthPoints(region, protagonist);
    }
    return totalGrowthPoints;
  }
}

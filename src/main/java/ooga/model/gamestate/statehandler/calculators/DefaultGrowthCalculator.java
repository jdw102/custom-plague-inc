package ooga.model.gamestate.statehandler.calculators;

import static ooga.Main.GLOBAL_RNG;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.DrainPopulation;
import ooga.model.gamestate.statehandler.TargetPopulation;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;

/**
 * Kevin Tu
 */

public class DefaultGrowthCalculator implements GrowthCalculator {


  private final List<TargetPopulation> targetPopulations;

  public DefaultGrowthCalculator() {
    targetPopulations = new ArrayList<>();
  }

  @Override
  public void addTargetPopulation(TargetPopulation targetPopulation) {
    targetPopulations.add(targetPopulation);
  }

  @Override
  public RegionState determineNextState(RegionModel region, Protagonist protagonist,
      Antagonist antagonist)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    Census changeCensus = region.getCurrentState().census().getCopy();
    for (TargetPopulation targetPopulation : targetPopulations) {
      String targetName = targetPopulation.getName();
      String sourceName = targetPopulation.getSource();
      int targetPop = calculatePopulationGrowth(targetPopulation.getGrowthFactors(),
          region,
          protagonist, targetName, sourceName);
      int sourcePop = calculateTargetProportion(targetPopulation.getSourceFactors(), region,
          protagonist, targetName);
      Iterator<DrainPopulation> drainPopulationIterator = targetPopulation.getDrainPopulationIterator();
      int targetAmt = targetPop - sourcePop;
      while (drainPopulationIterator.hasNext()) {
        DrainPopulation drainPopulation = drainPopulationIterator.next();
        int drainPop = calculateTargetProportion(drainPopulation.getFactors(), region,
            protagonist, targetName);
        changeCensus.adjustPopulation(drainPopulation.getName(), drainPop);
        targetAmt -= drainPop;
      }
      int sourceAmt = sourcePop - targetPop;
      changeCensus.adjustPopulation(targetName, targetAmt);
      changeCensus.adjustPopulation(sourceName, sourceAmt);
    }
    boolean hasAntagonist = antagonist.checkThreshold(region.getCurrentState().census(),
        region.getAntagonistThreshold());
    boolean isOpen = !antagonist.checkThreshold(region.getCurrentState().census(),
        region.getCloseThreshold());
    RegionState nextState = new RegionState(changeCensus, isOpen, hasAntagonist);
    return nextState;
  }


  /**
   * Calculates by how much the target population grows.
   *
   * @param factors
   * @param region
   * @param protagonist
   * @param trackedName
   * @param sourceName
   * @return the amount to be added
   */
  private int calculatePopulationGrowth(FactorCollection factors, RegionModel region,
      Protagonist protagonist, String trackedName, String sourceName)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    Census census = region.getCurrentState().census();
    double modifier = factors.totalMod(protagonist, region);
    long total = (long) Math.ceil(census.getPopulation(trackedName) * modifier);
    return (int) Math.min(total, census.getPopulation(sourceName));
  }

  /**
   * Calculates what proportion of the target population will be added to the drain.
   *
   * @param factors
   * @param region
   * @param protagonist
   * @param targetName
   */
  private int calculateTargetProportion(FactorCollection factors, RegionModel region,
      Protagonist protagonist, String targetName)
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    double modifier = factors.totalMod(protagonist, region);
    Census census = region.getCurrentState().census();
    double rand = GLOBAL_RNG.nextDouble();
    if (modifier >= rand) {
      return (int) Math.ceil(census.getPopulation(targetName) * modifier);
    } else {
      return 0;
    }
  }


  @Override
  public double calculateAntagonistProgress(RegionMap regionMap, Antagonist antagonist)
      throws ModifierNotFoundException {
    double progressToAdd = 0;
    for (RegionMap it = regionMap; it.hasNext(); ) {
      RegionModel region = it.next();
      double contribution = region.getAntagonistContribution(antagonist);
      progressToAdd += contribution;
    }
    return progressToAdd;
  }

}

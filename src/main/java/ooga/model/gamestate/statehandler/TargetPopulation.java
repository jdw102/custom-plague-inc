package ooga.model.gamestate.statehandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import ooga.model.gamestate.statehandler.factors.FactorCollection;

/**
 * A class that represents a population that can be grown by the growth model. It contains the name
 * of it, the source it comes from, and a list of drain populations it may turn into.
 */

public class TargetPopulation {

  private final String source;
  private final String name;
  private final FactorCollection growthFactors;
  private final FactorCollection sourceFactors;
  private final Collection<DrainPopulation> drainPopulations;


  public TargetPopulation(String name, String source, FactorCollection growthFactors,
      FactorCollection sourceFactors) {
    this.name = name;
    this.source = source;
    this.growthFactors = growthFactors;
    this.sourceFactors = sourceFactors;
    drainPopulations = new ArrayList<>();
  }

  public void addDrainPopulation(DrainPopulation drainPopulation) {
    drainPopulations.add(drainPopulation);
  }

  public Iterator<DrainPopulation> getDrainPopulationIterator() {
    return drainPopulations.iterator();
  }

  public String getSource() {
    return source;
  }

  public FactorCollection getGrowthFactors() {
    return growthFactors;
  }

  public FactorCollection getSourceFactors() {
    return sourceFactors;
  }

  public String getName() {
    return name;
  }
}

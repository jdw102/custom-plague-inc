package ooga.model.gamestate.statehandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.util.BiOperation;

/**
 * Represents the list of populations that trigger point growth and the factors that affect point
 * generation for growth and spread.
 */

public class PointsParameters implements Iterator<String> {

  private final List<String> populationParameters;
  private final FactorCollection growthFactors;
  private final FactorCollection spreadFactors;
  private Iterator<String> iterator;
  private final BiOperation pointsOperation;

  public PointsParameters(FactorCollection growthFactors, FactorCollection spreadFactors,
      BiOperation pointsOperation) {
    this.pointsOperation = pointsOperation;
    this.growthFactors = growthFactors;
    this.spreadFactors = spreadFactors;
    populationParameters = new ArrayList<>();
  }

  public void addPopulationParameter(String name) {
    populationParameters.add(name);
  }

  public FactorCollection getGrowthFactors() {
    return growthFactors;
  }

  public FactorCollection getSpreadFactors() {
    return spreadFactors;
  }

  public double calculatePoints(double currAmt, double nextAmt) {
    return pointsOperation.operate(currAmt, nextAmt);
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = populationParameters.iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = populationParameters.iterator();
    }
    return b;
  }

  @Override
  public String next() {
    return iterator.next();
  }
}


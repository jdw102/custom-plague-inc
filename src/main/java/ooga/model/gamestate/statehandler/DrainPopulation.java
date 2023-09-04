package ooga.model.gamestate.statehandler;

import ooga.model.gamestate.statehandler.factors.FactorCollection;

public class DrainPopulation {

  private final FactorCollection factors;
  private final String name;

  public DrainPopulation(String name, FactorCollection factors) {
    this.name = name;
    this.factors = factors;
  }

  public FactorCollection getFactors() {
    return factors;
  }

  public String getName() {
    return name;
  }
}

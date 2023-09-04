package ooga.model.gamestate.statehandler;

import ooga.util.Operation;

/**
 * Represents population that can be spread to other regions between state updates. If the
 * population fails to get added to a transfer census the alternate population is added instead.
 * Typically this is the "normal" population.
 */

public class SpreadablePopulation {

  private final String name;
  private final String alternatePopulation;
  private final Operation operation;

  public SpreadablePopulation(String name, String alternatePopulation, Operation operation) {
    this.name = name;
    this.alternatePopulation = alternatePopulation;
    this.operation = operation;
  }

  public String getName() {
    return name;
  }

  public String getAlternatePopulation() {
    return alternatePopulation;
  }

  public double operateOnFactor(double d) {
    return operation.operate(d);
  }
}

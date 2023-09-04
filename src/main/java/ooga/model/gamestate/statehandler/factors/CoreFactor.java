package ooga.model.gamestate.statehandler.factors;

import ooga.model.actor.Actor;
import ooga.model.actor.Protagonist;
import ooga.model.region.RegionModel;
import ooga.util.Operation;

/**
 * Produces a core factor of a protagonist by operating on it with the appropriate operation.
 */

public class CoreFactor implements Factor {

  private final String name;
  private final Operation operation;

  public CoreFactor(String name, Operation operation) {
    this.name = name;
    this.operation = operation;
  }

  @Override
  public double getOperatedFactor(Actor actor, RegionModel region) {
    return operation.operate(((Protagonist) actor).getCoreModifiers().getModifier(name));
  }

}

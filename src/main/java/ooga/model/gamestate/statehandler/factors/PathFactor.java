package ooga.model.gamestate.statehandler.factors;

import ooga.model.actor.Actor;
import ooga.model.actor.Protagonist;
import ooga.model.region.PathModel;
import ooga.util.Operation;

/**
 * Produces a path factor of a protagonist by operating on it with the appropriate operation.
 */
public class PathFactor {

  private final Operation operation;

  public PathFactor(Operation operation) {
    this.operation = operation;
  }

  public double getOperatedFactor(Actor actor, PathModel path) {
    return operation.operate(((Protagonist) actor).getTransmissionModifiers().getModifier(
        path.getType()));
  }
}

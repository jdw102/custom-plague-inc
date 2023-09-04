package ooga.model.gamestate.statehandler.factors;

import ooga.model.actor.Actor;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.RegionModel;
import ooga.util.Operation;

/**
 * Produces a region factor of a region for an actor and performs the appropriate operation on it.
 */
public class RegionFactor implements Factor {

  private final String name;
  private final Operation operation;

  public RegionFactor(String name, Operation operation) {
    this.name = name;
    this.operation = operation;
  }

  @Override
  public double getOperatedFactor(Actor actor, RegionModel region)
      throws FactorNotFoundException, ModifierNotFoundException {
    return operation.operate(actor.getModifier(name, region.getFactor(name)));
  }


}

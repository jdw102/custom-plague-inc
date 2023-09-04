package ooga.model.gamestate.statehandler.factors;

import ooga.model.actor.Actor;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.RegionModel;

public interface Factor {

  double getOperatedFactor(Actor actor, RegionModel region)
      throws FactorNotFoundException, ModifierNotFoundException;

}

package ooga.model.gamestate.statehandler.factors;

import java.util.ArrayList;
import java.util.Collection;
import ooga.model.actor.Actor;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.RegionModel;
import ooga.util.BiOperation;

/**
 * Class that contains collection of factors that can be totaled.
 */
public class FactorCollection {

  private final Collection<Factor> factors;
  private final BiOperation operation;


  public FactorCollection(BiOperation operation) {
    factors = new ArrayList<>();
    this.operation = operation;
  }

  /**
   * Totals all the factor modifications.
   *
   * @param actor
   * @param regionModel
   */
  public double totalMod(Actor actor, RegionModel regionModel)
      throws ModifierNotFoundException, FactorNotFoundException {
    double ret = 0;
    boolean start = true;
    for (Factor f : factors) {
      if (start) {
        start = false;
        ret = f.getOperatedFactor(actor, regionModel);
      } else {
        ret = operation.operate(ret, f.getOperatedFactor(actor, regionModel));
      }
    }
    return ret;
  }

  public void addFactor(Factor factor) {
    factors.add(factor);
  }

}

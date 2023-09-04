package ooga.controller.parsers.math;

import java.util.Collections;
import java.util.Set;

/**
 * Sub-data class that holds only information about subpopulations declared in Math JSON.
 */

public class SubPopulationData {

  private final Set<String> validSubPops;
  private final String defaultPopulation;

  public SubPopulationData(Set<String> set, String defaultPop) {
    validSubPops = set;
    defaultPopulation = defaultPop;
  }

  /**
   * Returns unmodifiable set of immutable data
   *
   * @return
   */
  public Set<String> getValidSubPops() {
    return Collections.unmodifiableSet(validSubPops);
  }

  /**
   * Returns String (immutable) of defaultPopulation.
   *
   * @return
   */
  public String getDefaultPopulation() {
    return defaultPopulation;
  }

  /**
   * Checks if some given subpop type is a valid one, including the default one.
   *
   * @param subPop
   * @return
   */
  public boolean isValidSubPopOrDefault(String subPop) {
    if (defaultPopulation.equals(subPop)) {
      return true;
    }
    return isValidNonDefaultSubPop(subPop);
  }

  /**
   * Checks if some given subpop type is a valid one, not including the default one.
   *
   * @param subPop
   * @return
   */
  public boolean isValidNonDefaultSubPop(String subPop) {
    return validSubPops.contains(subPop);
  }

  /**
   * Checks if given String is default subpop name
   *
   * @param subPop
   * @return
   */
  public boolean isDefaultSubpop(String subPop) {
    return defaultPopulation.equals(subPop);
  }

}

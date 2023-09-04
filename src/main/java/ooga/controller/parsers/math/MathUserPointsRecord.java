package ooga.controller.parsers.math;

import java.util.Collections;
import java.util.List;

/**
 * Data class to hold information about user points including its name and the factors its
 * calculations depend on
 *
 * @param name
 * @param populationParams
 * @param protagParams
 */

public record MathUserPointsRecord(String name, List<String> populationParams,
                                   List<String> protagParams) {

  @Override
  public String name() {
    return name;
  }

  /**
   * Returns immutable list of immutable objects.
   *
   * @return
   */
  @Override
  public List<String> populationParams() {
    return Collections.unmodifiableList(populationParams);
  }

  /**
   * Returns immutable list of immutable objects.
   *
   * @return
   */
  @Override
  public List<String> protagParams() {
    return Collections.unmodifiableList(protagParams);
  }

  /**
   * Given some protagonistFactor, checks whether this subpopulation's calculations depend on it.
   *
   * @param protagonistFactor
   * @return
   */
  public boolean dependsOnProtagFactor(String protagonistFactor) {
    return protagParams.contains(protagonistFactor);
  }

  public boolean dependsOnSubPop(String subPopType) {
    return populationParams.contains(subPopType);
  }
}

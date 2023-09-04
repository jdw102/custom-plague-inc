package ooga.controller.parsers.region_factors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data class to hold information about an individual category of regionFactors.
 *
 * @param name
 * @param defaultLevel
 * @param values
 */

public record RegionFactorRecord(String name, String defaultLevel, Map<String, Double> values) {

  @Override
  public String name() {
    return name;
  }

  @Override
  public String defaultLevel() {
    return defaultLevel;
  }

  /**
   * Returns immutable map of immutable data
   *
   * @return
   */
  @Override
  public Map<String, Double> values() {
    return Collections.unmodifiableMap(values);
  }

  /**
   * Returns a list but is a copy, so no data vulnerability
   *
   * @return
   */
  public List<String> getValuesForType() {
    List<String> lst = new ArrayList<>();
    for (String s : values.keySet()) {
      lst.add(String.valueOf(s));
    }
    return lst;
  }

  /**
   * Checks if given value is a valid one
   *
   * @param value
   * @return
   */
  public boolean isValidValue(String value) {
    return values.containsKey(value);
  }

  /**
   * ToString override, just to make testing/formatting nice
   *
   * @return
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String s : mapToString()) {
      sb.append(s);
    }
    String string = String.format("%s:\n\tdefault = %s,\n\tvals = %s", name, defaultLevel, sb);
    return string;
  }

  private List<String> mapToString() {
    List<String> lst = new ArrayList<>();
    for (String s : values.keySet()) {
      lst.add(String.format("\n\t\t- %s: base antagonist val = %f", s, values.get(s)));
    }
    return lst;
  }

  /**
   * Gets antagonistBaseVal for some given value
   *
   * @param val
   * @return
   */
  public double getAntagBaseVal(String val) {
    return values.get(val);
  }
}

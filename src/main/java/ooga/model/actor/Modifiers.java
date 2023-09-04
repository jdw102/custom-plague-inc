package ooga.model.actor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A data class that contains modifiers of a certain factor.
 */
public class Modifiers {

  private final Map<String, Double> map;
  private final String name;

  /**
   * Creates instance of modifiers class.
   *
   * @param name the name of the factor it modifies
   */
  public Modifiers(String name) {
    map = new HashMap<String, Double>();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addModifier(String level, double modifier) {
    map.put(level, modifier);
  }

  /**
   * Adjusts the modifier of a factor.
   *
   * @param level the level of the factor
   * @param amt   the amt by which the modifier is changed
   */
  public void adjustModifier(String level, double amt) {
    map.put(level, map.get(level) + amt);
  }

  public double getModifier(String level) {
    return map.get(level);
  }

  public Iterator<String> getModifierIterator() {
    return map.keySet().iterator();
  }
}

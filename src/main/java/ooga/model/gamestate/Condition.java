package ooga.model.gamestate;

import java.util.function.BiPredicate;
import ooga.util.ConditionalOperation;

/**
 * A class that represents a condition for either winning or losing a game. It is checked after
 * every update of the game to determine if the game has.
 */
public class Condition {

  private final BiPredicate<Double, Double> predicate;
  private final double threshold;
  private final Enum type;
  private final String name;
  private final String message;

  /**
   * Creates an instance of a condition.
   *
   * @param threshold the threshold at which the condition is satisfied
   * @param operation the string representing the private method for either greaterThan or lessThan
   * @param type      the type of condition
   * @param name      name of parameter used in the type, for instance the name of population in a
   *                  population type condition
   */
  public Condition(double threshold, ConditionalOperation operation, Enum type, String name,
      String message) {
    this.threshold = threshold;
    predicate = operation::operate;
    this.type = type;
    this.name = name;
    this.message = message;
  }

  /**
   * Checks if the condition is satisfied by passing the threshold and a parameter value into the
   * predicate created in the constructor.
   *
   * @param val the value checked for satisfaction
   * @return true if satisfied, false otherwise
   */
  public boolean check(double val) {
    return predicate.test(val, threshold);
  }

  public Enum getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getMessage() {
    return message;
  }
}

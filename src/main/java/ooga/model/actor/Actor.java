package ooga.model.actor;

/**
 * An interface that contains methods for retrieving, adding, and changing modifiers.
 */
public interface Actor {

  /**
   * Gets a modifier of certain factor at a certain level.
   *
   * @param factor
   * @param level
   */
  double getModifier(String factor, String level) throws ModifierNotFoundException;

  /**
   * Adjusts a modifier of a factor at a certain level.
   *
   * @param factor
   * @param level
   * @param amount
   */
  void adjustModifier(String factor, String level, double amount) throws ModifierNotFoundException;

  void addModifiers(Modifiers modifiers);

}

package ooga.model.actor;

public class ModifierNotFoundException extends Exception {

  private static final String MESSAGE = "Actor %s does not contain modifier for %s at %s";

  public ModifierNotFoundException(String actorName, String factor, String level) {
    super(String.format(MESSAGE, actorName, factor, level));
  }
}

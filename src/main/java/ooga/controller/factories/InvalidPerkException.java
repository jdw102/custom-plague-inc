package ooga.controller.factories;

public class InvalidPerkException extends Exception {

  private static final String MESSAGE = "Perk #%s modifies a factor that does not exist or at a level that does not exist.";

  public InvalidPerkException(int id) {
    super(String.format(MESSAGE, id));
  }
}

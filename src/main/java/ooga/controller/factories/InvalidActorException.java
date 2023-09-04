package ooga.controller.factories;

public class InvalidActorException extends Exception {

  private static final String MESSAGE = "%s is not a valid actor for modification; either use Protagonist or Antagonist";

  public InvalidActorException(String name) {
    super(String.format(MESSAGE, name));
  }
}

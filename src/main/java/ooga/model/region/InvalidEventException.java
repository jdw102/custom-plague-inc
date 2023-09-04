package ooga.model.region;

public class InvalidEventException extends Exception {

  private static final String MESSAGE = "Event with description \"%s\" is not a valid event, please check your config files";

  public InvalidEventException(String message) {
    super(String.format(MESSAGE, message));
  }
}

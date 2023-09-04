package ooga.controller.parsers.math;

public class InvalidOperationException extends Exception {

  private static final String MESSAGE = "%s is not a valid operation";

  public InvalidOperationException(String type) {
    super(String.format(MESSAGE, type));
  }
}

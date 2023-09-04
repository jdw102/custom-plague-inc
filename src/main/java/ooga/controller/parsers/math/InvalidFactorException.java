package ooga.controller.parsers.math;

public class InvalidFactorException extends Exception {

  private static final String MESSAGE = "%s is not a valid factor";

  public InvalidFactorException(String type) {
    super(String.format(MESSAGE, type));
  }
}

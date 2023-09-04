package ooga.controller.parsers.end_conditions;

public class InvalidConditionException extends Exception {

  public static final String MESSAGE = "%s is not a valid condition";

  public InvalidConditionException(String type) {
    super(String.format(MESSAGE, type));
  }
}

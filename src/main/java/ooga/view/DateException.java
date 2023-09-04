package ooga.view;

public class DateException extends Exception {

  public static final String MESSAGE = "Invalid date: %s";

  public DateException(String date) {
    super(String.format(MESSAGE, date));
  }
}

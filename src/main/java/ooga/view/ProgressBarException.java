package ooga.view;

public class ProgressBarException extends Exception {

  public static final String MESSAGE = "%s is not a valid progress bar type.";

  public ProgressBarException(String type) {
    super(String.format(MESSAGE, type));
  }
}

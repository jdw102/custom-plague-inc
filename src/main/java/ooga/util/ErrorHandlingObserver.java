package ooga.util;

/**
 * An interface that extends observer for view classes that can display errors.
 */
public interface ErrorHandlingObserver extends Observer {

  /**
   * A method meant to display exceptions.
   *
   * @param e the exception to be displayed
   */
  void showError(Exception e);
}

package ooga.util;

/**
 * An interface that extends observable meant for model classes that can throw errors.
 */
public interface ErrorThrowingObservable extends Observable {

  /**
   * A method meant to add the error handling observer to the class that implements the interface.
   *
   * @param observer the error handling observer
   */
  void addErrorHandlingObserver(ErrorHandlingObserver observer);

  /**
   * A method meant to pass the error to the error handling observer
   *
   * @param e the exception to be passed
   */
  void throwError(Exception e);
}

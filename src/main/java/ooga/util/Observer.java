package ooga.util;

/**
 * An interface meant to be implemented by view classes so that they can handle model changes.
 */
public interface Observer {

  /**
   * A method that is triggered when an observable notifies the observer.
   */
  void update();
}

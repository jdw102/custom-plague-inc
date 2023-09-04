package ooga.util;

/**
 * An interface meant for model classes that allow them to alert view classes of changes.
 */
public interface Observable {

  /**
   * Adds an observer to the list of observers to be notified.
   *
   * @param observer the new observer
   */
  void addObserver(Observer observer);

  /**
   * Removes observer from list of observers to be notified.
   *
   * @param observer the observer to be removed
   */
  void removeObserver(Observer observer);

  /**
   * A method to notify all observers of a change.
   */
  void notifyObservers();
}

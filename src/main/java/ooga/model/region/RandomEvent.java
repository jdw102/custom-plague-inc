package ooga.model.region;

import java.util.ArrayList;
import java.util.Collection;
import ooga.util.Observable;
import ooga.util.Observer;

public class RandomEvent implements Observable {

  private final double probability;
  private boolean available;
  private boolean activated;
  private final Collection<Observer> observers;
  private final String message;
  private final Runnable action;


  public RandomEvent(double probability, Runnable action, String message) {
    this.action = action;
    this.probability = probability;
    available = true;
    activated = false;
    observers = new ArrayList<>();
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public boolean isAvailable() {
    return available;
  }

  private void setAvailable(boolean b) {
    available = b;
  }

  public boolean isActivated() {
    return activated;
  }

  private void setActivated(boolean b) {
    activated = b;
  }

  public double getProbability() {
    return probability;
  }

  public void activate() throws InvalidEventException {
    try {
      action.run();
      setActivated(true);
      notifyObservers();
      setAvailable(false);
    } catch (RuntimeException e) {
      throw new InvalidEventException(message);
    }

  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }
}

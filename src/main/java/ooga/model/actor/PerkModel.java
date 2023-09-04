package ooga.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import ooga.controller.factories.InvalidPerkException;
import ooga.util.Observable;
import ooga.util.Observer;

/**
 * A parent class for perk models that contains the actions for activating a perk and the
 * prerequisites for triggering those actions.
 */
public class PerkModel implements Observable {

  private final BiPredicate<Double, Integer> activation;
  private final int id;
  private final PerkPrerequisites prerequisites;
  private final Collection<Observer> observers;
  private boolean active;
  private boolean available;
  private final double cost;
  private boolean refundable;


  //Mainly for testing--to hold list of factors affected + numerical effect
  private Map<String, Double> effects;

  /**
   * Creates instance of perk model.
   *
   * @param id            the id of the perk
   * @param activation    the action triggered when activated
   * @param prerequisites the list of ids of perks required to be bought before activation
   */
  public PerkModel(int id, BiPredicate<Double, Integer> activation, PerkPrerequisites prerequisites,
      double cost) {
    this.id = id;
    this.activation = activation;
    this.prerequisites = prerequisites;
    observers = new ArrayList<>();
    effects = new HashMap<>();
    available = true;
    this.cost = cost;
    refundable = true;
  }

  public void setRefundable(boolean b) {
    refundable = b;
  }

  public void setEffects(Map<String, Double> newEffects) {
    this.effects.clear();
    this.effects = newEffects;
  }
//  public Map<String, Double> getEffects() {
//    return Collections.unmodifiableMap(effects);
//  }

  public PerkPrerequisites getPrerequisites() {
    return prerequisites;
  }

  public boolean isActive() {
    return active;
  }

  /**
   * Toggles the activation of the perk. If not active the perk is activated, if active it is
   * refunded. It also notifies any observers of this change.
   */
  public void toggleActivate() throws InvalidPerkException {
    if (!active) {
      activate();
    } else if (refundable) {
      refund();
    }
    notifyObservers();
  }

  public void setActiveState() {
    active = activation.test(0 * 1.0, 1);
    available = true;
    notifyObservers();
  }

  private void activate() throws InvalidPerkException {
    try {
      active = activation.test(-1 * cost, 1);
    } catch (RuntimeException e) {
      throw new InvalidPerkException(id);
    }

  }

  private void refund() throws InvalidPerkException {
    try {
      active = false;
      activation.test(cost, -1);
    } catch (RuntimeException e) {
      throw new InvalidPerkException(id);
    }

  }

  public int getId() {
    return id;
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }

  public void updateAvailability(boolean available) {
    this.available = available;
    notifyObservers();
  }

  public boolean isAvailable() {
    return available;
  }
}

package ooga.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ooga.util.Observable;
import ooga.util.Observer;

/**
 * A class that represents the protagonist of the game and contains all the modifiers associated
 * with it.
 */
public class Protagonist implements Actor, Observable {

  private final ProtagonistFactors CORE = ProtagonistFactors.CORE;
  private final ProtagonistFactors PATH = ProtagonistFactors.PATH;
  private final Map<String, Modifiers> modifiersMap;
  private final Collection<Observer> observers;
  private final int id;

  /**
   * Creates instance of a protagonist.
   */
  public Protagonist(int id) {
    this.id = id;
    modifiersMap = new HashMap<>();
    observers = new ArrayList<>();
  }

  @Override
  public double getModifier(String factor, String level) throws ModifierNotFoundException {
    double mod;
    try {
      mod = modifiersMap.get(factor).getModifier(level);
    } catch (NullPointerException e) {
      throw new ModifierNotFoundException("Protagonist", factor, level);
    }
    return mod;
  }

  public Modifiers getTransmissionModifiers() {
    return modifiersMap.get(PATH.getName());
  }

  public Modifiers getCoreModifiers() {
    return modifiersMap.get(CORE.getName());
  }

  @Override
  public void adjustModifier(String factor, String level, double amount)
      throws ModifierNotFoundException {
    try {
      modifiersMap.get(factor).adjustModifier(level, amount);
    } catch (NullPointerException e) {
      throw new ModifierNotFoundException("Protagonist", factor, level);
    }
    notifyObservers();
  }

  @Override
  public void addModifiers(Modifiers modifiers) {
    modifiersMap.put(modifiers.getName(), modifiers);
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

  public int getId() {
    return id;
  }
}

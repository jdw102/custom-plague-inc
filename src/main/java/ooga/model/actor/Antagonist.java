package ooga.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ooga.model.gamestate.Condition;
import ooga.model.gamestate.Progressor;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.ConditionalOperation;
import ooga.util.Observer;

/**
 * A class that represents the antagonist of a game. It contains a level which is updated with the
 * game model and is calculated by an average of all region's antagonist rates. Also contains
 * modifiers for different region factors which are used to calculate these rates.
 */
public class Antagonist implements Actor, Progressor {

  private final Map<String, Modifiers> modifiersMap;
  private double amount;
  private final List<String> trackedPopulations;
  private final Collection<Observer> observers;
  private final ConditionalOperation thresholdOperation;


  /**
   * Creates instance of antagonist.
   *
   * @param amount the starting level of the antagonist
   */
  public Antagonist(double amount, ConditionalOperation operation) {
    this.thresholdOperation = operation;
    modifiersMap = new HashMap<>();
    this.amount = amount;
    this.trackedPopulations = new ArrayList<>();
    observers = new ArrayList<>();
  }

  public void addTrackedPopulation(String name) {
    trackedPopulations.add(name);
  }

  /**
   * Checks if census surpasses threshold of tracked populations.
   *
   * @param census    region census passed for checking
   * @param threshold either close or antagonist threshold
   */
  public boolean checkThreshold(Census census, double threshold)
      throws PopulationNotFoundException {
    int total = 0;
    for (String s : trackedPopulations) {
      total += census.getPopulation(s);
    }
    double ratio = (1.0 * total) / census.getTotal();
    return thresholdOperation.operate(ratio, threshold);
  }

  public void updateAmount(double val) {
    amount += val;
    notifyObservers();
  }

  @Override
  public double getModifier(String factor, String level) throws ModifierNotFoundException {
    double mod;
    try {
      mod = modifiersMap.get(factor).getModifier(level);
    } catch (NullPointerException e) {
      throw new ModifierNotFoundException("Antagonist", factor, level);
    }
    return mod;
  }

  @Override
  public void adjustModifier(String factor, String level, double amount)
      throws ModifierNotFoundException {
    try {
      modifiersMap.get(factor).adjustModifier(level, amount);
    } catch (NullPointerException e) {
      throw new ModifierNotFoundException("Antagonist", factor, level);
    }
  }

  @Override
  public void addModifiers(Modifiers modifiers) {
    modifiersMap.put(modifiers.getName(), modifiers);
  }

  @Override
  public boolean checkCondition(Condition condition) {
    return condition.check(amount);
  }

  @Override
  public double getProgress(String s) {
    return getProgress();
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

  public double getAmount() {
    return amount;
  }

  @Override
  public double getProgress() {
    return getAmount();
  }
}

package ooga.model.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.util.Observable;
import ooga.util.Observer;

/**
 * Parent class for model representing region. Contains the current state and next state of the
 * region and other important information used in calculations in the game model updating.
 *
 * @author Jerry Worthy
 */
public class RegionModel implements Observable {

  private static final String DEFAULT_LEVEL = "Normal";
  private final Collection<Observer> myObservers;
  private final int id;
  private final Map<String, String> factors;
  private final PathActivityMap pathActivityMap;
  private final double closeThreshold;
  private final double antagonistThreshold;
  private RegionState currentState;
  private RegionState nextState;
  private final double antagonistRate;

  /**
   * Parent class for state of regions. Contains all information that can change between updates of
   * the game model.
   *
   * @param id                  the id of the region
   * @param initialState        the initial region state of the region
   * @param pathActivityMap     represents how often different path types are triggered
   * @param closeThreshold      the threshold at which a certain population causes the region to
   *                            become closed
   * @param antagonistThreshold the threshold at which a certain population triggers antagonist
   *                            activity
   * @param antagonistRate      the base rate of increase that the country contributes to the
   *                            antagonist's progress
   */
  public RegionModel(int id, RegionState initialState, PathActivityMap pathActivityMap,
      double closeThreshold, double antagonistThreshold, double antagonistRate) {
    myObservers = new ArrayList<>();
    currentState = initialState;
    factors = new HashMap<>();
    this.pathActivityMap = pathActivityMap;
    this.closeThreshold = closeThreshold;
    this.antagonistThreshold = antagonistThreshold;
    this.id = id;
    this.antagonistRate = antagonistRate;
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : myObservers) {
      observer.update();
    }
  }

  public boolean isOpen() {
    return currentState.isOpen();
  }

  public boolean hasAntagonist() {
    return currentState.hasAntagonist();
  }

  public RegionState getCurrentState() {
    return currentState;
  }

  public RegionState getNextState() {
    return nextState;
  }

  public void setNextState(RegionState state) {
    nextState = state;
  }

  public void updateState() {
    currentState = nextState;
    notifyObservers();
  }

  @Override
  public void addObserver(Observer observer) {
    myObservers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    myObservers.remove(observer);
  }

  /**
   * Adds a region factor and level of the factor. For instance, "Climate" and "Hot."
   *
   * @param factor the factor
   * @param level  the level of the factor
   */
  public void addFactor(String factor, String level) {
    factors.put(factor, level);
  }

  public String getFactor(String factor) throws FactorNotFoundException {
    String level = DEFAULT_LEVEL;
    if (factors.get(factor) == null) {
      throw new FactorNotFoundException(factor, id);
    } else {
      level = factors.get(factor);
    }
    return level;
  }

  public double getAntagonistThreshold() {
    return antagonistThreshold;
  }

  public double getCloseThreshold() {
    return closeThreshold;
  }

  public int getId() {
    return id;
  }

  /**
   * Produces the contribution a region adds to the overall growth of an antagonist's progress.
   *
   * @param antagonist
   */
  public double getAntagonistContribution(Antagonist antagonist) throws ModifierNotFoundException {
    if (currentState.hasAntagonist()) {
      double modTotal = 1;
      for (String name : factors.keySet()) {
        String level = factors.get(name);
        double mod = antagonist.getModifier(name, level);
        if (modTotal == 0) {
          modTotal = mod;
        } else {
          modTotal *= mod;
        }
      }
      return antagonistRate * modTotal;
    }
    return 0;
  }


  /**
   * Gets the activity of certain path type.
   *
   * @param name the name of the path type
   * @return double representing probability path is triggered
   */
  public double getPathActivity(String name) throws PathNotFoundException {
    return pathActivityMap.getPathActivity(name);
  }

}

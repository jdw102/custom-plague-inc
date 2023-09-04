package ooga.model.gamestate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.Observer;

/**
 * A data class contained by the GameState that is meant to pass data about the protagonist,
 * antagonist, and total populations to the user. It is updated each time the GameState is updated.
 */
public class GameData implements Progressor {

  private final Map<Integer, Census> populationOverTime;
  private final Collection<Observer> observers;
  private int day;

  /**
   * Creates instance of game data.
   *
   * @param startingDay the day at which the game begins keeping track
   */
  public GameData(int startingDay) {
    this.day = startingDay;
    populationOverTime = new HashMap<>();
    observers = new ArrayList<>();
  }

  /**
   * Retrieves total census at a specific day.
   *
   * @param day the day to retrieve
   */
  public Census getCensusAt(int day) {
    return populationOverTime.get(day);
  }


  public int getDay() {
    return day;
  }

  /**
   * Adds tracked data to their respective maps and increments the day.
   *
   * @param census the total census of the game
   */
  public void updateData(Census census) {
    day++;
    populationOverTime.put(day, census);
    notifyObservers();
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

  @Override
  public boolean checkCondition(Condition condition) throws PopulationNotFoundException {
    if (condition.getType() == Conditionals.POPULATION) {
      return condition.check(getCensusAt(getDay()).getPopulation(condition.getName()));
    }
    return condition.check(getDay());
  }

  @Override
  public double getProgress(String s) throws PopulationNotFoundException {
    return getCensusAt(getDay()).getPopulation(s);
  }

  @Override
  public double getProgress() {
    return getDay();
  }

  public void updateDay() {
    day++;
  }
}

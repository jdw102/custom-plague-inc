package ooga.model.gamestate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.util.Observable;
import ooga.util.Observer;

/**
 * Tracks the state of the game including by checking the end conditions and updating the game
 * data.
 */

public class GameState implements Observable {

  private final EndConditions winConditions;
  private final EndConditions loseConditions;
  private final GameData gameData;
  private final Map<Enum, Progressor> conditionCheckerMap;
  private final Collection<Observer> observers;
  private String endMessage;
  private boolean running;


  public GameState(GameData gameData, EndConditions winConditions,
      EndConditions loseConditions) {
    this.gameData = gameData;
    this.winConditions = winConditions;
    this.loseConditions = loseConditions;
    conditionCheckerMap = new HashMap<>();
    this.running = false;
    observers = new ArrayList<>();
  }

  public void addProgressor(Progressor progressor, Enum type) {
    conditionCheckerMap.put(type, progressor);
  }

  public void update(RegionMap regionMap)
      throws PopulationNotFoundException {
    Census census = regionMap.getTotalCensus();
    gameData.updateData(census);
    if (checkConditions(winConditions) || checkConditions(loseConditions)) {
      running = false;
    }
  }

  protected boolean checkConditions(EndConditions conditions) throws PopulationNotFoundException {
    while (conditions.hasNext()) {
      Condition condition = conditions.next();
      if (!conditionCheckerMap.containsKey(condition.getType())) {
        continue;
      }
      boolean satisfied = conditionCheckerMap.get(condition.getType())
          .checkCondition(condition);
      if (satisfied) {
        triggerNotification(condition.getMessage());
        return true;
      }
    }
    return false;
  }

  private void triggerNotification(String message) {
    endMessage = message;
    toggleRunning();
    notifyObservers();
  }

  public String getEndMessage() {
    return endMessage;
  }

  public boolean isRunning() {
    return running;
  }

  public void toggleRunning() {
    running = !running;
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
}



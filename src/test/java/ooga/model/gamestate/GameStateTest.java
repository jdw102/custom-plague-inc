package ooga.model.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.GreaterThanOperation;
import ooga.util.LessThanOperation;
import ooga.model.region.Census;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionPaths;
import ooga.model.region.RegionState;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;

public class GameStateTest {

  //note from eloise 12/10: this test is so broken now, am commenting out but should be updated by whoever knows how to do that later


  GameState gameState;
  RegionMap regionMap;
  RegionPaths regionPaths;
  EndConditions winningConditions;
  EndConditions losingConditions;
  UserPoints userPoints;
  GameData gameData;
  Protagonist protagonist;
  Antagonist antagonist;
  RegionModel r1;
  String winMessage;
  String loseMessage;

  public GameStateTest() {
    winMessage = "You have won!";
    loseMessage = "You have lost...";
    Census c1 = createTestCensus("Pop1", 1000, "Pop2", 2000);
    Census c2 = createTestCensus("Pop1", 500, "Pop2", 5000);
    Census c3 = createTestCensus("Pop1", 8000, "Pop2", 0);

    RegionState initState1 = new RegionState(c1, true, false);
    RegionState initState2 = new RegionState(c2, true, false);
    RegionState initState3 = new RegionState(c3, true, false);

    r1 = new RegionModel(1, initState1, null, 0.5, 0.5, 0.0);
    RegionModel r2 = new RegionModel(2, initState2, null, 0.5, 0.5, 0.0);
    RegionModel r3 = new RegionModel(3, initState3, null, 0.5, 0.5, 0.0);

    regionMap = new RegionMap(new Census());
    regionMap.addRegion(r1);
    regionMap.addRegion(r2);
    regionMap.addRegion(r3);

    regionPaths = new RegionPaths();
    userPoints = new DefaultUserPoints(0.0, 100);
    gameData = new GameData(1);
    winningConditions = new EndConditions();
    losingConditions = new EndConditions();
    Modifiers pMods = new Modifiers("Core");
    Modifiers aMods = new Modifiers("Wealth");
    pMods.addModifier("Infectivity", 0.06);
    pMods.addModifier("Lethality", 0.4);
    aMods.addModifier("Rich", 0.05);
    aMods.addModifier("Poor", 0.1);
    protagonist = new Protagonist(0);
    protagonist.addModifiers(pMods);
    antagonist = new Antagonist(0.0, new GreaterThanOperation());
    antagonist.addTrackedPopulation("Default");
    antagonist.addModifiers(aMods);
  }

  private class TestDataObserver implements Observer {

    double day;
    GameData gameData;

    private TestDataObserver(GameData gameData) {
      this.gameData = gameData;
      day = gameData.getDay();
    }

    @Override
    public void update() {
      day = gameData.getDay();
    }

    public double getDay() {
      return day;
    }
  }

  private class TestStateObserver implements Observer {

    String message;
    GameState gameState;

    private TestStateObserver(GameState gameState) {
      this.gameState = gameState;
    }

    @Override
    public void update() {
      message = gameState.getEndMessage();
    }

    public String getMessage() {
      return message;
    }
  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
      int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  @Test
  void testDayWinningCondition() throws PopulationNotFoundException {
    Condition dayWinCondition = new Condition(2, new GreaterThanOperation(), Conditionals.TIME, "Day", winMessage);
    winningConditions.addCondition(dayWinCondition);
    //gameState = new DefaultGameState(regionMap, userPoints, regionPaths, gameData,winningConditions, losingConditions);
    gameState = new GameState(gameData, winningConditions, losingConditions);
    gameState.addProgressor(gameData, Conditionals.TIME);
    gameState.update(regionMap);
    assertEquals("You have won!", gameState.getEndMessage());
  }


  @Test
  void testPointsWinningCondition() throws PopulationNotFoundException {
    Condition winCondition = new Condition(20, new GreaterThanOperation(), Conditionals.POINTS, "", winMessage);
    winningConditions.addCondition(winCondition);
    gameState = new GameState(gameData, winningConditions, losingConditions);
    gameState.addProgressor(userPoints, Conditionals.POINTS);
    userPoints.adjustPoints(30);
    gameState.update(regionMap);
    assertEquals("You have won!", gameState.getEndMessage());
  }

  @Test
  void testDayPopulationLosingCondition() throws PopulationNotFoundException {
    Condition loseCondition = new Condition(10000, new GreaterThanOperation(), Conditionals.POPULATION, "Pop1", loseMessage);
    losingConditions.addCondition(loseCondition);
    gameState = new GameState(gameData, winningConditions, losingConditions);
    gameState.addProgressor(gameData, Conditionals.POPULATION);
    Census changeCensus = new Census();
    changeCensus.addPopulation("Pop1", 500);
    changeCensus.addPopulation("Pop2", 0);
    Census newCensus = r1.getCurrentState().census().getCopy();
    newCensus.add(changeCensus);
    r1.setNextState(
        new RegionState(newCensus, r1.isOpen(), r1.hasAntagonist()));
    r1.updateState();
    gameState.update(regionMap);
    assertEquals("You have lost...", gameState.getEndMessage());
  }

  @Test
  void testAntagonistWinningCondition() throws PopulationNotFoundException {
    Condition winCondition = new Condition(0.3, new LessThanOperation(), Conditionals.ANTAGONIST, "", winMessage);
    winningConditions.addCondition(winCondition);
    gameState = new GameState(gameData, winningConditions, losingConditions);
    gameState.addProgressor(antagonist, Conditionals.ANTAGONIST);
    gameState.update(regionMap);
    assertEquals("You have won!", gameState.getEndMessage());
  }

  @Test
  void testUserPointsUpdates() {
    double expected = 5;
    gameState = new GameState(gameData, winningConditions, losingConditions);
    userPoints.adjustPoints(5);
    assertEquals(expected, userPoints.getPoints());
  }

  @Test
  void testGameStatePointsUpdates() {
    double expected = 30;
    gameState = new GameState(gameData, winningConditions, losingConditions);
    userPoints.adjustPoints(expected);
    assertEquals(expected, userPoints.getPoints());
  }

  @Test
  void testGameStatePopulationUpdates() throws PopulationNotFoundException {
    double expectedTotal = 17000;
    double expected1 = 10000;
    gameState = new GameState(gameData, winningConditions, losingConditions);
    Census changeCensus = new Census();
    changeCensus.addPopulation("Pop1", 500);
    changeCensus.addPopulation("Pop2", 0);
    Census newCensus = r1.getCurrentState().census().getCopy();
    newCensus.add(changeCensus);
    r1.setNextState(
        new RegionState(newCensus, r1.isOpen(), r1.hasAntagonist()));
    r1.updateState();
    gameState.update(regionMap);
    assertEquals(expectedTotal, gameData.getCensusAt(gameData.getDay()).getTotal());
    assertEquals(expected1, gameData.getCensusAt(gameData.getDay()).getPopulation("Pop1"));
  }

  @Test
  void testStateObserverUpdates() throws PopulationNotFoundException {
    Condition loseCondition = new Condition(10000, new GreaterThanOperation(), Conditionals.POPULATION, "Pop1", loseMessage);
    losingConditions.addCondition(loseCondition);
    gameState = new GameState(gameData, winningConditions, losingConditions);
    gameState.addProgressor(gameData, Conditionals.POPULATION);
    TestStateObserver observer = new TestStateObserver(gameState);
    gameState.addObserver(observer);
    Census changeCensus = new Census();
    changeCensus.addPopulation("Pop1", 500);
    changeCensus.addPopulation("Pop2", 0);
    Census newCensus = r1.getCurrentState().census().getCopy();
    newCensus.add(changeCensus);
    r1.setNextState(
        new RegionState(newCensus, r1.isOpen(), r1.hasAntagonist()));
    r1.updateState();
    gameState.update(regionMap);
    assertEquals("You have lost...", observer.getMessage());
  }

}

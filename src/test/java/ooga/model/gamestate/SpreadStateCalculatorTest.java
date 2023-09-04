package ooga.model.gamestate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ooga.Main;
import ooga.model.actor.Antagonist;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.calculators.DefaultSpreadCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.region.Census;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.util.GreaterThanOperation;
import ooga.util.NormalOperation;
import org.junit.jupiter.api.Test;

public class SpreadStateCalculatorTest {

  SpreadCalculator spreadCalculator;

  Protagonist protagonist;

  Antagonist antagonist;

  PathModel path;

  public SpreadStateCalculatorTest() {
    SpreadablePopulation spreadablePopulation = new SpreadablePopulation("Infected", "Healthy",
        new NormalOperation());
    spreadCalculator = new DefaultSpreadCalculator();
    spreadCalculator.addSpreadablePopulation(spreadablePopulation);
    Census c1 = createTestCensus("Infected", 1000, "Dead", 200);
    c1.addPopulation("Healthy", 2000);
    RegionState initState1 = new RegionState(c1, true, false);
    RegionModel source = new RegionModel(1, initState1, null, 0.5, 0.5, 1000);

    Census c2 = createTestCensus("Infected", 0, "Dead", 0);
    c2.addPopulation("Healthy", 1000);
    RegionState initState2 = new RegionState(c1, true, false);
    RegionModel sink = new RegionModel(1, initState2, null, 0.5, 0.5, 1000);

    path = new PathModel(source, sink, "Land", null, 30);

    Modifiers coreModifiers = new Modifiers("Core");
    coreModifiers.addModifier("Infectivity", 0.4);
    coreModifiers.addModifier("Lethality", 0.3);
    coreModifiers.addModifier("Severity", 0.2);
    Modifiers pathModifiers = new Modifiers("Path");
    pathModifiers.addModifier("Land", 1.5);

    protagonist = new Protagonist(0);
    protagonist.addModifiers(coreModifiers);
    protagonist.addModifiers(pathModifiers);
    antagonist = new Antagonist(0.0, new GreaterThanOperation());
    antagonist.addTrackedPopulation("Infected");



  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
                                  int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  @Test
  void testGenerateTransferCensus() throws PopulationNotFoundException {
    Main.GLOBAL_RNG.setSeed(0);
    Census transferCensus = spreadCalculator.generateTransferCensus(path, protagonist, antagonist);
    assertEquals(11, transferCensus.getPopulation("Infected"));
    assertEquals(19, transferCensus.getPopulation("Healthy"));
    assertEquals(0, transferCensus.getPopulation("Dead"));
  }









}


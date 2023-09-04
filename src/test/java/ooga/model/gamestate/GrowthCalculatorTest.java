package ooga.model.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.TargetPopulation;
import ooga.model.gamestate.statehandler.calculators.DefaultGrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.factors.CoreFactor;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.model.gamestate.statehandler.factors.RegionFactor;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.util.GreaterThanOperation;
import ooga.util.MultiplyOperation;
import ooga.util.NormalOperation;
import org.junit.jupiter.api.Test;

public class GrowthCalculatorTest {

  GrowthCalculator stateCalculator;
  RegionModel region;

  RegionMap regions;
  Protagonist protagonist;
  Antagonist antagonist;

  public GrowthCalculatorTest() {
    Census c1 = createTestCensus("Infected", 1000, "Dead", 200);
    c1.addPopulation("Healthy", 2000);
    RegionState initState1 = new RegionState(c1, true, false);
    region = new RegionModel(1, initState1, null, 0.5, 0.5, 1000);
    region.addFactor("Climate", "Hot");
    protagonist = new Protagonist(0);
    Modifiers climateModifiers = new Modifiers("Climate");
    climateModifiers.addModifier("Hot", 0.8);
    climateModifiers.addModifier("Cold", 0.9);
    Modifiers coreModifiers = new Modifiers("Core");
    coreModifiers.addModifier("Infectivity", 0.4);
    coreModifiers.addModifier("Lethality", 0.3);
    coreModifiers.addModifier("Severity", 0.2);
    protagonist.addModifiers(climateModifiers);
    protagonist.addModifiers(coreModifiers);
    antagonist = new Antagonist(0.0, new GreaterThanOperation());
    antagonist.addTrackedPopulation("Infected");
    antagonist.addModifiers(climateModifiers);
    FactorCollection growthFactors = new FactorCollection(new MultiplyOperation());
    growthFactors.addFactor(new RegionFactor("Climate", new NormalOperation()));
    growthFactors.addFactor(new CoreFactor("Infectivity", new NormalOperation()));
    FactorCollection drainFactors = new FactorCollection(new MultiplyOperation());
    drainFactors.addFactor(new CoreFactor("Lethality", new NormalOperation()));
    TargetPopulation infected = new TargetPopulation("Infected", "Healthy",  growthFactors, new FactorCollection(new MultiplyOperation()));
    stateCalculator = new DefaultGrowthCalculator();
    stateCalculator.addTargetPopulation(infected);
    regions = new RegionMap(region.getCurrentState().census());
    regions.addRegion(region);
  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
      int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  @Test
  void testCorrectGrowth()
      throws ModifierNotFoundException, FactorNotFoundException, PopulationNotFoundException {

    RegionState nextState = stateCalculator.determineNextState(region, protagonist, antagonist);
    assertEquals(1321, nextState.census().getPopulation("Infected"));
    assertEquals(200, nextState.census().getPopulation("Dead"));
    assertEquals(1679, nextState.census().getPopulation("Healthy"));
    assertEquals(3200, nextState.census().getTotal());
  }

  @Test
  void testAntagonistThresholdReached()
      throws ModifierNotFoundException, FactorNotFoundException, PopulationNotFoundException {
    boolean expected = true;
    for (int i = 0; i < 3; i++) {
      region.setNextState(stateCalculator.determineNextState(region, protagonist, antagonist));
      region.updateState();
    }
    assertEquals(expected, region.hasAntagonist());
  }

  @Test
  void testCloseThresholdReached()
      throws ModifierNotFoundException, FactorNotFoundException, PopulationNotFoundException {
    boolean expected = true;
    for (int i = 0; i < 3; i++) {
      region.setNextState(stateCalculator.determineNextState(region, protagonist, antagonist));
      region.updateState();
    }
    assertEquals(expected, region.hasAntagonist());
  }

  @Test
  void testAntagonistProgress()
          throws ModifierNotFoundException, FactorNotFoundException, PopulationNotFoundException {
    for (int i = 0; i < 3; i++) {
      region.setNextState(stateCalculator.determineNextState(region, protagonist, antagonist));
      region.updateState();
    }
    assertEquals(800, stateCalculator.calculateAntagonistProgress(regions,antagonist));

  }
}

package ooga.model.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ooga.Main;
import ooga.controller.parsers.DataFileParser;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.statehandler.PointsParameters;
import ooga.model.gamestate.statehandler.calculators.DefaultPointsCalculator;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.model.gamestate.statehandler.factors.RegionFactor;
import ooga.model.region.Census;
import ooga.model.region.FactorNotFoundException;
import ooga.model.region.PathActivityMap;
import ooga.model.region.PathModel;
import ooga.model.region.PathPointsMap;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.util.DefaultPointsOperation;
import ooga.util.MultiplyOperation;
import ooga.util.NormalOperation;
import org.junit.jupiter.api.Test;

public class PointsCalculatorTest {

  PointsCalculator pointsCalculator;

  GrowthCalculator growthCalculator;

  Protagonist protagonist;

  Antagonist antagonist;

  PathModel path;

  RegionModel region;

  public PointsCalculatorTest() throws ModifierNotFoundException {
    FactorCollection growthFactors = new FactorCollection(new MultiplyOperation());
    FactorCollection spreadFactors = new FactorCollection(new MultiplyOperation());
    PointsParameters pointsParameters = new PointsParameters(growthFactors, spreadFactors,
        new DefaultPointsOperation());
    pointsParameters.addPopulationParameter("Infected");
    pointsCalculator = new DefaultPointsCalculator(pointsParameters);

    GrowthCalculatorTest growthCalculatorTest = new GrowthCalculatorTest();
    growthCalculator = growthCalculatorTest.stateCalculator;

    region = growthCalculatorTest.region;

    Census c1 = createTestCensus("Infected", 0, "Dead", 0);
    c1.addPopulation("Healthy", 1000);
    RegionState initState1 = new RegionState(c1, true, false);
    RegionModel sink = new RegionModel(1, initState1, null, 0.5, 0.5, 1000);
    Census c2 = createTestCensus("Infected", 1, "Dead", 0);
    c2.addPopulation("Healthy", 999);
    RegionState initState2 = new RegionState(c2, true, false);
    sink.setNextState(initState2);

    PathPointsMap pathPointsMap = new PathPointsMap();
    pathPointsMap.addPathPoints("Infected", 5);
    path = new PathModel(region, sink, "Land", pathPointsMap, 30);

    protagonist = growthCalculatorTest.protagonist;
    antagonist = growthCalculatorTest.antagonist;

    protagonist.adjustModifier("Core", "Infectivity", 2);

  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
      int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  @Test
  void testGrowthPoints()
      throws PopulationNotFoundException, FactorNotFoundException, ModifierNotFoundException {
    Main.GLOBAL_RNG.setSeed(0);
    region.setNextState(growthCalculator.determineNextState(region, protagonist, antagonist));
    int points = pointsCalculator.calculateGrowthPoints(region, protagonist);
    assertEquals(2, points);
  }

  @Test
  void testSpreadPoints()
      throws PopulationNotFoundException, FactorNotFoundException, ModifierNotFoundException {
    int points = pointsCalculator.calculateSpreadPoints(path, protagonist);
    assertEquals(5, points);
  }


  @Test
  void testProducesPoint()
      throws PopulationNotFoundException, ModifierNotFoundException, FactorNotFoundException {
    Census census = new Census();
    census.addPopulation("Pop1", 10);
    FactorCollection growthFactors = new FactorCollection(new MultiplyOperation());
    growthFactors.addFactor(new RegionFactor("Climate", new NormalOperation()));
    Census nextCensus = census.getCopy();
    nextCensus.adjustPopulation("Pop1", 15);
    Protagonist protagonist1 = new Protagonist(0);
    Modifiers modifiers = new Modifiers("Climate");
    modifiers.addModifier("Hot", 1.0);
    protagonist1.addModifiers(modifiers);
    RegionModel region1 = new RegionModel(2, new RegionState(census, true, false),
        new PathActivityMap(), 0.0, 0.0, 0.0);
    region1.setNextState(new RegionState(nextCensus, true, false));
    region1.addFactor("Climate", "Hot");
    PointsParameters pointsParameters = new PointsParameters(growthFactors,
        new FactorCollection(new MultiplyOperation()), new DefaultPointsOperation());
    pointsParameters.addPopulationParameter("Pop1");
    PointsCalculator pointsCalculator1 = new DefaultPointsCalculator(pointsParameters);
    assertNotEquals(0, pointsCalculator1.calculateGrowthPoints(region1, protagonist1));
  }


}


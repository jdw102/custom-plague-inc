package ooga.controller.factories;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.math.PointsParametersJSON;
import ooga.controller.parsers.math.SpreadablePopulationsJSON;
import ooga.controller.parsers.math.SubPopulationData;
import ooga.controller.parsers.math.TargetPopulationsJSON;
import ooga.model.gamestate.DefaultUserPoints;
import ooga.model.gamestate.UserPoints;
import ooga.model.gamestate.statehandler.PointsParameters;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.gamestate.statehandler.TargetPopulation;
import ooga.model.gamestate.statehandler.calculators.DefaultGrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.DefaultPointsCalculator;
import ooga.model.gamestate.statehandler.calculators.DefaultSpreadCalculator;
import ooga.model.gamestate.statehandler.calculators.GrowthCalculator;
import ooga.model.gamestate.statehandler.calculators.PointsCalculator;
import ooga.model.gamestate.statehandler.calculators.SpreadCalculator;
import ooga.model.region.Census;

public class CalculatorFactory {

  private final TargetPopulationsJSON targetPopulationsJSON;
  private final SpreadablePopulationsJSON spreadablePopulationsJSON;
  private final PointsParametersJSON pointsParametersJSON;

  public CalculatorFactory(JsonNode growthNode, JsonNode spreadNode, JsonNode pointsNode)
      throws InvalidOperationException, InvalidFactorException {
    targetPopulationsJSON = new TargetPopulationsJSON(growthNode);
    spreadablePopulationsJSON = new SpreadablePopulationsJSON(spreadNode);
    pointsParametersJSON = new PointsParametersJSON(pointsNode);
    spreadablePopulationsJSON.parseAllData();
    targetPopulationsJSON.parseAllData();
    pointsParametersJSON.parseAllData();

  }

  public GrowthCalculator createGrowthStateCalculator() {
    GrowthCalculator growthCalculator = new DefaultGrowthCalculator();
    Iterator<TargetPopulation> iterator = targetPopulationsJSON.targetPopulationIterator();
    while (iterator.hasNext()) {
      growthCalculator.addTargetPopulation(iterator.next());
    }
    return growthCalculator;
  }

  public SpreadCalculator createSpreadStateCalculator() {
    SpreadCalculator spreadCalculator = new DefaultSpreadCalculator();
    Iterator<SpreadablePopulation> iterator = spreadablePopulationsJSON.spreadablePopulationIterator();
//    startCensus = spreadablePopulationsJSON.getStartCensus();
    while (iterator.hasNext()) {
      spreadCalculator.addSpreadablePopulation(iterator.next());
    }
    return spreadCalculator;
  }

  public PointsCalculator createPointsCalculator() {
    PointsParameters pointsParameters = pointsParametersJSON.getPointsParameters();
    return new DefaultPointsCalculator(pointsParameters);
  }

  public UserPoints createUserPoints() {
    UserPoints userPoints = new DefaultUserPoints(
        pointsParametersJSON.getStartPoints(), pointsParametersJSON.getMaxPoints());
    return userPoints;
  }

  public SubPopulationData getSubPopulationData() {
    return targetPopulationsJSON.getSubPopulationData();
  }

  public Census createStartCensus() {
    return spreadablePopulationsJSON.getStartCensus();
  }
}

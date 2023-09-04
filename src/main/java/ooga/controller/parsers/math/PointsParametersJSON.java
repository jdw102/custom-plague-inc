package ooga.controller.parsers.math;

import com.fasterxml.jackson.databind.JsonNode;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.model.gamestate.statehandler.PointsParameters;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.util.BiOperation;

/**
 * A class that parses the points json config file and creates a PointsParameters instance for use
 * in the PointsCalculator and reads in the starting and maximum user points.
 */
public class PointsParametersJSON extends DataFileParser {

  private PointsParameters pointsParameters;
  private double maxPoints;
  private double startPoints;

  public PointsParametersJSON(JsonNode node) {
    super(node);
  }

  @Override
  public void parseAllData() throws InvalidOperationException, InvalidFactorException {
    JsonNode pointsParametersNode = getTopNode().get("pointsParameters");
    maxPoints = ParserUtils.getDoubleValue(pointsParametersNode, "maxPoints");
    startPoints = ParserUtils.getDoubleValue(pointsParametersNode, "startPoints");
    String pointsOperationName = ParserUtils.getNonBlankString(pointsParametersNode,
        "pointsOperation");
    BiOperation operation = makeBiOperation(pointsOperationName);
    String growthOperationName = ParserUtils.getNonBlankString(pointsParametersNode,
        "growthFactorsOperation");
    String spreadOperationName = ParserUtils.getNonBlankString(pointsParametersNode,
        "spreadFactorsOperation");
    JsonNode growthFactorsNode = pointsParametersNode.get("growthFactors");
    JsonNode spreadFactorsNode = pointsParametersNode.get("spreadFactors");
    FactorCollection growthFactors = makeFactorCollection(growthOperationName, growthFactorsNode);
    FactorCollection spreadFactors = makeFactorCollection(spreadOperationName, spreadFactorsNode);
    pointsParameters = new PointsParameters(growthFactors, spreadFactors, operation);
    for (JsonNode popNode : pointsParametersNode.get("populations")) {
      pointsParameters.addPopulationParameter(popNode.asText());
    }
  }


  public PointsParameters getPointsParameters() {
    return pointsParameters;
  }

  public double getMaxPoints() {
    return maxPoints;
  }

  public double getStartPoints() {
    return startPoints;
  }
}

package ooga.controller.parsers.antagonist;


import com.fasterxml.jackson.databind.JsonNode;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.model.actor.Antagonist;
import ooga.util.ConditionalOperation;

/**
 * A class that parses the antagonist json file and creates an antagonist.
 */
public class AntagonistJSON extends DataFileParser {

  Antagonist antagonist;

  public AntagonistJSON(JsonNode node) {
    super(node);
  }

  @Override
  public void parseAllData() throws InvalidOperationException {
    JsonNode antagonistNode = getTopNode().get("antagonist");
    double startingProgress = ParserUtils.getDoubleValue(antagonistNode, "startingProgress");
    String operationName = ParserUtils.getNonBlankString(antagonistNode, "thresholdOperation");
    ConditionalOperation operation = makeConditionalOperation(operationName);
    antagonist = new Antagonist(startingProgress, operation);
    for (JsonNode popNode : antagonistNode.get("trackedPopulations")) {
      antagonist.addTrackedPopulation(popNode.asText());
    }
  }

  public Antagonist getAntagonist() {
    return antagonist;
  }
}

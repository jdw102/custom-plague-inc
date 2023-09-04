package ooga.controller.parsers.end_conditions;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.model.gamestate.Condition;
import ooga.model.gamestate.Conditionals;
import ooga.model.gamestate.EndConditions;
import ooga.util.ConditionalOperation;

/**
 * A class that parses the win and lose conditions json files and creates an EndConditions
 * instance.
 */

public class EndConditionsJSON extends DataFileParser {

  private static final String OPERATION_PACKAGE = "ooga.util.";
  private static final String DEFAULT_TYPE = "POINTS";
  private EndConditions endConditions;

  public EndConditionsJSON(JsonNode topNode) {
    super(topNode);
  }


  @Override
  public void parseAllData() throws InvalidConditionException {
    JsonNode conditionsNode = getTopNode().get("conditions");
    setTopNode(conditionsNode);
    endConditions = new EndConditions();
    for (JsonNode conditionNode : conditionsNode) {
      double threshold = ParserUtils.getDoubleValue(conditionNode, "threshold");
      Class<?> clazz;
      try {
        String name = ParserUtils.getNonBlankString(conditionNode, "operation");
        clazz = Class.forName(String.format("%s%s", OPERATION_PACKAGE, name));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      ConditionalOperation conditional = null;
      try {
        conditional = (ConditionalOperation) clazz.getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
      String attemptType = ParserUtils.getNonBlankString(conditionNode, "type");
      Enum type = Conditionals.valueOf(DEFAULT_TYPE);
      try {
        type = Conditionals.valueOf(attemptType);
      } catch (IllegalArgumentException e) {
        throw new InvalidConditionException(attemptType);
      }
      String name = ParserUtils.getNonBlankString(conditionNode, "name");
      String message = ParserUtils.getNonBlankString(conditionNode, "message");
      Condition condition = new Condition(threshold, conditional, type, name, message);
      endConditions.addCondition(condition);
    }
  }

  public EndConditions getEndConditions() {
    return endConditions;
  }
}

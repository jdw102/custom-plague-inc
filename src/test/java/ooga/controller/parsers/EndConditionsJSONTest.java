package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import ooga.controller.parsers.end_conditions.EndConditionsJSON;
import ooga.controller.parsers.end_conditions.InvalidConditionException;
import ooga.model.gamestate.Condition;
import ooga.model.gamestate.Conditionals;
import ooga.model.gamestate.EndConditions;
import org.junit.jupiter.api.Test;

public class EndConditionsJSONTest {
  EndConditionsJSON endConditionsJSON;
  GameConfigParser gameConfigParser;
  JsonNode topNode;
  public EndConditionsJSONTest(){
    File f = new File(getClass().getResource("/json_tests/endconditionstest.json").getPath());
    gameConfigParser = new GameConfigParser();
    topNode = gameConfigParser.parseJSONConfig(f);
    endConditionsJSON = new EndConditionsJSON(topNode);
  }

  @Test
  void testConditionsMade() throws InvalidConditionException {
    endConditionsJSON.parseAllData();
    EndConditions endConditions = endConditionsJSON.getEndConditions();
    while (endConditions.hasNext()){
      Condition condition = endConditions.next();
      assertEquals(Conditionals.POPULATION, condition.getType());
      assertEquals("Dead", condition.getName());
      assertEquals("YOU HAVE SUCCESSFULLY KILLED EVERYONE ON THE PLANET!", condition.getMessage());
      assertEquals(true, condition.check(9000000000.0));
      assertEquals(false, condition.check(0));
    }
  }

  @Test
  void testThrowsInvalidCondition() {
    File f = new File(getClass().getResource("/json_tests/failedconditions.json").getPath());
    JsonNode node = gameConfigParser.parseJSONConfig(f);
    EndConditionsJSON endConditionsJSON1 = new EndConditionsJSON(node);
    assertThrows(InvalidConditionException.class, () -> endConditionsJSON1.parseAllData());
  }
}

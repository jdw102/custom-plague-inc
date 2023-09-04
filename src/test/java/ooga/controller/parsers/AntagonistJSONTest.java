package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import ooga.controller.parsers.antagonist.AntagonistJSON;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.math.SpreadablePopulationsJSON;
import org.junit.jupiter.api.Test;

public class AntagonistJSONTest {
  AntagonistJSON antagonistJSON;


  public AntagonistJSONTest() throws InvalidOperationException {
    File f = new File(getClass().getResource("/json_tests/antagonisttest.json").getPath());
    GameConfigParser gameConfigParser = new GameConfigParser();
    antagonistJSON = new AntagonistJSON(gameConfigParser.parseJSONConfig(f));
    antagonistJSON.parseAllData();
  }

  @Test
  void testParsing(){
    assertEquals(0.0, antagonistJSON.getAntagonist().getAmount());
  }

}

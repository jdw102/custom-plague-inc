package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Iterator;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.math.PointsParametersJSON;
import ooga.controller.parsers.math.SpreadablePopulationsJSON;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import org.junit.jupiter.api.Test;

public class SpreadJSONTest {
 GameConfigParser gameConfigParser;
 SpreadablePopulationsJSON spreadablePopulationsJSON;

  public SpreadJSONTest() throws InvalidOperationException {
    File f = new File(getClass().getResource("/json_tests/spreadtest.json").getPath());
    gameConfigParser = new GameConfigParser();
    spreadablePopulationsJSON = new SpreadablePopulationsJSON(gameConfigParser.parseJSONConfig(f));
    spreadablePopulationsJSON.parseAllData();
  }

  @Test
  void testParsing() throws PopulationNotFoundException {
    Iterator<SpreadablePopulation> iterator = spreadablePopulationsJSON.spreadablePopulationIterator();
    while (iterator.hasNext()){
      assertEquals("Infected" ,iterator.next().getName());
    }
    Census census = spreadablePopulationsJSON.getStartCensus();
    assertEquals(1, census.getPopulation("Infected"));
  }
}

package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.math.TargetPopulationsJSON;
import ooga.model.gamestate.statehandler.DrainPopulation;
import ooga.model.gamestate.statehandler.TargetPopulation;
import org.junit.jupiter.api.Test;

public class GrowthJSONTest {
  TargetPopulationsJSON targetPopulationsJSON;
  GameConfigParser gameConfigParser;

  public GrowthJSONTest() throws InvalidFactorException, InvalidOperationException {
    File f = new File(getClass().getResource("/json_tests/growthtest.json").getPath());
    gameConfigParser = new GameConfigParser();
    targetPopulationsJSON = new TargetPopulationsJSON(gameConfigParser.parseJSONConfig(f));
    targetPopulationsJSON.parseAllData();
  }

  @Test
  void testParsing(){
    Iterator<TargetPopulation> iterator = targetPopulationsJSON.targetPopulationIterator();
    while (iterator.hasNext()){
      TargetPopulation targetPopulation = iterator.next();
      assertEquals("Infected", targetPopulation.getName());
      Iterator<DrainPopulation> iterator1 = targetPopulation.getDrainPopulationIterator();
      while (iterator1.hasNext()){
        assertEquals("Dead", iterator1.next().getName());
      }
    }
  }

  @Test
  void testThrowsInvalidFactor(){
    File f = new File(getClass().getResource("/json_tests/thrownfactortest.json").getPath());
    targetPopulationsJSON = new TargetPopulationsJSON(gameConfigParser.parseJSONConfig(f));
    assertThrows(InvalidFactorException.class, () -> targetPopulationsJSON.parseAllData());
  }

  @Test
  void testThrowsInvalidOperation(){
    File f = new File(getClass().getResource("/json_tests/thrownoperationtest.json").getPath());
    targetPopulationsJSON = new TargetPopulationsJSON(gameConfigParser.parseJSONConfig(f));
    assertThrows(InvalidOperationException.class, () -> targetPopulationsJSON.parseAllData());
  }
}

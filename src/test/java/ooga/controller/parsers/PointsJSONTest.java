package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.math.PointsParametersJSON;
import ooga.controller.parsers.math.TargetPopulationsJSON;
import ooga.model.gamestate.statehandler.PointsParameters;
import org.junit.jupiter.api.Test;

public class PointsJSONTest {
  PointsParametersJSON pointsParametersJSON;
  GameConfigParser gameConfigParser;
  PointsParameters pointsParameters;

  public PointsJSONTest() throws InvalidFactorException, InvalidOperationException {
    File f = new File(getClass().getResource("/json_tests/pointstest.json").getPath());
    gameConfigParser = new GameConfigParser();
    pointsParametersJSON = new PointsParametersJSON(gameConfigParser.parseJSONConfig(f));
    pointsParametersJSON.parseAllData();
    pointsParameters = pointsParametersJSON.getPointsParameters();
  }

  @Test
  void testPopulations(){
    List<String> list = new ArrayList<>();
    while (pointsParameters.hasNext()){
      list.add(pointsParameters.next());
    }
    assertEquals("Infected", list.get(0));
    assertEquals("Dead", list.get(1));
  }

  @Test
  void testPoints(){
    assertEquals(100, pointsParametersJSON.getMaxPoints());
    assertEquals(0, pointsParametersJSON.getStartPoints());
  }

}

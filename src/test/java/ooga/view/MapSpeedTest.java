package ooga.view;

import ooga.view.map.MapReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class MapSpeedTest {
  private MapReader mapReader;

  public MapSpeedTest(){
    mapReader = new MapReader("Plague");
  }

  @Test
  @Timeout(3)
  void testSpeed(){
    mapReader.getRegionShapeMap();
  }

}

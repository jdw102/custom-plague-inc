package ooga.controller.parsers.paths;

import java.util.HashMap;
import java.util.Map;
import ooga.util.Coordinate;

public class RegionPortCoordinateMap {

  private final Map<Integer, Map<String, Coordinate>> map;

  public RegionPortCoordinateMap() {
    map = new HashMap<>();
  }

  public void addPortCoordinate(int id, String type, Coordinate coordinate) {
    map.putIfAbsent(id, new HashMap<>());
    Map<String, Coordinate> portMap = map.get(id);
    portMap.put(type, coordinate);
    map.put(id, portMap);
  }

  public Coordinate getPortCoordinate(int id, String type) {
    return map.get(id).get(type);
  }

  public String getformattedstring() {
    StringBuilder sb = new StringBuilder("RegionPortCoordinateMap:");
    for (int i : map.keySet()) {
      sb.append("\n\t- ").append(i);
      for (String s : map.get(i).keySet()) {
        sb.append(
            String.format("\n\t\t- %s: %s, %s", s, map.get(i).get(s).x(), map.get(i).get(s).y()));
      }
    }
    return sb.toString();
  }
}

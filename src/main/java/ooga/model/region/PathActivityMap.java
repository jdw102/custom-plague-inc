package ooga.model.region;

import java.util.HashMap;
import java.util.Map;

/**
 * A data class that represents the activity of each path type within a region model.
 *
 * @author Jerry Worthy
 */
public class PathActivityMap {

  private final Map<String, Double> pathActivityMap;

  public PathActivityMap() {
    pathActivityMap = new HashMap<>();
  }

  public void addPath(String name, Double activity) {
    pathActivityMap.put(name, activity);
  }

  public double getPathActivity(String name) throws PathNotFoundException {
    double activity = 0;
    if (pathActivityMap.get(name) == null) {
      throw new PathNotFoundException(name);
    } else {
      activity = pathActivityMap.get(name);
    }
    return activity;
  }

}

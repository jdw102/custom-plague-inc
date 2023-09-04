package ooga.model.region;

import java.util.HashMap;
import java.util.Map;

/**
 * A data class that contains the number of a points a population is worth when it first enters a
 * destination region in a path model.
 *
 * @author Jerry Worthy
 */
public class PathPointsMap {

  private final Map<String, Double> pathPointsMap;

  public PathPointsMap() {
    pathPointsMap = new HashMap<>();
  }

  public double getPoints(String populationName) {
    double points;
    try {
      points = pathPointsMap.get(populationName);
    } catch (NullPointerException e) {
      points = 0;
    }
    return points;
  }

  public void addPathPoints(String populationName, double points) {
    pathPointsMap.put(populationName, points);
  }
}

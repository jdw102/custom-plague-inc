package ooga.controller.parsers.regions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record RegionRecord(int id, String name, String description,
                           List<RegionPathRecord> paths, Map<String, String> factors,
                           int startingPopulation, double detectionThreshold,
                           double closedThreshold,
                           String color, double baseAntagonistRate) {

  @Override
  public String name() {
    return name;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public double detectionThreshold() {
    return detectionThreshold;
  }

  @Override
  public List<RegionPathRecord> paths() {
    return Collections.unmodifiableList(paths);
  }

  @Override
  public int startingPopulation() {
    return startingPopulation;
  }

  @Override
  public Map<String, String> factors() {
    return Collections.unmodifiableMap(factors);
  }

  @Override
  public String color() {
    return color;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public double closedThreshold() {
    return closedThreshold;
  }

  @Override
  public double baseAntagonistRate() {
    return baseAntagonistRate;
  }

  /**
   * Method to get a specific RegionPathRecord by its id
   *
   * @param pathID
   * @return
   * @throws NullPointerException
   */
  public RegionPathRecord getRegionPathRecord(int pathID) throws NullPointerException {
    for (RegionPathRecord rpr : paths) {
      if (rpr.pathRecord().id() == pathID) {
        return rpr;
      }
    }
    throw new NullPointerException(
        String.format("Region %d does not contain a path record for path ID %d", this.id, pathID));
  }

  /**
   * Method to check if a region allows paths of some id
   *
   * @param pathID
   * @return
   */
  public boolean hasPathOfID(int pathID) {
    for (RegionPathRecord rpr : paths) {
      if (rpr.pathRecord().id() == pathID) {
        return true;
      }
    }
    return false;
  }
}

package ooga.controller.parsers.regions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RegionsData implements Iterable<RegionRecord> {

  private final List<RegionRecord> validRegions;
  private final String defaultPop;
  private final Set<String> otherSubpops;

  public RegionsData(List<RegionRecord> validRegions, String defaultPop, Set<String> subPops) {
    this.validRegions = validRegions;
    this.defaultPop = defaultPop;
    otherSubpops = subPops;
  }

  /**
   * Returns unmodifiable copy of list, and all elements are immutable, so provides data protection.
   * Mostly for testing.
   *
   * @return a List containing all valid regions.
   */
  public List<RegionRecord> getValidRegions() {
    return Collections.unmodifiableList(validRegions);
  }

  /**
   * Returns list of all valid path names. Data in this list is immutable and the returned
   * collection is dispensable so data protection exists.
   *
   * @return a List containing all valid region names.
   */
  public List<String> getValidRegionNames() {
    List<String> validRegionNamesList = new ArrayList<>();
    for (RegionRecord r : validRegions) {
      validRegionNamesList.add(r.name());
    }
    return validRegionNamesList;
  }

  public boolean isValidRegionID(int id) {
    for (RegionRecord rr : getValidRegions()) {
      if (rr.id() == id) {
        return true;
      }
    }
    return false;
  }

  public RegionRecord getRegionByID(int id) throws NullPointerException {
    for (RegionRecord rr : getValidRegions()) {
      if (rr.id() == id) {
        return rr;
      }
    }
    throw new NullPointerException(
        String.format("No Region Record exists for region of ID %d", id));
  }

  @Override
  public Iterator<RegionRecord> iterator() {
    return getValidRegions().iterator();
  }

  public String getDefaultPop() {
    return this.defaultPop;
  }

  /**
   * Returns unmodifiable list of immutable data
   *
   * @return
   */
  public Set<String> getOtherSubpops() {
    return Collections.unmodifiableSet(otherSubpops);
  }

  /**
   * Just gets a nicely formatted string of the data within RegionsData. Used for testing, to delete
   * in the future.
   *
   * @return
   */
  public String getformattedString() {
    StringBuilder sb = new StringBuilder("RegionData:");
    sb.append("\n\tPopulations:\n\t\tDefault pop = " + defaultPop);
    sb.append("\n\t\tOther subpopulations: " + getOtherSubpops());
    sb.append("\n\tRegions:");

    for (RegionRecord r : validRegions) {

      StringBuilder sb2 = new StringBuilder();
      sb2.append(String.format("\n\t\t%s", r.name()));
      sb2.append(String.format("\n\t\t\t- id: %d", r.id()));
      sb2.append(String.format("\n\t\t\t- description: %s", r.description()));

      StringBuilder f = new StringBuilder();
      for (String s : r.factors().keySet()) {
        f.append(String.format("\n\t\t\t\t- %s: %s", s, r.factors().get(s)));
      }
      sb2.append(String.format("\n\t\t\t- Factors: %s", f));

      StringBuilder p = new StringBuilder();
      for (RegionPathRecord pr : r.paths()) {
        p.append(String.format("\n\t\t\t\t- %s: %f", pr.pathRecord().name(), pr.activity()));
        p.append(String.format("\n\t\t\t\t\t - connects to : " + pr.connections()));
      }
      sb2.append(String.format("\n\t\t\t- Paths: %s", p));

      sb2.append(String.format("\n\t\t\t- Pop: %d", r.startingPopulation()));
      sb2.append(String.format("\n\t\t\t- Det Thresh: %f", r.detectionThreshold()));
      sb2.append(String.format("\n\t\t\t- Close Thresh: %f", r.closedThreshold()));
      sb2.append(String.format("\n\t\t\t- Base Antag Contribution: %f", r.baseAntagonistRate()));
      sb2.append(String.format("\n\t\t\t- Color: %s", r.color()));

      sb.append(sb2);
    }
    return sb.toString();
  }
}

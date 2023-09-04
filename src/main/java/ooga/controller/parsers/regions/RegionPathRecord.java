package ooga.controller.parsers.regions;

import java.util.Collections;
import java.util.Set;
import ooga.controller.parsers.paths.PathRecord;

public record RegionPathRecord(int id, Set<Integer> connections, double activity,
                               PathRecord pathRecord) {

  @Override
  public int id() {
    return id;
  }

  @Override
  public double activity() {
    return activity;
  }

  @Override
  public Set<Integer> connections() {
    return Collections.unmodifiableSet(connections);
  }


  @Override
  public PathRecord pathRecord() {
    return pathRecord;
  }
}

package ooga.controller;

import java.util.HashMap;
import java.util.Map;
import ooga.model.actor.Protagonist;

/**
 * Data class used by controller to allow for selection of protagonist.
 */
public class ProtagonistMap {

  private final Map<Integer, Protagonist> protagonistMap;

  public ProtagonistMap() {
    protagonistMap = new HashMap<>();
  }

  public void addProtagonist(Protagonist protagonist) {
    protagonistMap.put(protagonist.getId(), protagonist);
  }

  public Protagonist getProtagonist(int id) {
    return protagonistMap.get(id);
  }

}

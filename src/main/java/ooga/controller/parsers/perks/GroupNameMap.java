package ooga.controller.parsers.perks;

import java.util.HashMap;
import java.util.Map;

public class GroupNameMap {

  private final Map<Integer, String> map;

  public GroupNameMap() {
    map = new HashMap<>();
  }

  public void addGroup(int id, String name) {
    map.put(id, name);
  }

  public String getName(int id) {
    return map.get(id);
  }

}

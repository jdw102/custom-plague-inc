package ooga.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFactorText implements Iterator<String> {

  private final Map<String, String> map;
  private Iterator<String> iterator;

  public RegionFactorText() {
    map = new HashMap<>();
  }

  public void addFactor(String factor, String level) {
    map.put(factor, level);
  }

  public String getFactor(String factor) {
    return map.get(factor);
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = map.keySet().iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = map.keySet().iterator();
    }
    return b;
  }

  @Override
  public String next() {
    return iterator.next();
  }
}

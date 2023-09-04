package ooga.controller.parsers.paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PortSymbolMap implements Iterator<String> {

  private final Map<String, String> portMap;
  private final Map<String, List<String>> typeMap;
  private Iterator<String> iterator;

  public PortSymbolMap() {
    portMap = new HashMap<>();
    typeMap = new HashMap<>();
  }


  public void addPort(String symbol, String imagePath, String type) {
    portMap.put(symbol, imagePath);
    if (!typeMap.containsKey(symbol)) {
      typeMap.put(symbol, new ArrayList<>());
    }
    typeMap.get(symbol).add(type);
  }

  public String getImagePath(String symbol) {
    return portMap.get(symbol);
  }

  public List<String> getType(String symbol) {
    return typeMap.get(symbol);
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = portMap.keySet().iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = portMap.keySet().iterator();
    }
    return b;
  }

  @Override
  public String next() {
    return iterator.next();
  }
}

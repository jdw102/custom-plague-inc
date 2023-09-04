package ooga.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PathViewCollection {

  private final Collection<PathView> collection;

  public PathViewCollection() {
    collection = new ArrayList<>();
  }

  public void addPathView(PathView pathView) {
    collection.add(pathView);
  }

  public Iterator<PathView> getIterator() {
    return collection.iterator();
  }

}

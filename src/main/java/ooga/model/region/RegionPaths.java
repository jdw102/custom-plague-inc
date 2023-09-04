package ooga.model.region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A data class that contains all PathModel instances and allows addition and iteration through
 * them.
 *
 * @author Jerry Worthy
 */
public class RegionPaths implements Iterator {

  private final List<PathModel> paths;
  private Iterator<PathModel> iterator;

  public RegionPaths() {
    paths = new ArrayList<>();
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = paths.iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = paths.iterator();
    }
    return b;
  }

  @Override
  public PathModel next() {
    return iterator.next();
  }

  public void addPath(PathModel path) {
    paths.add(path);
  }
}

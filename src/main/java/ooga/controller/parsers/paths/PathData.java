package ooga.controller.parsers.paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class to hold overall data obtained from Paths JSON
 */

public class PathData implements Iterable<PathRecord> {

  private final List<PathRecord> validPaths;

  public PathData(List<PathRecord> lst) {
    validPaths = lst;
  }

  /**
   * Checks if some provided ID is a valid path ID
   *
   * @param id
   * @return
   */
  public boolean isValidPathID(int id) {
    for (PathRecord p : validPaths) {
      if (p.id() == id) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if some provided path name is a valid path name
   *
   * @param name
   * @return
   */
  public boolean isValidPathName(String name) {
    for (PathRecord p : validPaths) {
      if (p.name().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provided some ID, returns the path with that ID
   *
   * @param id
   * @return
   * @throws NullPointerException if no path exists with that ID
   */
  public PathRecord getPathRecordByID(int id) throws NullPointerException {
    for (PathRecord p : validPaths) {
      if (p.id() == id) {
        return p;
      }
    }
    throw new NullPointerException(String.format("No path of ID %d", id));
  }

  /**
   * Returns unmodifiable copy of list, and all elements are immutable, so provides data protection.
   * Mostly for testing.
   *
   * @return
   */
  public List<PathRecord> getValidPaths() {
    return Collections.unmodifiableList(validPaths);
  }

  /**
   * Returns list of all valid path names. Data in this list is immutable and the returned
   * collection is dispensable so data protection exists.
   *
   * @return
   */
  public List<String> getValidPathNames() {
    List<String> lst = new ArrayList<>();
    for (PathRecord p : validPaths) {
      lst.add(p.name());
    }
    return lst;
  }

  @Override
  public Iterator<PathRecord> iterator() {
    return getValidPaths().iterator();
  }
}

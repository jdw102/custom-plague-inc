package ooga.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A data class that contains ids of perks required to be bought before another perk can be bought.
 * Contained by PerkModel classes.
 */
public class PerkPrerequisites implements Iterator<Integer> {

  private final Collection<Integer> preReqIds;
  private Iterator<Integer> iterator;

  public PerkPrerequisites() {
    preReqIds = new ArrayList<>();
  }

  public void addId(int id) {
    preReqIds.add(id);
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = preReqIds.iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = preReqIds.iterator();
    }
    return b;
  }

  @Override
  public Integer next() {
    return iterator.next();
  }

  public boolean contains(int id) {
    return preReqIds.contains(id);
  }

  public int size() {
    return preReqIds.size();
  }

}

package ooga.model.gamestate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A data class that contains a collection of conditions and a message to be retrieved when those
 * conditions are satisfied.
 */
public class EndConditions implements Iterator<Condition> {

  private final Collection<Condition> conditions;
  private Iterator<Condition> iterator;

  public EndConditions() {
    conditions = new ArrayList<>();
  }

  public void addCondition(Condition condition) {
    conditions.add(condition);
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = conditions.iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = conditions.iterator();
    }
    return b;
  }

  @Override
  public Condition next() {
    return iterator.next();
  }
}

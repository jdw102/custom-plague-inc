package ooga.model.region;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A data class that contains the amount of each population type held by a region's state. Allows
 * addition, retrieval, and iteration of population amounts.
 *
 * @author Jerry Worthy
 */
public class Census implements Iterator {

  private final Map<String, Integer> populations;
  private Iterator<String> iterator;

  public Census() {
    populations = new HashMap<>();
  }

  public void addPopulation(String name, int amount) {
    populations.put(name, amount);
  }

  public void adjustPopulation(String name, int amount) throws PopulationNotFoundException {
    populations.put(name, getPopulation(name) + amount);
  }

  public int getPopulation(String name) throws PopulationNotFoundException {
    int amt = 0;
    if (populations.get(name) == null) {
      throw new PopulationNotFoundException(name);
    } else {
      amt = populations.get(name);
    }
    return amt;
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = populations.keySet().iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = populations.keySet().iterator();
    }
    return b;
  }

  @Override
  public String next() {
    return iterator.next();
  }

  /**
   * Adds a census to itself.
   *
   * @param census
   */
  public void add(Census census) throws PopulationNotFoundException {
    while (census.hasNext()) {
      String pop = census.next();
      populations.putIfAbsent(pop, 0);
      populations.put(pop, populations.get(pop) + census.getPopulation(pop));
    }
  }

  /**
   * Subtracts a census from itself.
   *
   * @param census
   */
  public void subtract(Census census) throws PopulationNotFoundException {
    while (census.hasNext()) {
      String pop = census.next();
      populations.put(pop, populations.get(pop) - census.getPopulation(pop));
    }
  }

  /**
   * Creates a copy of itself.
   *
   * @return copy of itself
   */
  public Census getCopy() throws PopulationNotFoundException {
    Census newCensus = new Census();
    while (hasNext()) {
      String name = next();
      newCensus.addPopulation(name, getPopulation(name));
    }
    return newCensus;
  }

  /**
   * @return total population amount
   */
  public int getTotal() {
    int total = 0;
    Iterator<String> temp = populations.keySet().iterator();
    while (temp.hasNext()) {
      total += populations.get(temp.next());
    }
    return total;
  }
}

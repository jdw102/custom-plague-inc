package ooga.model.region;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ooga.util.Observer;

/**
 * A data class that contains all the instances of the RegionModel and allows addition and iteration
 * through them.
 *
 * @author Jerry Worthy
 */
public class RegionMap implements Iterator<RegionModel> {

  private final Map<Integer, RegionModel> regions;
  private Iterator<RegionModel> iterator;
  private final Collection<Observer> observers;
  private final Census startCensus;

  public RegionMap(Census startCensus) {
    regions = new HashMap<>();
    observers = new ArrayList<>();
    this.startCensus = startCensus;
  }

  @Override
  public boolean hasNext() {
    if (iterator == null) {
      iterator = regions.values().iterator();
    }
    boolean b = iterator.hasNext();
    if (!b) {
      iterator = regions.values().iterator();
    }
    return b;
  }

  @Override
  public RegionModel next() {
    return iterator.next();
  }

  public RegionModel getRegionModelByID(int id) throws NullPointerException {
    return regions.get(id);
  }

  public void addToRegion(int id, Census census) throws PopulationNotFoundException {
    regions.get(id).getCurrentState().census().add(census);
  }

  public void spreadStartingCensus(int id) throws PopulationNotFoundException {
    addToRegion(id, startCensus);
  }

  public void addRegion(RegionModel region) {
    regions.put(region.getId(), region);
  }

  public Census getTotalCensus() throws PopulationNotFoundException {
    Census census = new Census();
    int i = 0;
    while (hasNext()) {
      if (i == 0) {
        census = next().getCurrentState().census().getCopy();
      } else {
        census.add(next().getCurrentState().census());
      }
      i++;
    }
    return census;
  }
}

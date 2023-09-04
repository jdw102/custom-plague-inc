package ooga.model.region;

import static ooga.Main.GLOBAL_RNG;

import java.util.ArrayList;
import java.util.Collection;
import ooga.util.Observable;
import ooga.util.Observer;

/**
 * Parent class of model representing a path between two regions. Can be multiple types and contains
 * an origin region and destination region.
 *
 * @author Jerry Worthy
 */
public class PathModel implements Observable {

  private final RegionModel origin;
  private final RegionModel destination;
  private final String type;
  private final PathPointsMap pathPointsMap;
  private final int totalAmount;
  private final Collection<Observer> observers;

  /**
   * Creates instance of path model.
   *
   * @param origin        the origin region model
   * @param destination   the destination region model
   * @param type          the type of path
   * @param pathPointsMap contains information regarding how many points a population is worth if it
   *                      first enters a region
   * @param amount        the total amount of people transferred
   */
  public PathModel(RegionModel origin, RegionModel destination, String type,
      PathPointsMap pathPointsMap, int amount) {
    this.origin = origin;
    this.destination = destination;
    this.type = type;
    this.pathPointsMap = pathPointsMap;
    this.totalAmount = amount;
    observers = new ArrayList<>();
  }

  /**
   * Get points associated with population when it first enters a region.
   *
   * @param name the name of the population
   * @return number of points
   */
  public double getPoints(String name) {
    return pathPointsMap.getPoints(name);
  }

  public RegionModel getOrigin() {
    return origin;
  }

  public RegionModel getDestination() {
    return destination;
  }

  public String getType() {
    return type;
  }

  public int getTotalAmount() {
    return totalAmount;
  }

  /**
   * Based on path activity of origin region, it will randomly transfer a census of people to the
   * destination region by setting the next state of the destination and origin accordingly.
   *
   * @param census the census of people to be transferred
   */
  public void transferPeople(Census census)
      throws PathNotFoundException, PopulationNotFoundException {
    double rand = GLOBAL_RNG.nextDouble();
    if (rand <= origin.getPathActivity(type) && destination.getCurrentState().isOpen()) {
      origin.getNextState().census().subtract(census);
      destination.getNextState().census().add(census);
      notifyObservers();
    }
  }

  /**
   * Checks if destination region has just received a given population.
   *
   * @param population the population name
   */
  public boolean checkNewlyTransferred(String population) throws PopulationNotFoundException {
    return (!destination.getCurrentState().hasPopulation(population) && destination.getNextState()
        .hasPopulation(population));
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }
}

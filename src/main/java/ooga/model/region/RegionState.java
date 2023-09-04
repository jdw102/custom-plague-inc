package ooga.model.region;

/**
 * Parent class for state of regions. Contains all information that can change between updates of
 * the game model.
 *
 * @author Jerry Worthy
 */
public record RegionState(Census census, boolean isOpen, boolean hasAntagonist) {


  public boolean hasPopulation(String name) throws PopulationNotFoundException {
    return census.getPopulation(name) > 0;
  }

}

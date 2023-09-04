package ooga.controller.parsers.math;

/**
 * Class to hold overall data obtained from Math JSON
 */

public class MathData {

  private final SubPopulationData subpopsAndDependencies;
  private final MathUserPointsRecord userPointsRecord;

  /**
   * Constructor that takes in sub-datafiles/records for MathData to hold
   *
   * @param subPops    data class holding all subpopulation data
   * @param userPoints record class holding user points info
   */
  public MathData(SubPopulationData subPops, MathUserPointsRecord userPoints) {
    subpopsAndDependencies = subPops;
    userPointsRecord = userPoints;
  }

  public String getDefaultPopulation() {
    return subpopsAndDependencies.getDefaultPopulation();
  }

  public SubPopulationData getSubpopsAndDependencies() {
    return subpopsAndDependencies;
  }

  public MathUserPointsRecord getUserPointsRecord() {
    return userPointsRecord;
  }
}

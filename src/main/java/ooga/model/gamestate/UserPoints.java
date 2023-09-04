package ooga.model.gamestate;


/**
 * This interface is used to handle the math behind accruing user points (which will be show in the
 * view). This
 *
 * @author Anika Mitra
 */

public abstract class UserPoints implements Progressor {

  private final double maxAmt;
  private double points;

  public UserPoints(double start, double max) {
    this.points = start;
    this.maxAmt = max;
  }

  /**
   * @param val adjust the current points by val (can be positive or negative) Should throw an
   *            exception if points is ever less than 0 Can consider just making this a void
   *            setPoints(int val) method
   */
  public boolean adjustPoints(double val) {
    this.points = val;

    return true;
  }

  @Override
  public double getProgress() {
    return getPoints();
  }

  @Override
  public double getProgress(String s) {
    return getProgress();
  }

  public double getPoints() {
    return points;
  }

  protected double getMaxAmt() {
    return maxAmt;
  }
}

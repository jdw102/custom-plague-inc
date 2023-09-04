package ooga.util;

public class NegativeShiftedSineOperation implements Operation {

  @Override
  public double operate(double d1) {
    return (-1 * Math.sin(d1 + 0.5) + 1) / 5;
  }
}

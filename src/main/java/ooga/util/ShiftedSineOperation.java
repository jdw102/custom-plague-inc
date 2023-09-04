package ooga.util;

public class ShiftedSineOperation implements Operation {

  @Override
  public double operate(double d1) {
    return (Math.sin(d1 + 0.5)) / 5;
  }
}

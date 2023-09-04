package ooga.util;

public class DifferenceOperation implements BiOperation {

  public DifferenceOperation() {

  }

  @Override
  public double operate(double d1, double d2) {
    return d2 - d1;
  }
}

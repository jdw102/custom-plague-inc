package ooga.util;

public class SumOperation implements BiOperation {

  public SumOperation() {

  }

  @Override
  public double operate(double d1, double d2) {
    return d1 + d2;
  }
}

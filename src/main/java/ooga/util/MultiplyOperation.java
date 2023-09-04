package ooga.util;

public class MultiplyOperation implements BiOperation {

  public MultiplyOperation() {

  }


  @Override
  public double operate(double d1, double d2) {
    return d1 * d2;
  }
}

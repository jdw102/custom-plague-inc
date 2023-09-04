package ooga.util;

public class GreaterThanOperation implements ConditionalOperation {


  @Override
  public boolean operate(double d1, double d2) {
    return d1 >= d2;
  }
}

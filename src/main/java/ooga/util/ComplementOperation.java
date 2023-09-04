package ooga.util;

public class ComplementOperation implements Operation {

  public ComplementOperation() {

  }

  @Override
  public double operate(double d1) {
    return 1 - d1;
  }
}

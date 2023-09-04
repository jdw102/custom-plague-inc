package ooga.view.map;

public record RegionPortRecord(String portSymbol, double x, double y) {

  @Override
  public String portSymbol() {
    return portSymbol;
  }

  @Override
  public double x() {
    return x;
  }

  @Override
  public double y() {
    return y;
  }
}

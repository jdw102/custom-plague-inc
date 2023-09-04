package ooga.controller.parsers.paths;

public record SubPopSpreadRecord(String popName, int pointsWorth) {

  @Override
  public String popName() {
    return popName;
  }

  @Override
  public int pointsWorth() {
    return pointsWorth;
  }

}

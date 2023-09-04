package ooga.model.actor;

public enum ProtagonistFactors {
  CORE("Core"),
  PATH("Path");

  String name;

  ProtagonistFactors(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

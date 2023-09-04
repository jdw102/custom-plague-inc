package ooga.view;

import javafx.beans.property.SimpleStringProperty;

public class FactorText {

  private final SimpleStringProperty factor;
  private final SimpleStringProperty level;

  public FactorText(String factor, String level) {
    this.factor = new SimpleStringProperty(factor);
    this.level = new SimpleStringProperty(level);
  }

  public String getFactor() {
    return factor.get();
  }

  public String getLevel() {
    return level.get();
  }
}

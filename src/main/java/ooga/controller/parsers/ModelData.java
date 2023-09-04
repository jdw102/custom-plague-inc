package ooga.controller.parsers;

import java.util.Collections;
import java.util.List;

public abstract class ModelData {

  protected List<? extends Record> validModelData;

  public ModelData(List<? extends Record> dataRecords) {
    this.validModelData = dataRecords;
  }

  public abstract boolean isValidField(String name);

  public abstract boolean isValidValueForField(String type, String value);

  public abstract Record getModelData(String id);

  protected List<? extends Record> getRecordsList() {
    return Collections.unmodifiableList(validModelData);
  }

}

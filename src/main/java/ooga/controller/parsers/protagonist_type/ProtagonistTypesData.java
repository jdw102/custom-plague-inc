package ooga.controller.parsers.protagonist_type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ooga.controller.parsers.ModelData;

/**
 * Class that holds overall data obtained from ProtagonistTypes JSON
 */

public class ProtagonistTypesData extends ModelData {

  private final List<String> validCoreFactors;

  public ProtagonistTypesData(List<ProtagonistTypesRecord> protagonistTypesRecordList,
      List<String> coreFactors) {
    super(protagonistTypesRecordList);
    validCoreFactors = coreFactors;
  }

  /**
   * Checks whether some given protagonistname is a name for a valid protagonist
   *
   * @param protagonistName
   * @return
   */
  @Override
  public boolean isValidField(String protagonistName) {
    for (ProtagonistTypesRecord r : getValidProtagonists()) {
      if (r.name().equals(protagonistName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provides dispensable list with immutable elements, providing data protection.
   *
   * @return
   */
  public List<ProtagonistTypesRecord> getValidProtagonists() {
    List<ProtagonistTypesRecord> lst = new ArrayList<>();
    for (Record r : getRecordsList()) {
      lst.add((ProtagonistTypesRecord) r);
    }
    return lst;
  }

  /**
   * Provides immutable list of immutable objects, providing data protection.
   *
   * @return
   */
  public List<String> getValidCoreFactors() {
    return Collections.unmodifiableList(validCoreFactors);
  }

  /**
   * Checks whether some provided corefactor is a valid one
   *
   * @param coreFactor
   * @return
   */
  public boolean isValidCoreFactor(String coreFactor) {
    return validCoreFactors.contains(coreFactor);
  }

  /**
   * Method required by ModelData but unused for this class. Rethink.
   *
   * @param type
   * @param modifier
   * @return
   */
  @Override
  public boolean isValidValueForField(String type, String modifier) {
    if (!getValidProtagonists().isEmpty()) {
      return getValidProtagonists().get(0).isValidModifierForType(type, modifier);
    }
    return false;
  }

  /**
   * Given some protagonist name, retrieves that protagonistTypeRecord
   *
   * @param protagonistName
   * @return
   * @throws NullPointerException if provided name is not a valid one
   */
  @Override
  public Record getModelData(String protagonistName) throws NullPointerException {
    for (ProtagonistTypesRecord r : getValidProtagonists()) {
      if (r.name().equals(protagonistName)) {
        return r;
      }
    }
    throw new NullPointerException(
        String.format("No protagonist named %s exists", protagonistName));
  }

  public ProtagonistTypesRecord getProtagonistByID(int id) throws NullPointerException {
    for (ProtagonistTypesRecord r : getValidProtagonists()) {
      if (r.id() == id) {
        return r;
      }
    }
    throw new NullPointerException(
        String.format("No protagonist with id %d exists", id));
  }
}

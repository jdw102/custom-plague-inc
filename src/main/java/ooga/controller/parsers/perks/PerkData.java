package ooga.controller.parsers.perks;

import java.util.ArrayList;
import java.util.List;
import ooga.controller.parsers.ModelData;

/**
 * Class to hold overall data obtained from Perks JSON
 */

public class PerkData extends ModelData {

  public PerkData(List<PerkRecord> perkList) {
    super(perkList);
  }

  /**
   * Returns dispensable list
   *
   * @return
   */
  public List<PerkRecord> getValidPerks() {
    List<PerkRecord> lst = new ArrayList<>();
    for (Record r : getRecordsList()) {
      lst.add((PerkRecord) r);
    }
    return lst;
  }

  /**
   * Checks whether some given perk name is a valid one
   *
   * @param perkName
   * @return
   */
  @Override
  public boolean isValidField(String perkName) {
    for (PerkRecord r : getValidPerks()) {
      if (r.name().equals(perkName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Method required by ModelData but unused for this class. Rethink.
   *
   * @param type
   * @param value
   * @return
   */
  @Override
  public boolean isValidValueForField(String type, String value) {
    return false;
  }

  /**
   * Given some perk ID, returns that perk
   *
   * @param perkID
   * @return
   * @throws NullPointerException if provided ID is not a valid one
   */
  @Override
  public Record getModelData(String perkID) throws NullPointerException {
    for (PerkRecord r : getValidPerks()) {
      if (r.id().equals(Integer.valueOf(perkID))) {
        return r;
      }
    }
    throw new NullPointerException(String.format("No perk of id %s", perkID));
  }

  /**
   * Checks whether some given perk id is a valid one
   *
   * @param id
   * @return
   */
  public boolean isValidPerkID(int id) {
    for (PerkRecord r : getValidPerks()) {
      if (r.id().equals(id)) {
        return true;
      }
    }
    return false;
  }
}

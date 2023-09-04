package ooga.controller.parsers.region_factors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ooga.controller.parsers.ModelData;

/**
 * Holds overall data obtained from RegionFactorTypes JSON
 */
public class RegionFactorData extends ModelData implements Iterable<RegionFactorRecord> {

  public RegionFactorData(List<RegionFactorRecord> lst) {
    super(lst);
  }

  /**
   * Returns immutable list of unmodifiable data
   *
   * @return
   */
  public List<? extends Record> getValidRegionFactors() {
    return Collections.unmodifiableList(this.getRecordsList());
  }

  /**
   * Provided some type of regionfactor, returns if it is valid.
   *
   * @param name type such as "climate", "wealth"
   * @return
   */
  @Override
  public boolean isValidField(String name) {
    for (Record r : this.getRecordsList()) {
      if (((RegionFactorRecord) r).name().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provided some type of regionfactor and a possible value, returns whether that value is valid
   * for the given type. Also returns false if provided type is not a valid type.
   *
   * @param type  type of regionfactor such as "climate"
   * @param value value for regionfactor type such as "arid", "humid"
   * @return
   */
  @Override
  public boolean isValidValueForField(String type, String value) {
    for (Record r : this.getRecordsList()) {
      if (((RegionFactorRecord) r).name().equals(type)) {
        return ((RegionFactorRecord) r).isValidValue(value);
      }
    }
    return false;
  }

  /**
   * Returns some specific regionfactor identified by type from list of existing
   * regionfactorrecords.
   *
   * @param type
   * @return
   * @throws NullPointerException if provided type is not a valid regionfactor type
   */
  @Override
  public Record getModelData(String type) throws NullPointerException {
    for (Record r : getRecordsList()) {
      if (((RegionFactorRecord) r).name().equals(type)) {
        return r;
      }
    }
    throw new NullPointerException(String.format("No RegionFactor of type %s", type));
  }

  /**
   * Returns list of valid regionfactor types. Data in this collection is immutable so data
   * protection exists.
   *
   * @return
   */
  public List<String> getRegionFactorTypes() {
    List<String> lst = new ArrayList<>();
    for (Record r : getValidRegionFactors()) {
      lst.add(((RegionFactorRecord) r).name());
    }
    return lst;
  }


  /**
   * Iterates over copy of list of valid regionfactors.
   *
   * @return
   */
  @Override
  public Iterator iterator() {
    return getValidRegionFactors().iterator();
  }


}

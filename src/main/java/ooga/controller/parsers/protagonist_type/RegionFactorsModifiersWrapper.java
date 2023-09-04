package ooga.controller.parsers.protagonist_type;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper class for regionFactorModifiers that provides data protection for the list (as opposed to
 * using a Map with key = type, value = List<ModifierRecords> in ProtagonistTypesRecord which does
 * not)
 *
 * @param type
 * @param modifiers
 */

public record RegionFactorsModifiersWrapper(String type, List<ModifierRecord> modifiers) {

  @Override
  public String type() {
    return type;
  }

  /**
   * Returns immutable list of immutable data
   *
   * @return
   */
  @Override
  public List<ModifierRecord> modifiers() {
    return Collections.unmodifiableList(modifiers);
  }

  /**
   * Checks if given modifiername is a valid modifier for this regionfactormodifier set
   *
   * @param modifierName
   * @return
   */
  public boolean isValidModifier(String modifierName) {
    for (ModifierRecord p : modifiers) {
      if (p.name().equals(modifierName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns some modifier record as identified by name
   *
   * @param modifierName
   * @return
   * @throws NullPointerException if provided modifiername is not a valid one
   */
  public ModifierRecord getModifierByName(String modifierName) throws NullPointerException {
    for (ModifierRecord p : modifiers) {
      if (p.name().equals(modifierName)) {
        return p;
      }
    }
    throw new NullPointerException(
        String.format("Modifier %s is not a valid one for regionFactor of type %s", modifierName,
            this.type));
  }
}

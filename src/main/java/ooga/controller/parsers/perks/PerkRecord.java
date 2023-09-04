package ooga.controller.parsers.perks;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.protagonist_type.ModifierRecord;

/**
 * Data class to hold information about each perk as declared in Perks JSON
 *
 * @param id
 * @param name
 * @param description
 * @param cost
 * @param prereqPerks
 * @param factorModifiers
 */

public record PerkRecord(Integer id, Integer groupId, String name, String description, Integer cost,
                         String actorName,
                         Set<Integer> prereqPerks, List<ModifierRecord> factorModifiers,
                         String imagePath) {

  @Override
  public Integer id() {
    return id;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Integer cost() {
    return cost;
  }

  /**
   * Returns immutable collection of immutable data
   *
   * @return
   */
  @Override
  public Set<Integer> prereqPerks() {
    return Collections.unmodifiableSet(prereqPerks);
  }

  /**
   * Returns immutable collection of immutable data
   *
   * @return
   */
  @Override
  public List<ModifierRecord> factorModifiers() {
    return Collections.unmodifiableList(factorModifiers);
  }

  /**
   * Given some modifier name, returns this ModifierRecord
   *
   * @param name
   * @return
   * @throws NullPointerException if provided name is not a valid one for this perk's modifiers
   */
  public ModifierRecord getModifierByName(String name) throws NullPointerException {
    for (ModifierRecord mr : factorModifiers) {
      if (mr.name().equals(name)) {
        return mr;
      }
    }
    throw new NullPointerException(
        String.format("Perk %s has no modifier effect of name %s", this.name, name));
  }

  /**
   * Given some modifier name, checks whether it is a valid one for this perk
   *
   * @param modifierName
   * @return
   */
  public boolean isValidModifier(String modifierName) {
    for (ModifierRecord mr : factorModifiers) {
      if (mr.name().equals(modifierName)) {
        return true;
      }
    }
    return false;
  }
}

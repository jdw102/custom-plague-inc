package ooga.controller.parsers.protagonist_type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class that holds information about each protagonist type as specified in ProtagonistTypes
 * JSON
 *
 * @param name
 * @param allFactors map of modifier type to list of modifiers for that category
 */
public record ProtagonistTypesRecord(int id, String name, String description, String imagePath,
                                     Map<String, List<ModifierRecord>> allFactors) {

  /**
   * Returns dispensable map of unmodifiable contents
   *
   * @return
   */
  @Override
  public Map<String, List<ModifierRecord>> allFactors() {
    Map<String, List<ModifierRecord>> m = new HashMap<>();
    for (String s : allFactors.keySet()) {
      m.put(s, Collections.unmodifiableList(allFactors.get(s)));
    }
    return m;
  }

  /**
   * Gets list of modifiers for some type
   *
   * @param factorType
   * @return
   */
  public List<ModifierRecord> get(String factorType) {
    try {
      return Collections.unmodifiableList(allFactors.get(factorType));
    } catch (NullPointerException e) {
      return new ArrayList<>();
    }
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Checks if given type is a valid modifier category
   *
   * @param type
   * @return
   */
  public boolean isValidModifierType(String type) {
    return allFactors.containsKey(type);
  }

  /**
   * Checks if given modifier name is a valid name for that modifier category
   *
   * @param type
   * @param modifierName
   * @return
   */
  public boolean isValidModifierForType(String type, String modifierName) {
    if (!isValidModifierType(type)) {
      return false;
    }
    for (ModifierRecord mr : allFactors.get(type)) {
      if (mr.name().equals(modifierName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets list of each modifier name for some category
   *
   * @param type
   * @return
   */
  public List<String> getModifierNamesForType(String type) {
    List<String> lst = new ArrayList<>();
    if (isValidModifierType(type)) {
      for (ModifierRecord mr : allFactors.get(type)) {
        lst.add(mr.name());
      }
    }
    return lst;
  }

  /**
   * Given some modifier category and name, returns that specific ModifierRecord
   *
   * @param type
   * @param name
   * @return
   * @throws NullPointerException
   */
  public ModifierRecord getModifierByTypeAndName(String type, String name)
      throws NullPointerException {
    for (ModifierRecord mr : allFactors.get(type)) {
      if (mr.name().equals(name)) {
        return mr;
      }
    }
    throw new NullPointerException(
        String.format("No modifier named %s for type %s in protagonist %s", name, type, this.name));
  }

}

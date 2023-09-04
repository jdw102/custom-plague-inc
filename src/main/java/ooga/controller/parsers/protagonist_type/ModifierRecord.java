package ooga.controller.parsers.protagonist_type;

/**
 * Data class to hold some factor/modifier information. Used for both protagonist base
 * effectivenesses and perk effects.
 *
 * @param factor category for this modifier: like "climate", "Path", "Core", "wealth", etc
 *               (path/core or regionfactor category)
 * @param name
 * @param amount
 */
public record ModifierRecord(String factor, String name, Double amount) {

  @Override
  public String name() {
    return name;
  }

  @Override
  public Double amount() {
    return amount;
  }

  @Override
  public String factor() {
    return factor;
  }
}

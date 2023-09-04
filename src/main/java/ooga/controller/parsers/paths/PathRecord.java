package ooga.controller.parsers.paths;

import java.util.Collections;
import java.util.List;

/**
 * Data class to hold information about each path as declared in Path JSON
 *
 * @param id
 * @param name
 * @param imgPath
 * @param portImgPath
 * @param animationType
 * @param symbol
 * @param subpopSpread
 */

//change paths to actual Path objects if necessary
public record PathRecord(Integer id, String name, String imgPath, String portImgPath,
                         String animationType, String symbol, Integer peopleSpread,
                         List<SubPopSpreadRecord> subpopSpread) {

  @Override
  public Integer peopleSpread() {
    return peopleSpread;
  }

  @Override
  public Integer id() {
    return id;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String imgPath() {
    return imgPath;
  }

  @Override
  public String portImgPath() {
    return portImgPath;
  }

  @Override
  public String animationType() {
    return animationType;
  }

  @Override
  public String symbol() {
    return symbol;
  }

  /**
   * Returns immutable map of immutable objects.
   *
   * @return
   */
  @Override
  public List<SubPopSpreadRecord> subpopSpread() {
    return Collections.unmodifiableList(subpopSpread);
  }

  /**
   * Checks if some path spreads a given subpop.
   *
   * @param subpop
   * @return
   */
  public boolean spreadsThisSubpop(String subpop) {
    for (SubPopSpreadRecord s : subpopSpread) {
      if (s.popName().equals(subpop)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets num points worth for some given subpop
   *
   * @param subpop
   * @return
   * @throws NullPointerException if provided subpop is not a valid one.
   */
  public int getPointsForSubpop(String subpop) throws NullPointerException {
    return getSubPopSpreadRecord(subpop).pointsWorth();
  }

  public SubPopSpreadRecord getSubPopSpreadRecord(String subPop) throws NullPointerException {
    for (SubPopSpreadRecord s : subpopSpread) {
      if (s.popName().equals(subPop)) {
        return s;
      }
    }
    throw new NullPointerException(
        String.format("Path id %d does not spread subpopulation %s", id, subPop));
  }

  /**
   * Checks that provided port symbol is not blank. Should be used when parsing Map CSV.
   *
   * @return
   */
  public boolean hasValidPortSymbol() {
    return !symbol.isBlank();
  }
}

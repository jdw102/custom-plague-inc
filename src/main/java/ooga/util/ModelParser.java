package ooga.util;


/**
 * This class will provide a lightweight wrapper for a JSON parser. This parser will be able to
 * retrieve values from JSON files that will be used to generate other Models within the game.
 *
 * @author diego
 */
public class ModelParser {

  //GameConfigParser parser;

  /**
   * @param filename name of the file to be parsed
   */
  public ModelParser(String filename) {
    //parser = new GameConfigParser(filename);
  }

  public int parseInt(String key) {
    return 0;
  }

  public int[] parseIntArray(String key) {
    return null;
  }

  public String parseString(String key) {
    return "";
  }

  public String[] parseStringArray(String key) {
    return null;
  }
}

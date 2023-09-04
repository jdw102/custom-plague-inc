package ooga.controller.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Re-usable JSON parser utilities.
 *
 * @author diego
 */
public class ParserUtils {

  private static final Logger LOG = LogManager.getLogger(ParserUtils.class);
  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Generates a Map data structure from a JSON list, where each list element contains a String
   * value and a Double value.
   *
   * @param jnode             JsonNode where the list of [String, Double] entries are stored.
   * @param listName          name of the overall list in the JSON entry
   * @param stringElementName name of the JSON entry that stores a String
   * @param doubleElementName name of the JSON entry that stores a Double
   * @return a Map containing that matches all String and Double elements together
   */
  public static Map<String, Double> makeValuesMapDoubles(JsonNode jnode, String listName,
      String stringElementName, String doubleElementName) {
    Map<String, Double> valuesMap = new HashMap<>();
    try {
      for (JsonNode node : jnode.get(listName)) {
        try {
          String level = node.get(stringElementName).asText();
          if (level.isBlank()) {
            throw new NullPointerException("No value provided for " + stringElementName);
          }

          Double value = getDoubleValue(node, doubleElementName);

          valuesMap.put(level, value);

        } catch (NullPointerException n) {
          //val doesn't have a level entry, skip it
        }
      }
    } catch (NullPointerException e) {  //node has no values entry or entry is not an array
      return valuesMap;
    }
    return valuesMap;
  }

  /**
   * Creates a List of integers from a JsonNode int array entry.
   *
   * @param jsonNode The top node where the list is stored in.
   * @param keyName  The name of the
   * @return
   */
  public static List<Integer> makeIntValuesList(JsonNode jsonNode, String keyName) {
    List<Integer> integerList = new ArrayList<>();
    // TODO: handle errors.
    // iterate through each element, convert to int, and add to list
    try {
      JsonNode jsonListNode = jsonNode.get(keyName);
      for (JsonNode element : jsonListNode) {
        try {
          int value = Integer.parseInt(element.toString());
          integerList.add(value);
        } catch (NullPointerException | NumberFormatException f) {
          //ignore this element i guess?
        }
      }
    } catch (NullPointerException e) {
      throw new ConfigJSONException("Error parsing list of integers." + e);
    }
    return integerList;
  }

  /**
   * Similar to makeIntValuesList, but creates a set instead.
   *
   * @param jsonNode
   * @param keyName
   * @return
   */
  public static Set<Integer> makeIntValuesSet(JsonNode jsonNode, String keyName) {
    Set<Integer> intset = new HashSet<>();
    try {
      JsonNode jsonListNode = jsonNode.get(keyName);
      for (JsonNode element : jsonListNode) {
        try {
          int value = Integer.parseInt(element.toString());
          intset.add(value);
        } catch (NullPointerException | NumberFormatException f) {
        }
      }
    } catch (NullPointerException e) {
      throw new ConfigJSONException("Error parsing set of integers." + e);
    }
    return intset;
  }


  /**
   * Parses a Double-type value stored at a field in a JSON file.
   *
   * @param jsonNode the JSON node where the element is stored.
   * @param keyName  the name of the key holding the value
   * @return The value stored at the provided key.
   */
  public static Double getDoubleValue(JsonNode jsonNode, String keyName) {
    try {
      String parsedValue = jsonNode.get(keyName).toString();
      return Double.parseDouble(parsedValue);
    } catch (NumberFormatException | NullPointerException e) {
      return 1.0;
    }
  }

  /**
   * Modified getDoubleValue that throws the exceptions, allowing the user to handle them as they
   * wish.
   *
   * @param jnode
   * @param keyName
   * @return
   * @throws NullPointerException
   * @throws NumberFormatException
   */
  public static double getDoubleValueExceptions(JsonNode jnode, String keyName)
      throws NullPointerException, NumberFormatException {
    String val = jnode.get(keyName).asText();
    return Double.parseDouble(val);
  }

  /**
   * Parses a double-type value stored at a field in a JSON file. If parsing throws error or parsed
   * double does not pass the predicate, returns default value.
   *
   * @param jnode
   * @param keyName
   * @param check      some predicate used to evaluate the parsed double
   * @param defaultVal
   * @return
   */
  public static Double getDoubleValueDefault(JsonNode jnode, String keyName,
      Predicate<Double> check, double defaultVal, String msgForFailedTest) {
    try {
      double d = getDoubleValueExceptions(jnode, keyName);
      if (!check.test(d)) {
        throw new NullPointerException(
            String.format("looking for key %s, read %f, %s", keyName, d, msgForFailedTest));
      }
      return d;
    } catch (NumberFormatException | NullPointerException e) {
      LOG.warn(String.format("%s: using %f", e.getMessage(), defaultVal));
      return defaultVal;
    }
  }

  /**
   * Parses an int-tye value stored at a field in a JSON file.
   *
   * @param jnode   JsonNode to look in
   * @param keyName key holding value to look for
   * @return integer
   * @throws NullPointerException,  if value is not found
   * @throws NumberFormatException, if value is not an int exceptions are so user can decide how to
   *                                deal with
   */
  public static Integer getIntValue(JsonNode jnode, String keyName)
      throws NullPointerException, NumberFormatException {
    String val = jnode.get(keyName).asText();
    return Integer.parseInt(val);
  }

  /**
   * Parses an int-type value stored at a field in a JSON file. If parsing throws error or parsed
   * integer does not pass the predicate, returns default value.
   *
   * @param jnode
   * @param keyName
   * @param check      some condition used to evaluate the integer
   * @param defaultVal
   * @return
   */
  public static Integer getIntValueDefault(JsonNode jnode, String keyName, Predicate<Integer> check,
      int defaultVal, String msgForFailedTest) {
    try {
      int i = getIntValue(jnode, keyName);
      if (!check.test(i)) {
        throw new NullPointerException(
            String.format("looking for key %s, read %d, %s", keyName, i, msgForFailedTest));
      }
      return i;
    } catch (NumberFormatException | NullPointerException e) {
      LOG.warn(String.format("%s: using %d", e.getMessage(), defaultVal));
      return defaultVal;
    }
  }

  /**
   * Parses a String-type object stored at a field in a JSON file.
   *
   * @param jsonNode the JSON node where the element is stored.
   * @param keyName  the name of the key holding the value
   * @return The String stored at the provided key.
   */
  public static String getString(JsonNode jsonNode, String keyName) {
    try {
      return jsonNode.get(keyName).toString();
    } catch (NumberFormatException | NullPointerException e) {
      return "error";
    }
  }

  /**
   * Modified getString that disallows blank strings and allows user to decide what they want to do
   * with a NullPointerException
   * todo use this in regionfactorsjson and mathjson
   *
   * @param jnode   json node to work with
   * @param keyName key name to search for
   * @return string value for key
   * @throws NullPointerException if key is not found or returned string is blank
   */
  public static String getNonBlankString(JsonNode jnode, String keyName)
      throws NullPointerException {
    String s = jnode.get(keyName).asText();
    if (s.isBlank()) {
      throw new NullPointerException();
    }
    return s;
  }

  /**
   * Modified getString that allows user to provide some condition the string must pass and a
   * default value for this string to use if the parsing fails or the parsed string is blank or does
   * not meet the condition.
   *
   * @param jnode
   * @param keyName
   * @param check
   * @param defaultVal
   * @return
   */
  public static String getStringDefault(JsonNode jnode, String keyName, Predicate<String> check,
      String defaultVal, String msgForFailedTest) {
    try {
      String s = jnode.get(keyName).asText();
      if (!check.test(s)) {
        throw new NullPointerException(
            String.format("looking for key %s, read %s, %s", keyName, s, msgForFailedTest));
      }
      if (s.isBlank()) {
        throw new NullPointerException("Provided string was blank");
      }
      return s;
    } catch (NullPointerException e) {
      LOG.warn(String.format("%s: using %s", e.getMessage(), defaultVal));
      return defaultVal;
    }
  }

}

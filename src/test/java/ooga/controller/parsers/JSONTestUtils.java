package ooga.controller.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Useful Jackson JSON parser functions to use in
 * JSON parsing tests
 */
public class JSONTestUtils {
  public static JsonNode jsonStringToNode(String jsonString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(jsonString);
  }
}

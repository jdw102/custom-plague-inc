package ooga.controller.parsers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Main parser class to hold more specific parsers & information common to multiple
 */
public class GameConfigParser {

  public GameConfigParser() {

  }


  public JsonNode parseJSONConfig(File file) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jnode = mapper.readTree(file);
      return jnode;

    } catch (IOException e) {
      //invalid json file--todo handle
    }
    return null;
  }

}

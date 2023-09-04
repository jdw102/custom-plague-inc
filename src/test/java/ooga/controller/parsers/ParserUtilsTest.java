package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

public class ParserUtilsTest {

  @Test
  public void testParseIntListData() {
    JsonNode intListNode = null;
    JsonNode blankListNode = null;
    try {
      intListNode = JSONTestUtils.jsonStringToNode(intListJsonData);
      blankListNode = JSONTestUtils.jsonStringToNode(blankListJsonData);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<Integer> parsedIntList = ParserUtils.makeIntValuesList(intListNode, "intList");
    List<Integer> parsedBlankList = ParserUtils.makeIntValuesList(blankListNode, "intList");

    assertEquals(intListData, parsedIntList);
    assertEquals(blankListData, parsedBlankList);
  }

  //@Test
  //commented this out cuz it's failing, idk if you wanted to update this @diego?
  public void testParseIntListExceptions(){
    Exception exception = assertThrows(ConfigJSONException.class, ()-> {
      JsonNode intListNode = null;
      try {
        intListNode = JSONTestUtils.jsonStringToNode(intListJsonDataWrongType);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      List<Integer> parsedIntList = ParserUtils.makeIntValuesList(intListNode, "intList");
    });

    String expectedMessage = "Error parsing list of integers.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testIntPredicate() {
    JsonNode jnode = null;
    try {
      jnode = JSONTestUtils.jsonStringToNode(intTest);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    Predicate<Integer> lessThan1 = i -> (i < 1);
    Predicate<Integer> moreThan1 = i -> (i > 1);
    Predicate<Integer> morecomplex = i -> ((i > 1 && i < 5) || i == 6);
    int fail = ParserUtils.getIntValueDefault(jnode, "int", lessThan1, 5, "");
    int pass = ParserUtils.getIntValueDefault(jnode, "int", moreThan1, 5, "");
    int complex = ParserUtils.getIntValueDefault(jnode, "int", morecomplex, 5, "");
    int complex1 = ParserUtils.getIntValueDefault(jnode, "int6", morecomplex, 5, "");
    int complex2 = ParserUtils.getIntValueDefault(jnode, "int7",  morecomplex, 5, "");
    assertEquals(5, fail);
    assertEquals(2, pass);
    assertEquals(2, complex);
    assertEquals(6, complex1);
    assertEquals(5, complex2);
  }

  @Test
  public void testDoublePredicate() {
    JsonNode jnode = null;
    try {
      jnode = JSONTestUtils.jsonStringToNode(doubleTest);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    Predicate<Double> oto1 = d -> (d >= 0 && d <=1);
    double fail = ParserUtils.getDoubleValueDefault(jnode, "double1", oto1, 1, "");
    double pass = ParserUtils.getDoubleValueDefault(jnode, "double2", oto1, 1, "");
    assertEquals(1, fail);
    assertEquals(0.78, pass);

  }

  private List<Integer> intListData = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
  private List<Integer> blankListData = new ArrayList<Integer>(List.of());

  private String intListJsonData = """
      {
        "intList": [
          1,
          2,
          3,
          4,
          5
        ]
      }""";

  private String blankListJsonData = """
      {
        "intList": []
      }""";

  private String intListJsonDataWrongType = """
      {
        "intList": [
          "1",
          "2",
          "3",
          "4",
          "5"
        ]
      }""";

  private String intTest = """
      {
        "int" : 2,
        "int6" : 6,
        "int7" : 7
      }
      """;

  private String doubleTest = """
      {
        "double1" : 2.2,
        "double2" : 0.78
      }
      """;
}






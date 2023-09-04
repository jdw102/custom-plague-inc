package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import ooga.controller.parsers.region_factors.RegionFactorRecord;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class RegionFactorsJSONTest {

  JsonNode jnode;
  RegionFactorsJSON r;

  RegionFactorRecord rec;

  void stringToJnodeAndSetup(String in) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      r = new RegionFactorsJSON(jnode);
      r.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  void stringToJnode(String in) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      r = new RegionFactorsJSON(jnode);
    } catch (JsonProcessingException e) {
    }
  }

  @Test
  void RegionFactorRecordTest() {
    stringToJnodeAndSetup(singleValidSetup);
    assertEquals(1, r.getValidRegionFactors().getValidRegionFactors().size());
    RegionFactorRecord rf = (RegionFactorRecord) r.getValidRegionFactors().getValidRegionFactors().get(0);
    assertEquals("Climate", rf.name());
    assertEquals("Arid", rf.defaultLevel());

    Map<String, Double> expected = new HashMap<>();
    expected.put("Arid", 2.0);
    expected.put("Normal", 1.0);
    expected.put("Humid", 0.5);

    assertEquals(expected.size(), rf.values().size());

    for (String s : expected.keySet()) {
      assertTrue(rf.isValidValue(s));
      assertEquals(expected.get(s), rf.getAntagBaseVal(s));
    }
  }


  @Test
  void testRFCreation() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionFactorSetup);
    assertEquals(4, r.getValidRegionFactors().getValidRegionFactors().size());
  }

  @Test
  void testRFCreationName() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionFactorSetup);
    List<String> expectedNames = Arrays.asList("Climate", "Temperature", "Wealth", "Density");
    //for (RegionFactorRecord rf : r.getValidRegionFactors().getValidRegionFactors()) {
    for (RegionFactorRecord rf : r.getValidRegionFactors()) {
      assertTrue(expectedNames.contains(rf.name()));
      assertTrue(r.getValidRegionFactors().isValidField(rf.name()));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "Climate,Normal",
      "Temperature,Hot",
      "Wealth,Rich",
      "Density,Urban"
  })
  void testRFCreationDefaultValue(String type, String expectedDefault) {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionFactorSetup);
    assertEquals(expectedDefault, ((RegionFactorRecord) r.getValidRegionFactors().getModelData(type)).defaultLevel());
  }

  @ParameterizedTest
  @MethodSource("typeAndValues")
  void testRFCreationValues(String type, List<String> values, List<Double> antagVals) {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionFactorSetup);
    RegionFactorRecord rf = (RegionFactorRecord) r.getValidRegionFactors().getModelData(type);
    assertEquals(values.size(), rf.values().size());
    for (int i = 0; i < values.size(); i++) {
      String val = values.get(i);
      assertTrue(rf.values().containsKey(val));
      assertTrue(r.getValidRegionFactors().isValidValueForField(type, val));
    }
  }

  @ParameterizedTest
  @MethodSource("typeAndValues")
  void testRFCreationAntagValues(String type, List<String> values, List<Double> antagVals) {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionFactorSetup);
    RegionFactorRecord rf = (RegionFactorRecord) r.getValidRegionFactors().getModelData(type);
    assertEquals(antagVals.size(), rf.values().values().size());
    for (int i = 0; i < antagVals.size(); i++) {
      String val = values.get(i);
      Double antagVal = antagVals.get(i);
      assertEquals(antagVal, rf.getAntagBaseVal(val));
    }
  }

  @Test
  void testNoNameSkips() {
    for (String s : missingName) {
      stringToJnode(s);
      r.parseAllData();
      assertTrue(r.getValidRegionFactors().getValidRegionFactors().isEmpty());
    }
  }

  @Test
  void testNoValues() {
    stringToJnodeAndSetup(missingValuesTotal);
    assertEquals(0, r.getValidRegionFactors().getValidRegionFactors().size());
  }

  @Test
  void testInvalidValues() {
    for (String s : invalidvalues) {
      stringToJnodeAndSetup(s);
      assertEquals(0, r.getValidRegionFactors().getValidRegionFactors().size());
    }
  }

  @Test
  void testInvalidAntagLVL() {
    stringToJnodeAndSetup(invalidAntagVal);
    assertEquals(1, ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).getAntagBaseVal("Arid"));
  }

  @Test
  void testBadAntagVal() {
    stringToJnodeAndSetup(badAntagVal);
    assertEquals(1, ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).getAntagBaseVal("Arid"));
  }

  @Test
  void testInvalidLvl() {
    stringToJnode(valuesLvlBlank);
    r.parseAllData();
    assertEquals(1, r.getValidRegionFactors().getValidRegionFactors().size());
    assertTrue(r.getValidRegionFactors().isValidField("Climate"));
    assertEquals(2, ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).values().size());
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Normal"));
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Humid"));
    assertFalse(r.getValidRegionFactors().isValidValueForField("Climate", "Arid"));
    assertTrue(((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).defaultLevel().equals("Normal") || ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).defaultLevel().equals("Humid"));
  }

  @Test
  void testNoLvl() {
    stringToJnode(valuesMissingLvl);
    r.parseAllData();
    assertEquals(1, r.getValidRegionFactors().getValidRegionFactors().size());
    assertTrue(r.getValidRegionFactors().isValidField("Climate"));
    assertEquals(2, ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).values().size());
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Normal"));
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Humid"));
    assertFalse(r.getValidRegionFactors().isValidValueForField("Climate", "Arid"));
    assertTrue(((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).defaultLevel().equals("Normal") || ((RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate")).defaultLevel().equals("Humid"));
  }

  @Test
  void testValidType() {
    stringToJnodeAndSetup(singleValidSetup);
    assertTrue(r.getValidRegionFactors().isValidField("Climate"));
    assertFalse(r.getValidRegionFactors().isValidField("Temperature"));
  }

  @Test
  void testValidTypeValue() {
    stringToJnodeAndSetup(singleValidSetup);
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Arid"));
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Normal"));
    assertTrue(r.getValidRegionFactors().isValidValueForField("Climate", "Humid"));
    assertFalse(r.getValidRegionFactors().isValidValueForField("Climate", "Hot"));
  }

  @Test
  void someInvalidRFs() {
    stringToJnodeAndSetup(someValidSetup);
    assertEquals(3, r.getValidRegionFactors().getValidRegionFactors().size());
    assertTrue(r.getValidRegionFactors().isValidField("Climate"));
    assertTrue(r.getValidRegionFactors().isValidField("Temperature"));
    assertTrue(r.getValidRegionFactors().isValidField("Density"));
    assertFalse(r.getValidRegionFactors().isValidField("Wealth"));
  }

  @Test
  void duplicateType() {
    stringToJnodeAndSetup(duplicateType);
    assertEquals(3, r.getValidRegionFactors().getValidRegionFactors().size());
    assertTrue(r.getValidRegionFactors().isValidField("Wealth"));
  }

  @Test
  void duplicateVal() {
    stringToJnodeAndSetup(duplicateValForType);
    RegionFactorRecord rfr = (RegionFactorRecord) r.getValidRegionFactors().getModelData("Climate");
    assertEquals(2, rfr.values().size());
    assertTrue(rfr.isValidValue("Arid"));
    assertTrue(rfr.isValidValue("Humid"));
    assertFalse(rfr.isValidValue("Normal"));
  }

  //immutability tests
  void setupRec() {
    Map<String, Double> map = new HashMap<>();
    map.put("Hot", 0.3);
    map.put("Normal", 1.0);
    map.put("Cold", 0.2);

    rec = new RegionFactorRecord("Temperature", "Normal", map);
  }

  @Test
  void checkRecordImmutable() {
    setupRec();
    assertThrows(UnsupportedOperationException.class, () -> {
      rec.values().clear();
    });
    rec.getValuesForType().clear();
    assertEquals(3, rec.values().size());
  }

  @Test
  void checkDataImmutable() {
    stringToJnodeAndSetup(singleValidSetup);
    assertThrows(UnsupportedOperationException.class, () -> {
      r.getValidRegionFactors().getValidRegionFactors().clear();
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      r.getValidRegionFactors().getRecordsList().clear();
    });
  }

  //Stream arg defns:
  static Stream<Arguments> typeAndValues() {
    return Stream.of(
        Arguments.of("Climate", Arrays.asList("Arid", "Normal", "Humid"), Arrays.asList(1.0,0.2,1.0)),
        Arguments.of("Temperature", Arrays.asList("Hot", "Normal", "Cold"), Arrays.asList(2.0,1.0,1.0)),
        Arguments.of("Wealth", Arrays.asList("Poor", "Normal", "Rich"), Arrays.asList(1.0,1.0,0.5)),
        Arguments.of("Density", Arrays.asList("Rural", "Normal", "Urban"), Arrays.asList(1.0,3.0,10.0))
    );
  }



  //String defns:
  String missingValuesTotal = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid"
          }
        ]
      }""";

  String[] invalidvalues = {
      """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": 
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 2.0
              }
          }
        ]
      }""", """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              
              ]
          }
        ]
      }"""
  };

  String valuesLvlBlank = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "  ",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";

  String badAntagVal = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": "hello"
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";

  String invalidAntagVal = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": -1
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";

  String valuesMissingLvl = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";

  String[] missingName = {
      """
      {
        "regionFactorTypes": [
          {
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""",
      """
      {
        "regionFactorTypes": [
          {
            "name": "   ",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }"""
  };




  String singleValidSetup = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";

  String someValidSetup = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Normal",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 0.2
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 1.0
              }
            ]
          },
          {
            "name": "Temperature",
            "defaultLevel": "Hot",
            "values": [
              {
                "level": "Hot",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Cold",
                "antagonistBaseEffectiveness": 1.0
              }
            ]
          },
          {
            "defaultLevel": "Rich",
            "values": [
              {
                "level": "Poor",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Rich",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          },
          {
            "name": "Density",
            "defaultLevel": "Urban",
            "values": [
              {
                "level": "Rural",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 3.0
              },
              {
                "level": "Urban",
                "antagonistBaseEffectiveness": 10.0
              }
            ]
          }
        ]
      }""";

  String duplicateType = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Normal",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 0.2
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 1.0
              }
            ]
          },
          {
            "name": "Temperature",
            "defaultLevel": "Hot",
            "values": [
              {
                "level": "Hot",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Cold",
                "antagonistBaseEffectiveness": 1.0
              }
            ]
          },
          {
            "name" : "Wealth",
            "defaultLevel": "Rich",
            "values": [
              {
                "level": "Poor",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Rich",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          },
          {
            "name": "Wealth",
            "defaultLevel": "Urban",
            "values": [
              {
                "level": "Rural",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Normal",
                "antagonistBaseEffectiveness": 3.0
              },
              {
                "level": "Urban",
                "antagonistBaseEffectiveness": 10.0
              }
            ]
          }
        ]
      }""";

  String duplicateValForType = """
      {
        "regionFactorTypes": [
          {
            "name": "Climate",
            "defaultLevel": "Arid",
            "values": [
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 2.0
              },
              {
                "level": "Arid",
                "antagonistBaseEffectiveness": 1.0
              },
              {
                "level": "Humid",
                "antagonistBaseEffectiveness": 0.5
              }
            ]
          }
        ]
      }""";
}
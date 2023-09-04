package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ooga.controller.parsers.math.MathJSON;
import ooga.controller.parsers.paths.PathsJSON;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesData;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesJSON;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesRecord;
import ooga.controller.parsers.protagonist_type.RegionFactorsModifiersWrapper;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//the only reason this is so long is that there's like 1600 lines of tester strings (:

class ProtagonistTypesJSONTest {

  JsonNode jnode;
  RegionFactorsJSON r = new RegionFactorsJSON();
  MathJSON m;
  PathsJSON pths;
  ProtagonistTypesJSON p;

  //expected
  static List<ProtagonistTypesRecord> expectedProtags;
  List<String> expectedcores = List.of("Infectivity", "Lethality", "Severity");

  //helper methods

  private void setup() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode regions = mapper.readTree(ParserTestObjectUtils.regionFactorSetup);
      r = new RegionFactorsJSON(regions);
      r.parseAllData();
      JsonNode math = mapper.readTree(ParserTestObjectUtils.mathSetup);
      m = new MathJSON(math, r.getValidRegionFactors());
      m.parseAllData();
      JsonNode paths = mapper.readTree(ParserTestObjectUtils.pathsSetup);
      pths = new PathsJSON(paths, m.getMathData().getSubpopsAndDependencies());
      pths.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  private void stringToJnodeAndSetup(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      p = new ProtagonistTypesJSON(jnode, r.getValidRegionFactors(), pths.getValidPaths());
      p.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  private void stringToJnode(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      p = new ProtagonistTypesJSON(jnode, r.getValidRegionFactors(), pths.getValidPaths());
    } catch (JsonProcessingException e) {
    }
  }

  @BeforeAll
  static void makeProtags() {
    expectedProtags = List.of(ParserTestObjectUtils.bacteriaProtag, ParserTestObjectUtils.virusProtag);
  }

  //immutable tests
  @Test
  void testImmutableData() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    assertThrows(UnsupportedOperationException.class, () -> {
      p.getProtagonistTypesData().getRecordsList().clear();
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      p.getProtagonistTypesData().getValidProtagonists().get(0).get("Core").clear();
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      p.getProtagonistTypesData().getValidProtagonists().get(0).allFactors().get("Core").clear();
    });
    p.getProtagonistTypesData().getValidProtagonists().clear();
    assertEquals(2, p.getProtagonistTypesData().getValidProtagonists().size());
  }

  @Test
  void testImmutableRecordProtagType() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");

    test.allFactors().clear();
    assertEquals(6, test.allFactors().keySet().size());

    test.getModifierNamesForType("Climate").clear();
    assertEquals(3, test.getModifierNamesForType("Climate").size());
    assertThrows(UnsupportedOperationException.class, () -> {
      test.allFactors().get("Climate").clear();
      test.get("Climate").clear();
    });
  }

  //valid setup tests
  @Test
  void testExpectedcorefactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    List<String> actualCoreFactors = p.getCoreFactors();
    assertEquals(expectedcores.size(), actualCoreFactors.size());
    for (String s : expectedcores) {
      assertTrue(actualCoreFactors.contains(s));
    }
  }

  @Test
  void testName() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    assertEquals(2, p.getProtagonistTypesData().getValidProtagonists().size());
    assertTrue(p.getProtagonistTypesData().isValidField("Bacteria"));
    assertTrue(p.getProtagonistTypesData().isValidField("Virus"));
  }

  @Test
  void testNumFactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    assertEquals(6, p.getProtagonistTypesData().getValidProtagonists().get(0).allFactors().keySet().size());
  }

  @Test
  void testcorefactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    for (ProtagonistTypesRecord protag : expectedProtags) {
      ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData().getModelData(protag.name());
      assertEquals(protag.get("Core").size(), test.get("Core").size());
      assertEquals(3, test.get("Core").size());
      for (ModifierRecord pmr : protag.get("Core")) {
        assertTrue(test.getModifierNamesForType("Core").contains(pmr.name()));
        assertEquals(pmr.amount(), test.getModifierByTypeAndName("Core", pmr.name()).amount());
      }
    }
  }

  @Test
  void testPathFactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    for (ProtagonistTypesRecord protag : expectedProtags) {
      ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData().getModelData(protag.name());
      assertEquals(protag.get("Path").size(), test.get("Path").size());
      assertEquals(3, test.get("Path").size());
      for (ModifierRecord pmr : protag.get("Path")) {
        assertTrue(test.getModifierNamesForType("Path").contains(pmr.name()));
        assertEquals(pmr.amount(), test.getModifierByTypeAndName("Path", pmr.name()).amount());
      }
    }
  }

  @Test
  void testRegionFactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.protagonistSetup);
    String[] expectedRFtypes = {"Climate", "Density", "Wealth", "Temperature"};
    for (ProtagonistTypesRecord protag : expectedProtags) {
      ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData().getModelData(protag.name());
      for (String s : expectedRFtypes) {
        assertEquals(protag.get(s).size(), test.get(s).size());
        assertEquals(3, test.get(s).size());
        for (ModifierRecord mr : protag.get(s)) {
          assertTrue(test.getModifierNamesForType(s).contains(mr.name()));
          assertEquals(mr.amount(), test.getModifierByTypeAndName(s, mr.name()).amount());
        }
      }
    }
  }

  //invalid tests
  @Test
  void missingcorefactors() {
    for (String s : missingCoreFactors) {
      stringToJnodeAndSetup(s);
      ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
          .getModelData("Bacteria");
      assertTrue(test.get("Core").isEmpty());
    }
  }

  @Test
  void setOneInvalidCorefactor() {
    stringToJnodeAndSetup(oneInvalidCorefactor);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertEquals(2, test.get("Core").size());
    for (String s : new String[]{"Lethality", "Severity"}) {
      assertTrue(test.getModifierNamesForType("Core").contains(s));
    }
    assertEquals(0.1, test.getModifierByTypeAndName("Core","Lethality").amount());
    assertFalse(test.getModifierNamesForType("Core").contains("Infectivity"));
    assertThrows(NullPointerException.class, () -> {
      test.getModifierByTypeAndName("Core", "Infectivity");
    });
  }

  @Test
  void setNoProtagOptions() {
    stringToJnode(noProtagOptions);
    assertThrows(ConfigJSONException.class, () -> {
      p.parseAllData();
    });
  }

  @Test
  void nonArrayProtagOptions() {
    stringToJnode(nonArrayProtagOptions);
    assertThrows(ConfigJSONException.class, () -> {
      p.parseAllData();
    });
  }

  @Test
  void oneInvalidProtag() {
    for (String s : oneInvalidProtag) {
      stringToJnodeAndSetup(s);
      assertEquals(1, p.getProtagonistTypesData().getValidProtagonists().size());
      assertTrue(p.getProtagonistTypesData().isValidField("Bacteria"));
      assertTrue(p.getProtagonistTypesData().getProtagonistByID(0).name().equals("Bacteria"));
      assertThrows(NullPointerException.class, () -> p.getProtagonistTypesData().getProtagonistByID(1));
    }
  }

  @Test
  void duplicatedName() {
    stringToJnodeAndSetup(duplicatedName);
    assertEquals(2, p.getProtagonistTypesData().getValidProtagonists().size());
    assertEquals("Bacteria", p.getProtagonistTypesData().getProtagonistByID(0).name());
    assertEquals("Bacteria", p.getProtagonistTypesData().getProtagonistByID(1).name());
  }

  @Test
  void unrecognizedPathModifier() {
    stringToJnodeAndSetup(unrecognizedPathModifier);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertEquals(3, test.get("Path").size());
    assertTrue(test.getModifierNamesForType("Path").contains("Sea"));
    assertEquals(1.0, test.getModifierByTypeAndName("Path", "Sea").amount());
    assertFalse(test.getModifierNamesForType("Path").contains("Underground"));
  }

  @Test
  void unrecognizedCoreFactor() {
    stringToJnodeAndSetup(unrecognizedCoreFactor);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertEquals(3, test.get("Core").size());
    assertTrue(test.getModifierNamesForType("Core").contains("Lethality"));
    assertEquals(0, test.getModifierByTypeAndName("Core", "Lethality").amount());
    assertFalse(test.getModifierNamesForType("Core").contains("contagiousness"));
  }

  @Test
  void missingname() {
    for (String s : noName) {
      stringToJnodeAndSetup(s);
      assertEquals(1, p.getProtagonistTypesData().getValidProtagonists().size());
      assertEquals("Protagonist 0", p.getProtagonistTypesData().getProtagonistByID(0).name());
    }
  }

  @Test
  void unrecognizedRFtype() {
    stringToJnodeAndSetup(unrecognizedRFType);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertTrue(test.isValidModifierType("Wealth"));
    assertFalse(test.isValidModifierType("Wealthiness"));
    assertEquals(3, test.get("Wealth").size());
    String[] arr = {"Rich", "Normal", "Poor"};
    for (String s : arr) {
      assertTrue(test.isValidModifierForType("Wealth", s));
      assertEquals(1, test.getModifierByTypeAndName("Wealth", s).amount());
    }
  }

  @Test
  void unrecognizedRFval() {
    stringToJnodeAndSetup(unrecognizedRFValue);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertEquals(3, test.get("Temperature").size());
    String[] arr = {"Hot", "Normal", "Cold"};

    assertTrue(test.isValidModifierForType("Temperature", "Hot"));
    assertEquals(0.13, test.getModifierByTypeAndName("Temperature", "Hot").amount());
    assertTrue(test.isValidModifierForType("Temperature", "Normal"));
    assertEquals(1, test.getModifierByTypeAndName("Temperature", "Normal").amount());
    assertTrue(test.isValidModifierForType("Temperature", "Cold"));
    assertEquals(1, test.getModifierByTypeAndName("Temperature", "Cold").amount());

    assertFalse(test.isValidModifierForType("Temperature", "temperate"));
  }

  @Test
  void invalidVal() {
    stringToJnodeAndSetup(invalidValForModifier);
    ProtagonistTypesRecord test = (ProtagonistTypesRecord) p.getProtagonistTypesData()
        .getModelData("Bacteria");
    assertEquals(3, test.get("Wealth").size());
    assertTrue(test.isValidModifierForType("Wealth", "Poor"));
    assertTrue(test.isValidModifierForType("Wealth", "Normal"));
    assertTrue(test.isValidModifierForType("Wealth", "Rich"));
    assertEquals(1, test.getModifierByTypeAndName("Wealth", "Poor").amount());
  }

  //String defns


  String[] missingCoreFactors = {"""
      {
        "protagonistTypes": {
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """,
      """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [""],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """};
  String oneInvalidCorefactor = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            " ",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;
  String noProtagOptions = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "options": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;
  String nonArrayProtagOptions = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions":
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }

        }
      }
      """;
  String[] oneInvalidProtag = {"""
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            },
            {
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.45
                },
                {
                  "name": "Lethality",
                  "amount": 0.23
                },
                {
                  "name": "Severity",
                  "amount": 0.64
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.32
                    },
                    {
                      "name": "Hot",
                      "amount": 0.24
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.65
                    },
                    {
                      "name": "Humid",
                      "amount": 0.53
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.42
                    },
                    {
                      "name": "Urban",
                      "amount": 1.43
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.55
                    },
                    {
                      "name": "Poor",
                      "amount": 1.76
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.57
                },
                {
                  "name": "Air",
                  "amount": 0.34
                },
                {
                  "name": "Sea",
                  "amount": 0.41
                }
              ]
            }
          ]
        }
      }
      """,
      """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            },
            {
              "name": "Virus",
              "description": "Also small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.45
                },
                {
                  "name": "Lethality",
                  "amount": 0.23
                },
                {
                  "name": "Severity",
                  "amount": 0.64
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.32
                    },
                    {
                      "name": "Hot",
                      "amount": 0.24
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.65
                    },
                    {
                      "name": "Humid",
                      "amount": 0.53
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.42
                    },
                    {
                      "name": "Urban",
                      "amount": 1.43
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.55
                    },
                    {
                      "name": "Poor",
                      "amount": 1.76
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.57
                },
                {
                  "name": "Air",
                  "amount": 0.34
                },
                {
                  "name": "Sea",
                  "amount": 0.41
                }
              ]
            }
          ]
        }
      }
      """,
      """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            },
            {
              "id": "hello",
              "name": "Virus",
              "description": "Also small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.45
                },
                {
                  "name": "Lethality",
                  "amount": 0.23
                },
                {
                  "name": "Severity",
                  "amount": 0.64
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.32
                    },
                    {
                      "name": "Hot",
                      "amount": 0.24
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.65
                    },
                    {
                      "name": "Humid",
                      "amount": 0.53
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.42
                    },
                    {
                      "name": "Urban",
                      "amount": 1.43
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.55
                    },
                    {
                      "name": "Poor",
                      "amount": 1.76
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.57
                },
                {
                  "name": "Air",
                  "amount": 0.34
                },
                {
                  "name": "Sea",
                  "amount": 0.41
                }
              ]
            }
          ]
        }
      }
      """,
      """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            },
            {
              "id": 0,
              "name": "Virus",
              "description": "Also small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.45
                },
                {
                  "name": "Lethality",
                  "amount": 0.23
                },
                {
                  "name": "Severity",
                  "amount": 0.64
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.32
                    },
                    {
                      "name": "Hot",
                      "amount": 0.24
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.65
                    },
                    {
                      "name": "Humid",
                      "amount": 0.53
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.42
                    },
                    {
                      "name": "Urban",
                      "amount": 1.43
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.55
                    },
                    {
                      "name": "Poor",
                      "amount": 1.76
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.57
                },
                {
                  "name": "Air",
                  "amount": 0.34
                },
                {
                  "name": "Sea",
                  "amount": 0.41
                }
              ]
            }
          ]
        }
      }
      """
  };
  String duplicatedName = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            },
            {
              "id": 1,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.45
                },
                {
                  "name": "Lethality",
                  "amount": 0.23
                },
                {
                  "name": "Severity",
                  "amount": 0.64
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.32
                    },
                    {
                      "name": "Hot",
                      "amount": 0.24
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.65
                    },
                    {
                      "name": "Humid",
                      "amount": 0.53
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.42
                    },
                    {
                      "name": "Urban",
                      "amount": 1.43
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.55
                    },
                    {
                      "name": "Poor",
                      "amount": 1.76
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.57
                },
                {
                  "name": "Air",
                  "amount": 0.34
                },
                {
                  "name": "Sea",
                  "amount": 0.41
                }
              ]
            }
          ]
        }
      }
      """;

  String[] noName = {"""
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": " ",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """,
      """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """
  };


  String unrecognizedPathModifier = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Underground",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;

  String unrecognizedCoreFactor = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "contagiousness",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;

  String unrecognizedRFType = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealthiness",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;
  String unrecognizedRFValue = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "temperate",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": 1.15
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;
  String invalidValForModifier = """
      {
        "protagonistTypes": {
          "expectedCoreFactors": [
            "Infectivity",
            "Lethality",
            "Severity"
          ],
          "protagonistOptions": [
            {
              "id": 0,
              "name": "Bacteria",
              "description": "Small but deadly!",
              "image": "/protagonist_icons/bacteria.png",
              "Core": [
                {
                  "name": "Infectivity",
                  "amount": 0.2
                },
                {
                  "name": "Lethality",
                  "amount": 0.1
                },
                {
                  "name": "Severity",
                  "amount": 0.15
                }
              ],
              "Region": [
                {
                  "type": "Temperature",
                  "factors": [
                    {
                      "name": "Cold",
                      "amount": 0.12
                    },
                    {
                      "name": "Hot",
                      "amount": 0.13
                    }
                  ]
                },
                {
                  "type": "Climate",
                  "factors": [
                    {
                      "name": "Arid",
                      "amount": 0.32
                    },
                    {
                      "name": "Humid",
                      "amount": 0.22
                    }
                  ]
                },
                {
                  "type": "Density",
                  "factors": [
                    {
                      "name": "Rural",
                      "amount": 0.18
                    },
                    {
                      "name": "Urban",
                      "amount": 1.1
                    }
                  ]
                },
                {
                  "type": "Wealth",
                  "factors": [
                    {
                      "name": "Rich",
                      "amount": 0.14
                    },
                    {
                      "name": "Poor",
                      "amount": "hello!"
                    }
                  ]
                }
              ],
              "Path": [
                {
                  "name": "Land",
                  "amount": 0.54
                },
                {
                  "name": "Air",
                  "amount": 0.45
                },
                {
                  "name": "Sea",
                  "amount": 0.32
                }
              ]
            }
          ]
        }
      }
      """;
}
package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ooga.controller.parsers.math.SubPopulationData;
import ooga.controller.parsers.paths.PathsJSON;
import ooga.controller.parsers.region_factors.RegionFactorData;
import ooga.controller.parsers.region_factors.RegionFactorRecord;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import ooga.controller.parsers.regions.RegionPathRecord;
import ooga.controller.parsers.regions.RegionRecord;
import ooga.controller.parsers.regions.RegionsData;
import ooga.controller.parsers.regions.RegionsJSON;
import org.junit.jupiter.api.Test;

class RegionsJSONTest {

  //setup
  RegionFactorsJSON rfj;
  RegionFactorData rfd;
  SubPopulationData spd = new SubPopulationData(Set.of("Infected", "Dead"), "Healthy");
  PathsJSON pj;

  private Map<Integer, RegionRecord> expected = new HashMap<>();
  private List<Integer> okayIDs = new ArrayList<>();

  //testing
  private JsonNode jnode;
  RegionsJSON regionsJSON;
  RegionsData regionsData;

  private void setup() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode regionFactors = mapper.readTree(ParserTestObjectUtils.regionFactorSetup);
      rfj = new RegionFactorsJSON(regionFactors);
      rfj.parseAllData();
      rfd = rfj.getValidRegionFactors();
      JsonNode pathsnode = mapper.readTree(ParserTestObjectUtils.pathsSetup);
      pj = new PathsJSON(pathsnode, spd);
      pj.parseAllData();
    } catch (JsonProcessingException ignored) {
    }
    expected.put(ParserTestObjectUtils.region1.id(), ParserTestObjectUtils.region1);
    okayIDs.add(ParserTestObjectUtils.region1.id());
    expected.put(ParserTestObjectUtils.region2.id(), ParserTestObjectUtils.region2);
    okayIDs.add(ParserTestObjectUtils.region2.id());
    expected.put(ParserTestObjectUtils.region3.id(), ParserTestObjectUtils.region3);
    okayIDs.add(ParserTestObjectUtils.region3.id());
    expected.put(ParserTestObjectUtils.region4.id(), ParserTestObjectUtils.region4);
    okayIDs.add(ParserTestObjectUtils.region4.id());
  }

  private void stringToJnodeAndSetupSpecifyList(String in, List<Integer> okIDs) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      regionsJSON = new RegionsJSON(jnode, rfd, spd, pj.getValidPaths(),
          okIDs);
      regionsJSON.parseAllData();
      regionsData = regionsJSON.getRegionData();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void stringToJnodeAndSetup(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      regionsJSON = new RegionsJSON(jnode, rfj.getValidRegionFactors(), spd, pj.getValidPaths(),
          okayIDs);
      regionsJSON.parseAllData();
      regionsData = regionsJSON.getRegionData();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testNormalSetupLite() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionsSetup);
    assertEquals(4, regionsData.getValidRegions().size());
  }

  @Test
  void NormalSetupInDepthBasicVals() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionsSetup);
    for (RegionRecord actual : regionsData.getValidRegions()) {
      RegionRecord compare = expected.get(actual.id());
      assertEquals(compare.name(), actual.name());
      assertEquals(compare.description(), actual.description());
      assertEquals(compare.startingPopulation(), actual.startingPopulation());
      assertEquals(compare.detectionThreshold(), actual.detectionThreshold());
      assertEquals(compare.closedThreshold(), actual.closedThreshold());
      assertEquals(compare.color(), actual.color());
      assertEquals(compare.baseAntagonistRate(), actual.baseAntagonistRate());
    }
  }

  @Test
  void NormalSetupInDepthPaths() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionsSetup);
    for (RegionRecord actual : regionsData.getValidRegions()) {
      RegionRecord compare = expected.get(actual.id());
      assertEquals(compare.paths().size(), actual.paths().size());
      for (RegionPathRecord rpr : actual.paths()) {
        RegionPathRecord compareRPR = compare.getRegionPathRecord(rpr.id());
        assertEquals(compareRPR.connections().size(), rpr.connections().size());
        for (int id : rpr.connections()) {
          assertTrue(compareRPR.connections().contains(id));
        }
        assertEquals(compareRPR.activity(), rpr.activity());
      }
    }
  }

  @Test
  void NormalSetupInDepthFactors() {
    stringToJnodeAndSetup(ParserTestObjectUtils.regionsSetup);
    for (RegionRecord actual : regionsData.getValidRegions()) {
      RegionRecord compare = expected.get(actual.id());
      assertEquals(compare.factors().size(), actual.factors().size());
      for (String type : actual.factors().keySet()) {
        assertEquals(compare.factors().get(type), actual.factors().get(type));
      }
    }
  }

  //invalid setups
  @Test
  void badID() {
    stringToJnodeAndSetupSpecifyList(badID, List.of(1));
    assertEquals(1, regionsData.getValidRegions().size());
    assertTrue(regionsData.isValidRegionID(1));
  }

  @Test
  void supplementRegions() {
    stringToJnodeAndSetup(noName);
    assertEquals(4, regionsData.getValidRegions().size());
    for (RegionRecord rr : regionsData.getValidRegions()) {
      assertEquals(String.format("Region %d", rr.id()), rr.name());
      if (rr.id() != 1) {
        assertEquals(3, rr.paths().size());
        for (RegionPathRecord rpr : rr.paths()) {
          assertTrue(rpr.connections().isEmpty());
          assertEquals(RegionsJSON.DEFAULT_PATH_ACTIVITY, rpr.activity());
        }
        assertEquals(4, rr.factors().size());
        double num = 0;
        for (String type : rr.factors().keySet()) {
          assertEquals(((RegionFactorRecord) rfd.getModelData(type)).defaultLevel(),
              rr.factors().get(type));
          num += (((RegionFactorRecord) rfd.getModelData(type)).getAntagBaseVal(
              rr.factors().get(type)));
        }
        assertEquals(num / 4, rr.baseAntagonistRate());
        assertEquals(RegionsJSON.DEFAULT_STARTING_POPULATION, rr.startingPopulation());
        assertEquals(RegionsJSON.DEFAULT_CLOSE_THRESHOLD, rr.closedThreshold());
        assertEquals(RegionsJSON.DEFAULT_DETECTION_THRESHOLD, rr.detectionThreshold());
        assertTrue(rr.color().matches("#(?:[0-9a-fA-F]{3}){1,2}$"));
      }
    }
  }

  @Test
  void supplementRegionsConnections() {
    stringToJnodeAndSetup(noName);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(2, test.getRegionPathRecord(1).connections().size());
    assertEquals(Set.of(2, 3), test.getRegionPathRecord(1).connections());
  }

  @Test
  void noName() {
    stringToJnodeAndSetupSpecifyList(noName, List.of(1));
    assertEquals(1, regionsData.getValidRegions().size());
    assertEquals("Region 1", regionsData.getRegionByID(1).name());
  }

  @Test
  void noDescription() {
    stringToJnodeAndSetup(noDescription);
    assertEquals("", regionsData.getRegionByID(1).description());
  }

  @Test
  void unrecognizedPathID() {
    stringToJnodeAndSetup(unrecognizedPathID);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(1, test.paths().size());
    assertTrue(test.hasPathOfID(1));
    assertFalse(test.hasPathOfID(3));
  }

  @Test
  void noConnectionsValidPath() {
    stringToJnodeAndSetup(noConnectionsValidPath);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(2, test.paths().size());
    assertTrue(test.hasPathOfID(1));
    assertTrue(test.hasPathOfID(2));
    assertTrue(test.getRegionPathRecord(2).connections().isEmpty());
    assertFalse(test.getRegionPathRecord(1).connections().isEmpty());
  }

  @Test
  void badPathActivity() {
    stringToJnodeAndSetup(badPathActivity);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(2, test.paths().size());
    for (RegionPathRecord rpr : test.paths()) {
      assertEquals(0.05, rpr.activity());
    }
  }

  @Test
  void badRegionFactorVal() {
    stringToJnodeAndSetup(badRegionFactorVal);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(4, test.factors().size());
    assertEquals("Normal", test.factors().get("Climate"));
    for (String s : new String[]{"Temperature", "Density", "Wealth"}) {
      assertEquals(expected.get(1).factors().get(s), test.factors().get(s));
    }
  }

  @Test
  void badRegionFactor() {
    stringToJnodeAndSetup(badRegionFactor);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(4, test.factors().size());
    assertEquals("Normal", test.factors().get("Climate"));
    for (String s : new String[]{"Temperature", "Density", "Wealth"}) {
      assertEquals(expected.get(1).factors().get(s), test.factors().get(s));
    }
    assertFalse(test.factors().containsKey("what"));
  }

  @Test
  void noAntagBaseVal() {
    stringToJnodeAndSetup(noBaseAntagVal);
    RegionRecord test = regionsData.getRegionByID(1);
    assertEquals(1.25, test.baseAntagonistRate());
  }

  @Test
  void invalidClaimedPath() {
    stringToJnodeAndSetup(invalidClaimedPath);
    for (RegionRecord rr : regionsData.getValidRegions()) {
      if (rr.id() != 4) {
        assertTrue(rr.hasPathOfID(2));
        assertFalse(rr.getRegionPathRecord(2).connections().contains(4));
      } else {
        assertFalse(rr.hasPathOfID(2));
      }
    }
  }

  @Test
  void claimedSelfConnection() {
    stringToJnodeAndSetup(claimedSelfConnection);
    RegionRecord test = regionsData.getRegionByID(1);
    assertFalse(test.getRegionPathRecord(2).connections().contains(1));
    assertEquals(1, test.getRegionPathRecord(2).connections().size());
  }

  @Test
  void dropR1OutgoingPathsButStillHasIncoming() {
    stringToJnodeAndSetup(noOutgoingPathsButIncoming);
    RegionRecord r1 = regionsData.getRegionByID(1);
    assertTrue(r1.hasPathOfID(1));
    assertTrue(r1.hasPathOfID(2));
    assertTrue(r1.getRegionPathRecord(1).connections().isEmpty());
    assertTrue(r1.getRegionPathRecord(2).connections().isEmpty());
    for (int i : new int[]{2, 3, 4}) {
      RegionRecord test = regionsData.getRegionByID(i);
      assertTrue(test.getRegionPathRecord(1).connections().contains(1));
      if (test.hasPathOfID(2)) {
        assertTrue(test.getRegionPathRecord(2).connections().contains(1));
      }
    }
  }

  @Test
  void badPathcONnectionsLayered() {
    stringToJnodeAndSetup(badpathconnectionlayered);
    assertTrue(regionsData.isValidRegionID(3));
    assertFalse(regionsData.isValidRegionID(0));
    for (RegionRecord rr : regionsData) {
      for (RegionPathRecord rpr : rr.paths()) {
        assertFalse(rpr.connections().contains(0));
        if (rr.id() != 3) {
          assertTrue(rpr.connections().contains(3));
        }

      }
    }
  }

  @Test
  void badColor() {
    for (String s : badcolor) {
      stringToJnodeAndSetup(s);
      RegionRecord r1 = regionsData.getRegionByID(1);
      assertTrue(r1.color().matches("#(?:[0-9a-fA-F]{3}){1,2}$"));
    }
  }


  //String defns
  String badID = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          },
          {
            "name": "country 2",
            "description": "this is country 2",
            "paths": [{
              "id": 1,
              "connections": [3],
              "activity": 0.09
            },
              {
                "id": 2,
                "connections": [1, 3, 4],
                "activity": 0.14
              }
            ],
            "factors": {
              "Climate": "Humid",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Urban"
            },
            "startingPopulation": 2000000,
            "antagonistRate": 2000,
            "detectionThresh": 0.79,
            "closedThresh": 0.19,
            "color" : "#6b34eb"
          },
          {
            "id": 1,
            "name": "country 3",
            "description": "this is country 3",
            "paths": [{
              "id": 1,
              "connections": [1, 4],
              "activity": 0.03
            },
              {
                "id": 2,
                "connections": [1, 2],
                "activity": 0.32
              }
            ],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Cold",
              "Wealth": "Poor",
              "Density": "Normal"
            },
            "startingPopulation": 3000000,
            "antagonistRate": 3000,
            "detectionThresh": 0.68,
            "closedThresh": 0.22,
            "color" : "#c44da0"
          },
          {
            "id": 0,
            "name": "country 4",
            "description": "this is country 4",
            "paths": [{
              "id": 1,
              "connections": [1, 2],
              "activity": 0.12
            }],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Normal"
            },
            "startingPopulation": 4000000,
            "antagonistRate": 4000,
            "detectionThresh": 0.74,
            "closedThresh": 0.06,
            "color" : "#faf36e"
          }
        ]
      }
      """;

  String noName = """
      {
        "regions": [
          {
            "id": 1,
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String noDescription = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String unrecognizedPathID = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 3,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String noConnectionsValidPath = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String badPathActivity = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3]
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 100
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String badRegionFactorVal = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "temperate",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String badRegionFactor = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "what": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;
  String noBaseAntagVal = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String invalidClaimedPath = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [3, 2, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          },
          {
            "id": 2,
            "name": "country 2",
            "description": "this is country 2",
            "paths": [{
              "id": 1,
              "connections": [3],
              "activity": 0.09
            },
              {
                "id": 2,
                "connections": [1, 3, 4],
                "activity": 0.14
              }
            ],
            "factors": {
              "Climate": "Humid",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Urban"
            },
            "startingPopulation": 2000000,
            "antagonistRate": 2000,
            "detectionThresh": 0.79,
            "closedThresh": 0.19,
            "color" : "#6b34eb"
          },
          {
            "id": 3,
            "name": "country 3",
            "description": "this is country 3",
            "paths": [{
              "id": 1,
              "connections": [1, 4],
              "activity": 0.03
            },
              {
                "id": 2,
                "connections": [1, 2],
                "activity": 0.32
              }
            ],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Cold",
              "Wealth": "Poor",
              "Density": "Normal"
            },
            "startingPopulation": 3000000,
            "antagonistRate": 3000,
            "detectionThresh": 0.68,
            "closedThresh": 0.22,
            "color" : "#c44da0"
          },
          {
            "id": 4,
            "name": "country 4",
            "description": "this is country 4",
            "paths": [{
              "id": 1,
              "connections": [1, 2],
              "activity": 0.12
            }],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Normal"
            },
            "startingPopulation": 4000000,
            "antagonistRate": 4000,
            "detectionThresh": 0.74,
            "closedThresh": 0.06,
            "color" : "#faf36e"
          }
        ]
      }
      """;

  String claimedSelfConnection = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [1, 4],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          }
        ]
      }
      """;

  String badpathconnectionlayered = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [2, 3],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [2, 4, 3],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          },
          {
            "id": 2,
            "name": "country 2",
            "description": "this is country 2",
            "paths": [{
              "id": 1,
              "connections": [3],
              "activity": 0.09
            },
              {
                "id": 2,
                "connections": [1, 0, 4, 3],
                "activity": 0.14
              }
            ],
            "factors": {
              "Climate": "Humid",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Urban"
            },
            "startingPopulation": 2000000,
            "antagonistRate": 2000,
            "detectionThresh": 0.79,
            "closedThresh": 0.19,
            "color" : "#6b34eb"
          },
          {
            "id": 0,
            "name": "country 3",
            "description": "this is country 3",
            "paths": [{
              "id": 1,
              "connections": [1, 4],
              "activity": 0.03
            },
              {
                "id": 2,
                "connections": [1, 2],
                "activity": 0.32
              }
            ],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Cold",
              "Wealth": "Poor",
              "Density": "Normal"
            },
            "startingPopulation": 3000000,
            "antagonistRate": 3000,
            "detectionThresh": 0.68,
            "closedThresh": 0.22,
            "color" : "#c44da0"
          },
          {
            "id": 4,
            "name": "country 4",
            "description": "this is country 4",
            "paths": [{
              "id": 1,
              "connections": [1, 2,3],
              "activity": 0.12
            }],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Normal"
            },
            "startingPopulation": 4000000,
            "antagonistRate": 4000,
            "detectionThresh": 0.74,
            "closedThresh": 0.06,
            "color" : "#faf36e"
          }
        ]
      }
      """;

  String noOutgoingPathsButIncoming = """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [1, 100,  0],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#34d5eb"
          },
          {
            "id": 2,
            "name": "country 2",
            "description": "this is country 2",
            "paths": [{
              "id": 1,
              "connections": [1, 3],
              "activity": 0.09
            },
              {
                "id": 2,
                "connections": [1, 3, 4],
                "activity": 0.14
              }
            ],
            "factors": {
              "Climate": "Humid",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Urban"
            },
            "startingPopulation": 2000000,
            "antagonistRate": 2000,
            "detectionThresh": 0.79,
            "closedThresh": 0.19,
            "color" : "#6b34eb"
          },
          {
            "id": 3,
            "name": "country 3",
            "description": "this is country 3",
            "paths": [{
              "id": 1,
              "connections": [1, 4],
              "activity": 0.03
            },
              {
                "id": 2,
                "connections": [1, 2],
                "activity": 0.32
              }
            ],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Cold",
              "Wealth": "Poor",
              "Density": "Normal"
            },
            "startingPopulation": 3000000,
            "antagonistRate": 3000,
            "detectionThresh": 0.68,
            "closedThresh": 0.22,
            "color" : "#c44da0"
          },
          {
            "id": 4,
            "name": "country 4",
            "description": "this is country 4",
            "paths": [{
              "id": 1,
              "connections": [1, 2],
              "activity": 0.12
            }],
            "factors": {
              "Climate": "Normal",
              "Temperature": "Normal",
              "Wealth": "Rich",
              "Density": "Normal"
            },
            "startingPopulation": 4000000,
            "antagonistRate": 4000,
            "detectionThresh": 0.74,
            "closedThresh": 0.06,
            "color" : "#faf36e"
          }
        ]
      }
      """;

  String[] badcolor = {
      """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [1, 100,  0],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24
          }]}""",
      """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [1, 100,  0],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "nope"
          }]}""",
      """
      {
        "regions": [
          {
            "id": 1,
            "name": "country 1",
            "description": "this is country 1",
            "paths": [{
              "id": 1,
              "connections": [],
              "activity": 0.05
            },
              {
                "id": 2,
                "connections": [1, 100,  0],
                "activity": 0.12
              }
            ],
            "factors": {
              "Climate": "Arid",
              "Temperature": "Hot",
              "Wealth": "Normal",
              "Density": "Rural"
            },
            "startingPopulation": 1000000,
            "antagonistRate": 1000,
            "detectionThresh": 0.68,
            "closedThresh": 0.24,
            "color" : "#GA4deb"
          }]}"""};
}




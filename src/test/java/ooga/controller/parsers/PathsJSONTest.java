package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ooga.controller.parsers.math.MathJSON;
import ooga.controller.parsers.paths.PathRecord;
import ooga.controller.parsers.paths.PathsJSON;
import ooga.controller.parsers.paths.SubPopSpreadRecord;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import org.junit.jupiter.api.Test;

class PathsJSONTest {

  JsonNode jnode;
  RegionFactorsJSON r = new RegionFactorsJSON();
  PathsJSON p;
  MathJSON m;

  PathRecord[] expectedRecords;

  void setup() {
    makeValidPaththings();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode regions = mapper.readTree(ParserTestObjectUtils.regionFactorSetup);
      r = new RegionFactorsJSON(regions);
      r.parseAllData();
      JsonNode math = mapper.readTree(ParserTestObjectUtils.mathSetup);
      m = new MathJSON(math, r.getValidRegionFactors());
      m.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  void makeValidPaththings() {
    expectedRecords = new PathRecord[]{ParserTestObjectUtils.landPath, ParserTestObjectUtils.airPath, ParserTestObjectUtils.seaPath};
  }

  void stringToJnodeAndSetup(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      p = new PathsJSON(jnode, m.getMathData().getSubpopsAndDependencies());
      p.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  void stringToJnode(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      p = new PathsJSON(jnode, m.getMathData().getSubpopsAndDependencies());
    } catch (JsonProcessingException e) {
    }
  }

  //valid tests
  @Test
  void testIDsAndNames() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      assertTrue(p.getValidPaths().isValidPathName(pr.name()));
      assertTrue(p.getValidPaths().isValidPathID(pr.id()));
      assertEquals(pr.name(), p.getValidPaths().getPathRecordByID(pr.id()).name());
    }
    assertEquals(expectedRecords.length, p.getValidPaths().getValidPaths().size());
  }

  @Test
  void testImgPath() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      assertEquals(pr.imgPath(), p.getValidPaths().getPathRecordByID(pr.id()).imgPath());
    }
  }

  @Test
  void testPortImgPath() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      assertEquals(pr.portImgPath(), p.getValidPaths().getPathRecordByID(pr.id()).portImgPath());
    }
  }

  @Test
  void testAnimationType() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      assertEquals(pr.animationType(), p.getValidPaths().getPathRecordByID(pr.id()).animationType());
    }
  }

  @Test
  void testSymbol() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      assertEquals(pr.symbol(), p.getValidPaths().getPathRecordByID(pr.id()).symbol());
      assertEquals((pr.id() != 0), p.getValidPaths().getPathRecordByID(pr.id()).hasValidPortSymbol());
    }
  }

  @Test
  void testSpreadMap() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    for (PathRecord pr : expectedRecords) {
      PathRecord tester = p.getValidPaths().getPathRecordByID(pr.id());
      assertEquals(pr.subpopSpread().size(), tester.subpopSpread().size());
      assertEquals(pr.peopleSpread(), tester.peopleSpread());
      for (SubPopSpreadRecord sr : pr.subpopSpread()) {
        assertTrue(tester.spreadsThisSubpop(sr.popName()));
        assertEquals(sr.pointsWorth(), tester.getPointsForSubpop(sr.popName()));
      }
    }
  }

  //invalid tests

  @Test
  void testNoPathNode() {
    stringToJnode(noPathNode);
    assertThrows(ConfigJSONException.class, () -> {
      p.parseAllData();
    });
  }

  @Test
  void testNoValidPaths() {
    stringToJnodeAndSetup(allPathsInvalidID);
    assertTrue(p.getValidPaths().getValidPaths().isEmpty());
  }

  @Test
  void testDuplicateID() {
    stringToJnodeAndSetup(duplicateID);
    assertEquals(2, p.getValidPaths().getValidPaths().size());
    assertTrue(p.getValidPaths().isValidPathID(0));
    assertEquals("Land", p.getValidPaths().getPathRecordByID(0).name());
    assertTrue(p.getValidPaths().isValidPathID(1));
    assertEquals("Air", p.getValidPaths().getPathRecordByID(1).name());
    assertFalse(p.getValidPaths().isValidPathName("Sea"));
  }

  @Test
  void testMissingName() {
    stringToJnodeAndSetup(missingName);
    assertEquals(3, p.getValidPaths().getValidPaths().size());
    assertTrue(p.getValidPaths().isValidPathName("Land"));
    assertTrue(p.getValidPaths().isValidPathName("Path 1"));
    assertEquals("Path 1", p.getValidPaths().getPathRecordByID(1).name());
    assertTrue(p.getValidPaths().isValidPathName("Path 2"));
    assertEquals("Path 2", p.getValidPaths().getPathRecordByID(2).name());
  }

  @Test
  void testMissingFilePath() {
    stringToJnodeAndSetup(missingFilePath);
    assertTrue(p.getValidPaths().isValidPathName("Air"));
    assertEquals("", p.getValidPaths().getPathRecordByID(1).imgPath());
    assertEquals("", p.getValidPaths().getPathRecordByID(1).portImgPath());
  }

  @Test
  void testNoValidPathSymbol() {
    stringToJnodeAndSetup(noValidPathSymbol);
    assertFalse(p.getValidPaths().getPathRecordByID(0).hasValidPortSymbol());
    assertEquals("", p.getValidPaths().getPathRecordByID(0).symbol());
    assertFalse(p.getValidPaths().getPathRecordByID(1).hasValidPortSymbol());
    assertEquals("", p.getValidPaths().getPathRecordByID(1).symbol());
  }

  @Test
  void testPartiallyValidPortSymbol() {
    stringToJnodeAndSetup(partiallyValidPortSymbol);
    assertEquals(".symbol!", p.getValidPaths().getPathRecordByID(1).symbol());
    assertTrue(p.getValidPaths().getPathRecordByID(1).hasValidPortSymbol());
  }

  @Test
  void testInvalidAnimationType() {
    //already tested that missing anim entry gets replaced by "fade" when doing valid testing on Land PathRecord
    stringToJnodeAndSetup(invalidAnimationType);
    assertEquals("Fade", p.getValidPaths().getPathRecordByID(1).animationType());
  }

  @Test
  void testMissingSpreadInfo() {
    stringToJnodeAndSetup(missingSpreadInfo);
    for (int i = 0; i < 3; i++) {
      assertTrue(p.getValidPaths().getPathRecordByID(i).subpopSpread().isEmpty());
    }
  }

  @Test
  void testMissingPoints() {
    stringToJnodeAndSetup(missingPointsWorthAndSpreadnum);
    assertEquals(1, p.getValidPaths().getPathRecordByID(1).getSubPopSpreadRecord("Infected").pointsWorth());
    assertEquals(10, p.getValidPaths().getPathRecordByID(1).peopleSpread());
  }

  @Test
  void invalidSubpop() {
    stringToJnodeAndSetup(invalidSubpop);
    assertEquals(1, p.getValidPaths().getValidPaths().size());
    assertTrue(p.getValidPaths().getPathRecordByID(1).spreadsThisSubpop("Dead"));
    assertFalse(p.getValidPaths().getPathRecordByID(1).spreadsThisSubpop("alive n well"));
    assertEquals(1, p.getValidPaths().getPathRecordByID(1).getPointsForSubpop("Dead"));
  }

  @Test
  void duplicateName() {
    stringToJnodeAndSetup(duplicateName);
    assertEquals(3, p.getValidPaths().getValidPaths().size());
    assertEquals("Air", p.getValidPaths().getPathRecordByID(1).name());
    assertEquals("Path 2", p.getValidPaths().getPathRecordByID(2).name());
  }

  //immutable tests
  @Test
  void testImmutableRecord() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    assertThrows(UnsupportedOperationException.class, () -> {
      p.getValidPaths().getPathRecordByID(1).subpopSpread().clear();
    });
  }

  @Test
  void testImmutableData() {
    stringToJnodeAndSetup(ParserTestObjectUtils.pathsSetup);
    assertThrows(UnsupportedOperationException.class, () -> {
      p.getValidPaths().getValidPaths().clear();
    });
    p.getValidPaths().getValidPathNames().clear();
    assertEquals(3, p.getValidPaths().getValidPathNames().size());
  }

  //String defns:
  String noPathNode = """
      {
        "pths": [
          {
            "id" : 0,
            "name" : "Land",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 2,
            "name" : "Sea",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
                "type" : "Dead",
                "pointsWorth" : 3
              }
            ]
          }
        ]
      }
      """;

  String allPathsInvalidID = """
      {
        "paths": [
          {
            "id" : -1,
            "name" : "Land",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : "hello!",
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "name" : "Sea",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
                "type" : "Dead",
                "pointsWorth" : 3
              }
            ]
          }
        ]
      }
      """;

  String duplicateID = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 1,
            "name" : "Sea",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
                "type" : "Dead",
                "pointsWorth" : 3
              }
            ]
          }
        ]
      }
      """;
  String missingName = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 1,
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 2,
            "name" : " ",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
                "type" : "Dead",
                "pointsWorth" : 3
              }
            ]
          }
        ]
      }
      """;

  String missingFilePath = """
      {
        "paths": [
          {
            "id" : 1,
            "name" : "Air",
            "portImg" : "  ",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;

  String noValidPathSymbol = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 1
              }
            ]
          },
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : "9832  23421",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;

  String partiallyValidPortSymbol = """
      {
        "paths": [
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : "23  .32901symbol!  ",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;

  String invalidAnimationType = """
      {
        "paths": [
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "funky swirl",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;

  String missingSpreadInfo = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land"
          },
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "alive and well",
                "pointsWorth" : 2
              }
            ]
          },
          {
            "id" : 2,
            "name" : "Sea",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
              }
            ]
          }
        ]
      }
      """;

  String invalidSubpop = """
      {
        "paths": [
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "alive n well",
                "pointsWorth" : 2
              },
              {
                "type" : "Dead",
                "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;

  String missingPointsWorthAndSpreadnum = """
      {
        "paths": [
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Infected"
              }
            ]
          }
        ]
      }
      """;

  String duplicateName = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land"
          },
          {
            "id" : 1,
            "name" : "Air",
            "img" : "\\\\somedir\\\\air.png",
            "portImg" : "\\\\somedir\\\\airport.png",
            "animationType" : "path",
            "symbol" : ".",
            "subPopulationSpread" : [
              {
                "type" : "Healthy",
                "pointsWorth" : 2
              }
            ]
          },
          {
            "id" : 2,
            "name" : "Air",
            "img" : "\\\\somedir\\\\boat.png",
            "portImg" : "\\\\somedir\\\\seaport.png",
            "animationType" : "fade",
            "symbol" : "*",
            "subPopulationSpread" : [
              {
              "type" : "Dead",
              "pointsWorth" : 1
              }
            ]
          }
        ]
      }
      """;



}
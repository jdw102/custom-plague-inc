package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.math.MathJSON;
import ooga.controller.parsers.paths.PathsJSON;
import ooga.controller.parsers.perks.PerkRecord;
import ooga.controller.parsers.perks.PerksJSON;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesJSON;
import ooga.controller.parsers.region_factors.RegionFactorsJSON;
import org.junit.jupiter.api.Test;

class PerksJSONTest {

  JsonNode jnode;
  RegionFactorsJSON r = new RegionFactorsJSON();
  MathJSON m;
  PathsJSON pths;
  ProtagonistTypesJSON pts;
  PerksJSON prk;

  //expected
  List<PerkRecord> expectedPerks = List.of(ParserTestObjectUtils.air1Perk, ParserTestObjectUtils.coughingPerk, ParserTestObjectUtils.bird1Perk);

  //setup helper methods
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
      JsonNode protgs = mapper.readTree(ParserTestObjectUtils.protagonistSetup);
      pts = new ProtagonistTypesJSON(protgs, r.getValidRegionFactors(), pths.getValidPaths());
      pts.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  private void stringToJnodeAndSetup(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      prk = new PerksJSON(jnode, pts.getProtagonistTypesData());
      prk.parseAllData();
    } catch (JsonProcessingException e) {
    }
  }

  private void stringToJnode(String in) {
    setup();
    ObjectMapper mapper = new ObjectMapper();
    try {
      jnode = mapper.readTree(in);
      prk = new PerksJSON(jnode, pts.getProtagonistTypesData());
    } catch (JsonProcessingException e) {
    }
  }

  //immutable tests
  @Test
  void immutableData() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    assertThrows(UnsupportedOperationException.class, () -> {
      prk.getPerkData().getRecordsList().clear();
    });
    prk.getPerkData().getValidPerks().clear();
    assertFalse(prk.getPerkData().getValidPerks().isEmpty());
    prk.getPerkData().getValidPerks().set(1, new PerkRecord(10, 0, "hi", "", 2, "Protagonist",Set.of(), List.of(new ModifierRecord("thing", "idk", 0.3)), ""));
    //assertFalse(prk.getPerkData().isValidField(String.valueOf(10)));
    assertFalse(prk.getPerkData().isValidPerkID(10));
    prk.getPerkData().getValidPerks().clear();
    assertEquals(3, prk.getPerkData().getValidPerks().size());
  }

  @Test
  void immutableRecord() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    PerkRecord rec = (PerkRecord) prk.getPerkData().getModelData("1");
    assertThrows(UnsupportedOperationException.class, () -> {
      rec.prereqPerks().clear();
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      rec.factorModifiers().clear();
    });
  }

  //valid setup
  @Test
  void num() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    assertEquals(expectedPerks.size(), prk.getPerkData().getRecordsList().size());
  }

  @Test
  void idsAndNames() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    for (PerkRecord pr : expectedPerks) {
      assertTrue(prk.getPerkData().isValidPerkID(pr.id()));
      assertTrue(prk.getPerkData().isValidField(pr.name()));
      assertEquals(pr.name(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).name());
    }
  }

  @Test
  void cost() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    for (PerkRecord pr : expectedPerks) {
      assertEquals(pr.cost(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).cost());
    }
  }

  @Test
  void description() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    for (PerkRecord pr : expectedPerks) {
      assertEquals(pr.description(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).description());
    }
  }

  @Test
  void prereqs() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    for (PerkRecord pr : expectedPerks) {
      assertEquals(pr.prereqPerks(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).prereqPerks());
    }
  }

  @Test
  void modifiers() {
    stringToJnodeAndSetup(ParserTestObjectUtils.perksSetup);
    for (PerkRecord pr : expectedPerks) {
      assertEquals(pr.factorModifiers().size(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).factorModifiers().size());
      for (ModifierRecord mr : pr.factorModifiers()) {
        assertEquals(mr.amount(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(pr.id()))).getModifierByName(mr.name()).amount());
      }
    }
  }

  //invalid tests
  @Test
  void badPerksNode() {
    for (String s : badPerksNode) {
      stringToJnode(s);
      prk.parseAllData();
      assertEquals(0, prk.getPerkData().getValidPerks().size());
    }
  }

  @Test
  void noValidPerks() {
    stringToJnodeAndSetup(noPerks);
    assertTrue(prk.getPerkData().getValidPerks().isEmpty());
  }

  @Test
  void oneBadID() {
    for (String s : oneInvalidBadID) {
      stringToJnodeAndSetup(s);
      assertEquals(1, prk.getPerkData().getValidPerks().size());
      assertTrue(prk.getPerkData().isValidField("Air 1"));
      assertFalse(prk.getPerkData().isValidField("Coughing"));
    }
  }

  @Test
  void missingName() {
    stringToJnodeAndSetup(missingName);
    assertEquals(1, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidField("Perk 1"));
    assertEquals("Perk 1", ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).name());
  }

  @Test
  void missingDescription() {
    stringToJnodeAndSetup(missingDescription);
    assertEquals(1, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidField("Air 1"));
    assertTrue(((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).description().isEmpty());
  }

  //((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1)))

  @Test
  void missingCost() {
    stringToJnodeAndSetup(missingCost);
    assertEquals(1, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidField("Air 1"));
    assertEquals(1, ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).cost());
  }

  @Test
  void missingPrereqs() {
    stringToJnodeAndSetup(missingPrereqs);
    assertEquals(1, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidField("Air 1"));
    assertTrue(((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).prereqPerks().isEmpty());
  }

  @Test
  void badFactors() {
    stringToJnodeAndSetup(badFactors);
    assertEquals(3, ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).factorModifiers().size());
    for (ModifierRecord mr : ParserTestObjectUtils.air1Perk.factorModifiers()) {
      assertTrue(((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).isValidModifier(mr.name()));
      assertEquals(mr.amount(), ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(1))).getModifierByName(mr.name()).amount());
    }
  }

  @Test
  void allBadFactors() {
    stringToJnodeAndSetup(allBadFactors);
    assertEquals(1, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidPerkID(1));
    assertFalse(prk.getPerkData().isValidPerkID(2));
  }

  @Test
  void badPrereqs() {
    stringToJnodeAndSetup(badPrereqs);
    assertEquals(2, prk.getPerkData().getValidPerks().size());
    assertTrue(((PerkRecord) prk.getPerkData().getModelData(String.valueOf(2))).prereqPerks().isEmpty());
  }

  @Test
  void noFactors() {
    stringToJnodeAndSetup(noFactors);
    assertTrue(prk.getPerkData().getValidPerks().isEmpty());
  }

  @Test
  void badPrereqDroppedPerk() {
    stringToJnodeAndSetup(badPrereqDroppedPerk);
    //2 gets dropped, 3's prereqs should be empty
    assertEquals(2, prk.getPerkData().getValidPerks().size());
    assertTrue(prk.getPerkData().isValidPerkID(3));
    assertFalse(prk.getPerkData().isValidPerkID(2));
    assertEquals(1, ((PerkRecord) prk.getPerkData().getModelData(String.valueOf(3))).prereqPerks().size());
    assertTrue(((PerkRecord) prk.getPerkData().getModelData(String.valueOf(3))).prereqPerks().contains(1));
  }

  //Strings
  String[] badPerksNode = {
      """
      {
         "perk": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""",
      """
      {
         "perks":
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }

       }"""
  };

  String noPerks = """
      {
         "perks": [
           {
             "id" : -100,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String[] oneInvalidBadID = {
      """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urban",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""",
      """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : "hi,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urban",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""",
      """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : -1,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urban",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""",
      """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : 1,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urban",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }"""
  };

  String missingName = """
      {
         "perks": [
           {
             "id" : 1,
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String missingDescription = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String missingCost = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String missingPrereqs = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String badFactors = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               },
               {
                "factor" : "what",
                "name" : "idk",
                "effect" : 0.2
                },
                {
                "name" : "Sea",
                "effect" : 0.2
                },
                {
                "factor" : "Climate",
                "name" : "hot",
                "effect" : 0.2
                },
                {
                "factor" : "Core",
                "name" : "infectinoness",
                "effect" : 0.1
                },
                {
                "factor" : "Core",
                "name" : "lethality",
                "effect" : "hi"
                },
                            {
                "factor" : "Core",
                "nme" : "lethality",
                "effect" : 0.1
                }

             ]
           }
         ]
       }""";

  String allBadFactors = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : 2,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "nope",
                 "effect" : 0.03
               },
               {
                 "factr" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "rich",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String badPrereqs = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : 2,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [2,-1,100],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "Severity",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urban",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

  String noFactors = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
             ]
           }
         ]
       }""";

  String badPrereqDroppedPerk = """
      {
         "perks": [
           {
             "id" : 1,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name": "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Air",
                 "effect" : 0.09
               },
               {
                 "factor" : "Climate",
                 "name" : "Arid",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : 2,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "prereqID" : [],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "infection",
                 "effect" : 0.03
               },
               {
                 "factor" : "Core",
                 "name" : "severeness",
                 "effect" : 0.01
               },
               {
                 "factor" : "Density",
                 "name" : "Urbanstuffidk",
                 "effect" : 0.08
               }
             ]
           },
           {
             "id" : 3,
             "name" : "Bird 1",
             "description" : "Birds become susceptible to infection. Avian carriers increase Infectivity, land transmission and mutation",
             "cost" : 4,
             "prereqID" : [1,2],
             "factorsModified" : [
               {
                 "factor" : "Core",
                 "name" : "Infectivity",
                 "effect" : 0.03
               },
               {
                 "factor": "Path",
                 "name" : "Land",
                 "effect" : 0.08
               }
             ]
           }
         ]
       }""";

}
package ooga.controller.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ooga.controller.parsers.paths.PathRecord;
import ooga.controller.parsers.paths.SubPopSpreadRecord;
import ooga.controller.parsers.perks.PerkRecord;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesData;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesRecord;
import ooga.controller.parsers.regions.RegionPathRecord;
import ooga.controller.parsers.regions.RegionRecord;

public class ParserTestObjectUtils {

  //Valid JSON strings
  public static final String regionFactorSetup = """ 
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
            "name": "Wealth",
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
      }
      """;

  public static final String mathSetup = """
        {
          "math": {
                  "defaultPopulation" : "Healthy",
                  "populations" : [
                    {
                      "name" : "Infected",
                      "sourcePop" : "Healthy",
                      "protagonistFactors" : ["Infectivity"],
                      "regionFactors" : ["Climate", "Temperature", "Wealth", "Density"]
                    },
                    {
                      "name" : "Dead",
                      "sourcePop" : "Infected",
                      "protagonistFactors" : ["Severity", "Lethality"],
                      "regionFactors" : []
                    }
                  ],
                  "userPoints" : {
                    "name" : "DNA Points",
                    "populationParameters" : ["Infected", "Dead"],
                    "protagonistParameters" : ["Severity"]
                  }
                }
              }
      """;

  public static final String pathsSetup = """
      {
        "paths": [
          {
            "id" : 0,
            "name" : "Land",
            "numSpread" : 10,
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
            "animationType" : "Line",
            "symbol" : ".",
            "numSpread" : 12,
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
            "animationType" : "Fade",
            "symbol" : "*",
            "numSpread" : 5,
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

  public static final String protagonistSetup = """
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
              "name": "Virus",
              "description": "Deadly but small!",
              "image": "/protagonist_icons/virus.png",
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

  public static final String perksSetup = """
      {
         "perks": [
           {
             "id" : 1,
             "group" : 0,
             "name" : "Air 1",
             "description" : "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
             "cost" : 9,
             "imagePath": "perks_icons/mosquito.png",
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
             "group" : 1,
             "name" : "Coughing",
             "description" : "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
             "cost" : 4,
             "imagePath": "perks_icons/mosquito.png",
             "prereqID" : [1],
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
           },
           {
             "id" : 3,
             "group": 0,
             "name" : "Bird 1",
             "description" : "Birds become susceptible to infection. Avian carriers increase Infectivity, land transmission and mutation",
             "imagePath": "perks_icons/mosquito.png",
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
  public static final String regionsSetup = """
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

  //Dummy protagonists
  public static final ProtagonistTypesRecord bacteriaProtag = makeBacteria();

  private static ProtagonistTypesRecord makeBacteria() {
    //setup core factors
    List<ModifierRecord> factors = new ArrayList<>();
    factors.add(new ModifierRecord("Core", "Infectivity", 0.2));
    factors.add(new ModifierRecord("Core", "Lethality", 0.1));
    factors.add(new ModifierRecord("Core", "Severity", 0.15));

    //setup path modifiers
    factors.add(new ModifierRecord("Path", "Land", 0.54));
    factors.add(new ModifierRecord("Path", "Air", 0.45));
    factors.add(new ModifierRecord("Path", "Sea", 0.32));

    //setup regionfactor modifiers
    //Temperature
    factors.add(new ModifierRecord("Temperature", "Cold", 0.12));
    factors.add(new ModifierRecord("Temperature", "Hot", 0.13));
    factors.add(new ModifierRecord("Temperature", "Normal", 1.0));
    //Climate
    factors.add(new ModifierRecord("Climate", "Arid", 0.32));
    factors.add(new ModifierRecord("Climate", "Humid", 0.22));
    factors.add(new ModifierRecord("Climate", "Normal", 1.0));
    //pop density
    factors.add(new ModifierRecord("Density", "Rural", 0.18));
    factors.add(new ModifierRecord("Density", "Urban", 1.1));
    factors.add(new ModifierRecord("Density", "Normal", 1.0));
    //Wealth
    factors.add(new ModifierRecord("Wealth", "Rich", 0.14));
    factors.add(new ModifierRecord("Wealth", "Poor", 1.15));
    factors.add(new ModifierRecord("Wealth", "Normal", 1.0));

    Map<String, List<ModifierRecord>> m = new HashMap<>();
    for (ModifierRecord mr : factors) {
      if (!m.containsKey(mr.factor())) {
        m.put(mr.factor(), new ArrayList<>());
      }
      m.get(mr.factor()).add(mr);
    }

    return new ProtagonistTypesRecord(0, "Bacteria", "Small but deadly!",
        "/protagonist_icons/bacteria.png", m);
  }

  public static final ProtagonistTypesRecord virusProtag = makeVirus();

  private static ProtagonistTypesRecord makeVirus() {
    //setup core factors
    List<ModifierRecord> factors = new ArrayList<>();
    factors.add(new ModifierRecord("Core", "Infectivity", 0.45));
    factors.add(new ModifierRecord("Core", "Lethality", 0.23));
    factors.add(new ModifierRecord("Core", "Severity", 0.64));

    //setup path modifiers
    factors.add(new ModifierRecord("Path", "Land", 0.57));
    factors.add(new ModifierRecord("Path", "Air", 0.34));
    factors.add(new ModifierRecord("Path", "Sea", 0.41));

    //setup regionfactor modifiers
    //Temperature
    factors.add(new ModifierRecord("Temperature", "Cold", 0.32));
    factors.add(new ModifierRecord("Temperature", "Hot", 0.24));
    factors.add(new ModifierRecord("Temperature", "Normal", 1.0));
    //Climate
    factors.add(new ModifierRecord("Climate", "Arid", 0.65));
    factors.add(new ModifierRecord("Climate", "Humid", 0.53));
    factors.add(new ModifierRecord("Climate", "Normal", 1.0));
    //pop density
    factors.add(new ModifierRecord("Density", "Rural", 0.42));
    factors.add(new ModifierRecord("Density", "Urban", 1.43));
    factors.add(new ModifierRecord("Density", "Normal", 1.0));
    //Wealth
    factors.add(new ModifierRecord("Wealth", "Rich", 0.55));
    factors.add(new ModifierRecord("Wealth", "Poor", 1.76));
    factors.add(new ModifierRecord("Wealth", "Normal", 1.0));

    Map<String, List<ModifierRecord>> m = new HashMap<>();
    for (ModifierRecord mr : factors) {
      if (!m.containsKey(mr.factor())) {
        m.put(mr.factor(), new ArrayList<>());
      }
      m.get(mr.factor()).add(mr);
    }

    return new ProtagonistTypesRecord(1, "Virus", "Deadly but small!",
        "/protagonist_icons/virus.png", m);
  }

  public static final ProtagonistTypesData protagData = new ProtagonistTypesData(
      Arrays.asList(bacteriaProtag, virusProtag),
      Arrays.asList("Infectivity", "Lethality", "Severity"));

  //Dummy perks
  public static final PerkRecord air1Perk = makeAir1();

  private static PerkRecord makeAir1() {
    List<ModifierRecord> lst = new ArrayList<>();
    lst.add(new ModifierRecord("Core", "Infectivity", 0.03));
    lst.add(new ModifierRecord("Path", "Air", 0.09));
    lst.add(new ModifierRecord("Climate", "Arid", 0.08));
    return new PerkRecord(1, 0, "Air 1",
        "Gives pathogen ability to travel on dust particles. Increase Infectivity, especially in Arid environments and plane transmission. (taken from real Plague Inc)",
        9, "Protagonist", new HashSet<>(), lst, "perks_icons/mosquito.png");
  }

  public static final PerkRecord coughingPerk = makeCoughing();

  private static PerkRecord makeCoughing() {
    List<ModifierRecord> lst = new ArrayList<>();
    lst.add(new ModifierRecord("Core", "Infectivity", 0.03));
    lst.add(new ModifierRecord("Core", "Severity", 0.01));
    lst.add(new ModifierRecord("Density", "Urban", 0.08));
    return new PerkRecord(2, 1, "Coughing",
        "Chance of infection by spreading pathogen into surroundings, especially in high density, Urban areas. (taken from real Plague Inc)",
        4, "Protagonist", Set.of(
        1), lst, "perks_icons/mosquito.png");
  }

  public static final PerkRecord bird1Perk = makeBird1();

  private static PerkRecord makeBird1() {
    List<ModifierRecord> lst = new ArrayList<>();
    lst.add(new ModifierRecord("Core", "Infectivity", 0.03));
    lst.add(new ModifierRecord("Path", "Land", 0.08));
    return new PerkRecord(3, 0, "Bird 1",
        "Birds become susceptible to infection. Avian carriers increase Infectivity, land transmission and mutation",
        4, "Protagonist", Set.of(1, 2), lst, "perks_icons/mosquito.png");
  }

  //Dummy pathRecords
  public static final PathRecord landPath = new PathRecord(0, "Land", "", "", "Fade", "", 10,
      Arrays.asList(new SubPopSpreadRecord("Infected", 1)));

  public static final PathRecord airPath = new PathRecord(1, "Air", "\\somedir\\air.png",
      "\\somedir\\airport.png", "Line", ".", 12,
      Arrays.asList(new SubPopSpreadRecord("Infected", 2), new SubPopSpreadRecord("Dead", 1)));

  public static final PathRecord seaPath = new PathRecord(2, "Sea", "\\somedir\\boat.png",
      "\\somedir\\seaport.png", "Fade", "*", 5, Arrays.asList(new SubPopSpreadRecord("Dead", 3)));


  //Dummy regionRecords and regionpathRecords
  public static final RegionPathRecord region1air = new RegionPathRecord(1, Set.of(2, 3), 0.05,
      airPath);
  public static final RegionPathRecord region1sea = new RegionPathRecord(2, Set.of(2), 0.12,
      seaPath);
  public static final RegionRecord region1 = makeRegion1();

  private static RegionRecord makeRegion1() {
    int id = 1;
    String name = "country 1";
    String description = "this is country 1";
    List<RegionPathRecord> paths = Arrays.asList(region1air, region1sea);
    Map<String, String> factors = region1Factors();
    int popNum = 1000000;
    double detThresh = 0.68;
    double closeThresh = 0.24;
    String color = "#34d5eb";
    double antag = 1250; //from avg of regionfactors defined above: (1+2+1+1)/4  * 1000 (from region json)
    return new RegionRecord(id, name, description, paths, factors, popNum, detThresh, closeThresh,
        color, antag);
  }

  private static Map<String, String> region1Factors() {
    Map<String, String> m = new HashMap<>();
    m.put("Climate", "Arid");
    m.put("Temperature", "Hot");
    m.put("Wealth", "Normal");
    m.put("Density", "Rural");
    return m;
  }


  public static final RegionPathRecord region2air = new RegionPathRecord(1, Set.of(3), 0.09,
      airPath);
  public static final RegionPathRecord region2sea = new RegionPathRecord(2, Set.of(1, 3), 0.14,
      seaPath);
  public static final RegionRecord region2 = makeRegion2();

  private static RegionRecord makeRegion2() {
    int id = 2;
    String name = "country 2";
    String description = "this is country 2";
    List<RegionPathRecord> paths = Arrays.asList(region2air, region2sea);
    Map<String, String> factors = region2Factors();
    int popNum = 2000000;
    double detThresh = 0.79;
    double closeThresh = 0.19;
    String color = "#6b34eb";
    double antag = 6250; //from avg of regionfactors defined above: (1+1+0.5+10)/4  * 2000 (from json)
    return new RegionRecord(id, name, description, paths, factors, popNum, detThresh, closeThresh,
        color, antag);
  }

  private static Map<String, String> region2Factors() {
    Map<String, String> m = new HashMap<>();
    m.put("Climate", "Humid");
    m.put("Temperature", "Normal");
    m.put("Wealth", "Rich");
    m.put("Density", "Urban");
    return m;
  }


  public static final RegionPathRecord region3air = new RegionPathRecord(1, Set.of(1, 4), 0.03,
      airPath);
  public static final RegionPathRecord region3sea = new RegionPathRecord(2, Set.of(1, 2), 0.32,
      seaPath);
  public static final RegionRecord region3 = makeregion3();

  private static RegionRecord makeregion3() {
    int id = 3;
    String name = "country 3";
    String description = "this is country 3";
    List<RegionPathRecord> paths = Arrays.asList(region3air, region3sea);
    Map<String, String> factors = region3Factors();
    int popNum = 3000000;
    double detThresh = 0.68;
    double closeThresh = 0.22;
    String color = "#c44da0";
    double antag = 3900; //from avg of regionfactors defined above: (0.2+1+1+3)/4 * 3000
    return new RegionRecord(id, name, description, paths, factors, popNum, detThresh, closeThresh,
        color, antag);
  }

  private static Map<String, String> region3Factors() {
    Map<String, String> m = new HashMap<>();
    m.put("Climate", "Normal");
    m.put("Temperature", "Cold");
    m.put("Wealth", "Poor");
    m.put("Density", "Normal");
    return m;
  }


  public static final RegionPathRecord region4air = new RegionPathRecord(1, Set.of(1, 2), 0.12,
      airPath);
  public static final RegionRecord region4 = makeregion4();

  private static RegionRecord makeregion4() {
    int id = 4;
    String name = "country 4";
    String description = "this is country 4";
    List<RegionPathRecord> paths = Arrays.asList(region4air);
    Map<String, String> factors = region4Factors();
    int popNum = 4000000;
    double detThresh = 0.74;
    double closeThresh = 0.06;
    String color = "#faf36e";
    double antag = 4700; //from avg of regionfactors defined above: (0.2+1+0.5+3)/4 * 4000 (from json)
    return new RegionRecord(id, name, description, paths, factors, popNum, detThresh, closeThresh,
        color, antag);
  }

  private static Map<String, String> region4Factors() {
    Map<String, String> m = new HashMap<>();
    m.put("Climate", "Normal");
    m.put("Temperature", "Normal");
    m.put("Wealth", "Rich");
    m.put("Density", "Normal");
    return m;
  }

}

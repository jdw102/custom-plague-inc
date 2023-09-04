package ooga.controller.factories;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import ooga.controller.parsers.antagonist.AntagonistJSON;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.controller.parsers.region_factors.RegionFactorData;
import ooga.controller.parsers.region_factors.RegionFactorRecord;
import ooga.model.actor.Antagonist;
import ooga.model.actor.Modifiers;

public class AntagonistFactory {

  private final AntagonistJSON antagonistJSON;

  public AntagonistFactory(JsonNode antagonistNode) throws InvalidOperationException {
    antagonistJSON = new AntagonistJSON(antagonistNode);
    antagonistJSON.parseAllData();
  }

  public Antagonist createAntagonist(RegionFactorData regionFactorData) {
    Antagonist antagonist = antagonistJSON.getAntagonist();
    for (RegionFactorRecord record : regionFactorData) {
      Map<String, Double> modifiersMap = record.values();
      Modifiers modifiers = new Modifiers(record.name());
      for (String s : modifiersMap.keySet()) {
        modifiers.addModifier(s, modifiersMap.get(s));
      }
      antagonist.addModifiers(modifiers);
    }
    return antagonist;
  }
}

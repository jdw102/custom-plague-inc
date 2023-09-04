package ooga.controller.factories;

import java.util.List;
import ooga.controller.ProtagonistMap;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesData;
import ooga.controller.parsers.protagonist_type.ProtagonistTypesRecord;
import ooga.model.actor.Modifiers;
import ooga.model.actor.Protagonist;
import ooga.view.ProtagonistSelector;
import ooga.view.ProtagonistView;

public class ProtagonistFactory {

  private final ProtagonistTypesData protagonistTypesData;
  private final String gameType;

  public ProtagonistFactory(ProtagonistTypesData protagonistTypesData, String gameType) {
    this.protagonistTypesData = protagonistTypesData;
    this.gameType = gameType;
  }

  public void make(ProtagonistSelector protagonistSelector, ProtagonistMap protagonistMap) {
    for (ProtagonistTypesRecord protagonistTypesRecord : protagonistTypesData.getValidProtagonists()) {
      String imagePath = String.format("%s/%s%s", "/games", gameType,
          protagonistTypesRecord.imagePath());
      Protagonist protagonist = new Protagonist(protagonistTypesRecord.id());
      configureSelectedProtagonist(protagonist, protagonistTypesRecord);
      ProtagonistView protagonistView = new ProtagonistView(protagonist,
          protagonistTypesRecord.name(),
          protagonistTypesRecord.description(), imagePath, gameType);
      protagonist.addObserver(protagonistView);
      protagonistSelector.addProtagonist(protagonistView);
      protagonistMap.addProtagonist(protagonist);
    }
  }

  private void configureSelectedProtagonist(Protagonist protagonist,
      ProtagonistTypesRecord protagonistTypesRecord) {
    for (String factorType : protagonistTypesRecord.allFactors().keySet()) {
      Modifiers m = new Modifiers(factorType);
      for (ModifierRecord mr : protagonistTypesRecord.get(factorType)) {
        m.addModifier(mr.name(), mr.amount());
      }
      protagonist.addModifiers(m);
    }
  }

  private Modifiers makeModifiers(List<ModifierRecord> modifierRecordList, String name) {
    Modifiers modifiers = new Modifiers(name);
    for (ModifierRecord modifierRecord : modifierRecordList) {
      modifiers.addModifier(modifierRecord.factor(), modifierRecord.amount());
    }
    return modifiers;
  }


}

package ooga.model;

import ooga.model.UserModel;
import ooga.model.actor.Protagonist;

public class ExampleRegionGrowthModel implements RegionGrowthModel {
  GrowthMathModel mathModel;
  RegionMap regionMap;

  public void handleGrowthState(Protagonist protagonist, UserModel userModel) {
    while (regionMap.hasNext()){
      Region region = regionMap.next();
      region.setNextState(mathModel.determineNextState(region, protagonist));
      region.setAntagonistRate(mathModel.caluclateAntagonistRate(region));
      userModel.adjustPoints(mathModel.calculatePoints(region, protagonist));
    }
  }

  public void updateStates() {
    while (regionMap.hasNext()){
      regionMap.next().update();
    }
  }
}

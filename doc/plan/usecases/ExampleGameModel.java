package ooga.model;

public class ExampleGameModel extends GameModel {
  RegionGrowthModel regionGrowthModel;
  RegionSpreadModel regionSpreadModel;
  Protagonist protagonist;
  UserModel userModel;
  Antagonist antagonist;

  public void update() {
    regionGrowthModel.handleGrowthStates(protagonist, userModel);
    regionGrowthModel.updateStates();
    regionSpreadModel.handleSpreadStates(protagonist, userModel);
    regionGrowthModel.updateStates();
    antagonist.updateRate(RegionGrowthModel.getRegionMap());
    if (antagonist.hasWon()){
      loseGame();
    }
  }
  private void loseGame(){

  }

}

package ooga.model;

public class ExampleGameModel extends GameModel {
  RegionGrowthModel regionGrowthModel;
  RegionSpreadModel regionSpreadModel;
  Protagonist protagonist;
  UserModel userModel;
  Antagonist antagonist;
  WorldDataModel worldDataModel;

  public void update() {
    worldDataModel.setPoints(userModel.getPoints());
    worldDataModel.updateSubPopulations(regionGrowthModel.getRegionModel());
    worldDataModel.setTotalInfectedRegions(regionGrowthModel.getTotalInfectedRegions());
    if (worldDataModel.checkWinCondition()){
      winGame();
    }
    if (worldDataModel.checkLoseCondition()){
      loseGame();
    }
  }
  private void winGame(){

  }
  private void loseGame(){

  }

}

public ExamplePathModel extends PathModel {
  boolean isInfected;
  RegionModel origin;
  RegionModel destination;
  Census additionalSubPop;

  @Override
  public void infectDestination(){
    isInfected = true;
    RegionState state = new RegionState(additionalSubPop, false, isInfected, false);
    destination.setNextState(state);
  }
}

public class ExampleRegionSpreadModel implements RegionSpreadModel {
  SpreadMathModel mathModel;
  RegionPaths regionPaths;

  public void handlePathStates(Protagonist protagonist, UserModel userModel){
    while (RegionPaths.hasNext()){
      PathModel path = RegionPaths.next();
      if (mathModel.pathTriggered(path)){
        if (mathModel.infectDestination(path)){
          path.infectDestination();
        }
        path.notifyObservers();
        userModel.adjustPoints(path.getPoints());
      }
    }
  }
}
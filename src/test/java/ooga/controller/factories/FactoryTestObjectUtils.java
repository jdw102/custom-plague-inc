package ooga.controller.factories;

import ooga.model.region.Census;
import ooga.model.region.PathActivityMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;

public class FactoryTestObjectUtils {

  //RegionModels
  //todo update these with updated antag calc logic
  public static final RegionModel region1model = new RegionModel(1, region1state(), region1PathActivity(), 0.24, 0.68, 1.25);
  public static final RegionModel region2model = new RegionModel(2, region2state(), region2PathActivity(), 0.19, 0.79, 0.875);
  public static final RegionModel region3model = new RegionModel(3, region3state(), region3PathActivity(), 0.22, 0.68, 1.3);
  public static final RegionModel region4model = new RegionModel(4, region4state(), region4PathActivity(), 0.06, 0.74, 1.175);

  //RegionStates
  public static RegionState region1state() {
    return new RegionState(region1Census(), true, false);
  }
  public static RegionState region2state() {
    return new RegionState(region2Census(), true, false);
  }
  public static RegionState region3state() {
    return new RegionState(region3Census(), true, false);
  }
  public static RegionState region4state() {
    return new RegionState(region4Census(), true, false);
  }

  //Censuses
  public static Census region1Census() {
    Census c = new Census();
    c.addPopulation("Healthy", 1000000);
    c.addPopulation("Infected", 0);
    c.addPopulation("Dead", 0);
    return c;
  }
  public static Census region2Census() {
    Census c = new Census();
    c.addPopulation("Healthy", 2000000);
    c.addPopulation("Infected", 0);
    c.addPopulation("Dead", 0);
    return c;
  }
  public static Census region3Census() {
    Census c = new Census();
    c.addPopulation("Healthy", 3000000);
    c.addPopulation("Infected", 0);
    c.addPopulation("Dead", 0);
    return c;
  }
  public static Census region4Census() {
    Census c = new Census();
    c.addPopulation("Healthy", 4000000);
    c.addPopulation("Infected", 0);
    c.addPopulation("Dead", 0);
    return c;
  }

  //PathActivities
  public static PathActivityMap region1PathActivity() {
    PathActivityMap p = new PathActivityMap();
    p.addPath("Air", 0.05);
    p.addPath("Sea", 0.12);
    return p;
  }
  public static PathActivityMap region2PathActivity() {
    PathActivityMap p = new PathActivityMap();
    p.addPath("Air", 0.09);
    p.addPath("Sea", 0.14);
    return p;
  }
  public static PathActivityMap region3PathActivity() {
    PathActivityMap p = new PathActivityMap();
    p.addPath("Air", 0.03);
    p.addPath("Sea", 0.32);
    return p;
  }
  public static PathActivityMap region4PathActivity() {
    PathActivityMap p = new PathActivityMap();
    p.addPath("Air", 0.12);
    return p;
  }

}

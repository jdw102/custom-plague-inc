package ooga.model.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PathModelTest {

  PathModel landPath;
  PathModel airPath;
  RegionPaths regionPaths;

  public PathModelTest() {
    Census originCensus = createTestCensus("Pop1", 2000, "Pop2", 1000);
    Census destinationCensus = createTestCensus("Pop1", 3000, "Pop2", 500);
    Census census = createTestCensus("Pop1", 100, "Pop2", 0);
    RegionState destinationNextState = new RegionState(census, true, false);
    Census census2 = createTestCensus("Pop1", 200, "Pop2", 200);
    RegionState originNextState = new RegionState(census2, true, false);
    RegionState originState = new RegionState(originCensus, true, false);
    RegionState destinationState = new RegionState(destinationCensus, true, false);
    PathActivityMap pathActivityMap = createPathActivity("Land", 1.0, "Air", 0.0);
    RegionModel origin = new RegionModel(0, originState, pathActivityMap, 1.0, 1.0, 0.0);
    RegionModel destination = new RegionModel(1, destinationState, pathActivityMap, 1.0, 1.0, 0.0);
    origin.setNextState(originNextState);
    destination.setNextState(destinationNextState);
    PathPointsMap landPoints = new PathPointsMap();
    landPoints.addPathPoints("Pop1", 1);
    landPoints.addPathPoints("Pop2", 2);
    PathPointsMap airPoints = new PathPointsMap();
    airPoints.addPathPoints("Pop1", 2);
    airPoints.addPathPoints("Pop2", 1);
    landPath = new PathModel(origin, destination, "Land", landPoints, 10);
    airPath = new PathModel(origin, destination, "Air", airPoints, 10);
    regionPaths = new RegionPaths();
    regionPaths.addPath(landPath);
    regionPaths.addPath(airPath);
  }

  private static Stream<Arguments> successfulNewTransfers() {
    return Stream.of(
        arguments(3, 2, 7, 8),
        arguments(4, 1, 6, 9),
        arguments(5, 6, 17, 16)
    );
  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
      int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  private PathActivityMap createPathActivity(String name1, double act1, String name2, double act2) {
    PathActivityMap pathActivityMap = new PathActivityMap();
    pathActivityMap.addPath(name1, act1);
    pathActivityMap.addPath(name2, act2);
    return pathActivityMap;
  }

  @Test
  public void testSuccessfulCensusTransfer()
      throws PopulationNotFoundException, PathNotFoundException {
    Census transferCensus = new Census();
    int originPopExpected = landPath.getOrigin().getNextState().census().getTotal() - 100;
    int pop1Expected =
        landPath.getDestination().getNextState().census().getPopulation("Pop1") + 95;
    int pop2Expected =
        landPath.getDestination().getNextState().census().getPopulation("Pop2") + 5;
    transferCensus.addPopulation("Pop1", 95);
    transferCensus.addPopulation("Pop2", 5);
    landPath.transferPeople(transferCensus);
    landPath.getDestination().updateState();
    landPath.getOrigin().updateState();
    assertEquals(originPopExpected, landPath.getOrigin().getNextState().census().getTotal());
    assertEquals(pop1Expected,
        landPath.getDestination().getNextState().census().getPopulation("Pop1"));
    assertEquals(pop2Expected,
        landPath.getDestination().getNextState().census().getPopulation("Pop2"));
    assertEquals(pop1Expected + pop2Expected,
        landPath.getDestination().getNextState().census().getTotal());
  }

  @Test
  public void testFailedCensusTransfer() throws PopulationNotFoundException, PathNotFoundException {
    int originPopExpected = landPath.getOrigin().getNextState().census().getTotal();
    Census transferCensus = new Census();
    int pop1Expected = landPath.getDestination().getNextState().census().getPopulation("Pop1");
    int pop2Expected = landPath.getDestination().getNextState().census().getPopulation("Pop2");
    transferCensus.addPopulation("Pop1", 95);
    transferCensus.addPopulation("Pop2", 5);
    airPath.transferPeople(transferCensus);
    airPath.getOrigin().updateState();
    airPath.getDestination().updateState();
    assertEquals(originPopExpected, landPath.getOrigin().getNextState().census().getTotal());
    assertEquals(pop1Expected,
        airPath.getDestination().getNextState().census().getPopulation("Pop1"));
    assertEquals(pop2Expected,
        airPath.getDestination().getNextState().census().getPopulation("Pop2"));
    assertEquals(pop1Expected + pop2Expected,
        airPath.getDestination().getNextState().census().getTotal());
  }

  @ParameterizedTest
  @MethodSource("successfulNewTransfers")
  public void testCorrectPoints(int pop1, int pop2, int landExpected, int airExpected) {
    double landActual = landPath.getPoints("Pop1") * pop1 + landPath.getPoints("Pop2") * pop2;
    double airActual = airPath.getPoints("Pop1") * pop1 + airPath.getPoints("Pop2") * pop2;
    assertEquals(landExpected, landActual);
    assertEquals(airActual, airExpected);
    assertEquals(0, landPath.getPoints("l"));
  }

  @Test
  void testObserversUpdate() throws PopulationNotFoundException, PathNotFoundException {
    TestObserver observer = new TestObserver(landPath);
    landPath.addObserver(observer);
    Census transferCensus = new Census();
    transferCensus.addPopulation("Pop1", 95);
    transferCensus.addPopulation("Pop2", 5);
    landPath.transferPeople(transferCensus);
    assertTrue(observer.getSentPop1());
  }

  @Test
  void testPathIteration() {
    int expected = 2;
    int count = 0;
    while (regionPaths.hasNext()) {
      regionPaths.next();
      count++;
    }
    assertEquals(expected, count);
  }

  @Test
  void testCheckIfNewlyTransfered() throws PopulationNotFoundException, PathNotFoundException {
    Census originCensus = new Census();
    originCensus.addPopulation("Pop1", 100);
    Census destinationCensus = new Census();
    destinationCensus.addPopulation("Pop1", 0);
    PathPointsMap pathPointsMap = new PathPointsMap();
    pathPointsMap.addPathPoints("Pop1", 0);
    PathActivityMap pathActivityMap = new PathActivityMap();
    pathActivityMap.addPath("Land", 1.0);
    RegionModel origin = new RegionModel(0, new RegionState(originCensus.getCopy(), true, false),
        pathActivityMap, 1.0, 1.0, 0);
    RegionModel destination = new RegionModel(0, new RegionState(destinationCensus.getCopy(), true, false),
        pathActivityMap, 1.0, 1.0, 0);
    origin.setNextState(new RegionState(originCensus, true, false));
    destination.setNextState(new RegionState(destinationCensus, true, false));
    PathModel pathModel = new PathModel(origin, destination, "Land", pathPointsMap, 10);
    Census transferCensus = new Census();
    transferCensus.addPopulation("Pop1", 2);
    pathModel.transferPeople(transferCensus);
    assertTrue(pathModel.checkNewlyTransferred("Pop1"));
  }

  private class TestObserver implements Observer {

    boolean sentPop1;
    PathModel path;

    public TestObserver(PathModel path) {
      this.path = path;
      try {
        sentPop1 = path.getDestination().getCurrentState().hasPopulation("Pop1");
      } catch (PopulationNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void update() {
      try {
        sentPop1 = path.getDestination().getCurrentState().hasPopulation("Pop1");
      } catch (PopulationNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    public boolean getSentPop1() {
      return sentPop1;
    }
  }

}

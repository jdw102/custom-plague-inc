package ooga.model.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RegionMapTest {

  private final RegionMap map;

  public RegionMapTest() {
    Census startCensus = new Census();
    startCensus.addPopulation("Pop1", 1);
    map = new RegionMap(startCensus);
    Census c1 = createTestCensus("Pop1", 1000, "Pop2", 2000);
    Census c2 = createTestCensus("Pop1", 500, "Pop2", 5000);
    Census c3 = createTestCensus("Pop1", 8000, "Pop2", 0);

    RegionState initState1 = new RegionState(c1, true, false);
    RegionState initState2 = new RegionState(c2, true, false);
    RegionState initState3 = new RegionState(c3, true, false);

    RegionModel r1 = new RegionModel(1, initState1, null, 0.5, 0.5, 0.0);
    RegionModel r2 = new RegionModel(2, initState2, null, 0.5, 0.5, 0.0);
    RegionModel r3 = new RegionModel(3, initState3, null, 0.5, 0.5, 0.0);

    map.addRegion(r1);
    map.addRegion(r2);
    map.addRegion(r3);
  }

  private Census createTestCensus(String popOneName, int popOneAmt, String popTwoName,
      int popTwoAmt) {
    Census census = new Census();
    census.addPopulation(popOneName, popOneAmt);
    census.addPopulation(popTwoName, popTwoAmt);
    return census;
  }

  @Test
  public void testTotalCensus() throws PopulationNotFoundException {
    int expected = 16500;
    Census totalCensus = map.getTotalCensus();
    assertEquals(expected, totalCensus.getTotal());
  }

  private static Stream<Arguments> populationAmounts() {
    return Stream.of(
        arguments("Pop1", 9500),
        arguments("Pop2", 7000)
    );
  }

  @ParameterizedTest
  @MethodSource("populationAmounts")
  public void testTotalSubPopulation(String name, int expectedAmt)
      throws PopulationNotFoundException {
    Census totalCensus = map.getTotalCensus();
    assertEquals(expectedAmt, totalCensus.getPopulation(name));
  }

  @Test
  void testGetRegionById() throws PopulationNotFoundException {
    assertEquals(1000, map.getRegionModelByID(1).getCurrentState().census().getPopulation("Pop1"));
  }

  @Test
  void testStart() throws PopulationNotFoundException {
    map.spreadStartingCensus(1);
    assertEquals(1001, map.getRegionModelByID(1).getCurrentState().census().getPopulation("Pop1"));
  }
}

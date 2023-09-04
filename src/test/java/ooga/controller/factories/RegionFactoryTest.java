package ooga.controller.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.ParserTestObjectUtils;
import ooga.controller.parsers.paths.PortSymbolMap;
import ooga.controller.parsers.regions.RegionPathRecord;
import ooga.controller.parsers.regions.RegionRecord;
import ooga.controller.parsers.regions.RegionsData;
import ooga.model.region.Census;
import ooga.model.region.PathNotFoundException;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.view.DisplayView;
import ooga.view.map.MapReader;
import ooga.view.map.MapView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegionFactoryTest {

  private RegionsData regionsData = new RegionsData(
      List.of(ParserTestObjectUtils.region1, ParserTestObjectUtils.region2,
          ParserTestObjectUtils.region3, ParserTestObjectUtils.region4), "Healthy",
      Set.of("Infected", "Dead"));
  List<RegionModel> rms = new ArrayList<>();
  private RegionFactory rf;

  @BeforeEach
  public void setup() {
    rf = new RegionFactory(regionsData, "dw");
    for (RegionRecord rr : regionsData) {
      rms.add(rf.makeRegionModel(rr));
    }
  }


  private RegionModel findRM(int id) {
    for (RegionModel r : rms) {
      if (r.getId() == id) {
        return r;
      }
    }
    return null;
  }

  @Test
  void overallNumCorrect() {
    assertEquals(4, rms.size());
  }

  @Test
  void testRegion1ModelCorrect() {
    RegionModel test = findRM(1);
    RegionRecord base = ParserTestObjectUtils.region1;
    assertNull(test.getNextState());
//    assertEquals(base.baseAntagonistRate(), test.getAntagonistContribution());
    assertTrue(test.isOpen());
    assertFalse(test.hasAntagonist());

  }

  @Test
  void testRegion1ModelPopulationsCorrect() throws PopulationNotFoundException {
    RegionModel test = findRM(1);
    RegionRecord base = ParserTestObjectUtils.region1;
    Census census = test.getCurrentState().census();
    assertEquals(1000000, census.getPopulation("Healthy"));
    assertTrue(test.getCurrentState().hasPopulation("Healthy"));
    assertEquals(0, census.getPopulation("Infected"));
    assertFalse(test.getCurrentState().hasPopulation("Infected"));
    assertEquals(0, census.getPopulation("Dead"));
    assertFalse(test.getCurrentState().hasPopulation("Dead"));
  }

  @Test
  void testRegion1ModelPathActivityCorrect() throws PathNotFoundException {
    RegionModel test = findRM(1);
    RegionPathRecord air = ParserTestObjectUtils.region1air;
    RegionPathRecord sea = ParserTestObjectUtils.region1sea;

    assertEquals(air.activity(), test.getPathActivity("Air"));
    assertEquals(sea.activity(), test.getPathActivity("Sea"));
  }

  @Test
  void testRegion2ModelCorrect() {
    RegionModel test = findRM(2);
    RegionRecord base = ParserTestObjectUtils.region2;
    assertNull(test.getNextState());
//    assertEquals(base.baseAntagonistRate(), test.getAntagonistContribution());
    assertTrue(test.isOpen());
    assertFalse(test.hasAntagonist());

  }

  @Test
  void testRegion2ModelPopulationsCorrect() throws PopulationNotFoundException {
    RegionModel test = findRM(2);
    RegionState state = test.getCurrentState();
    RegionRecord base = ParserTestObjectUtils.region2;
    assertEquals(2000000, state.census().getPopulation("Healthy"));
    assertTrue(state.hasPopulation("Healthy"));
    assertEquals(0, state.census().getPopulation("Infected"));
    assertFalse(state.hasPopulation("Infected"));
    assertEquals(0, state.census().getPopulation("Dead"));
    assertFalse(state.hasPopulation("Dead"));
  }

  @Test
  void testRegion2ModelPathActivityCorrect() throws PathNotFoundException {
    RegionModel test = findRM(2);
    RegionPathRecord air = ParserTestObjectUtils.region2air;
    RegionPathRecord sea = ParserTestObjectUtils.region2sea;

    assertEquals(air.activity(), test.getPathActivity("Air"));
    assertEquals(sea.activity(), test.getPathActivity("Sea"));
  }

  @Test
  void testRegion3ModelCorrect() {
    RegionModel test = findRM(3);
    RegionRecord base = ParserTestObjectUtils.region3;
    assertNull(test.getNextState());
//    assertEquals(base.baseAntagonistRate(), test.getAntagonistContribution());
    assertTrue(test.isOpen());
    assertFalse(test.hasAntagonist());

  }

  @Test
  void testRegion3ModelPopulationsCorrect() throws PopulationNotFoundException {
    RegionModel test = findRM(3);
    RegionState state = test.getCurrentState();
    RegionRecord base = ParserTestObjectUtils.region3;
    assertEquals(3000000, state.census().getPopulation("Healthy"));
    assertTrue(state.hasPopulation("Healthy"));
    assertEquals(0, state.census().getPopulation("Infected"));
    assertFalse(state.hasPopulation("Infected"));
    assertEquals(0, state.census().getPopulation("Dead"));
    assertFalse(state.hasPopulation("Dead"));
  }

  @Test
  void testRegion3ModelPathActivityCorrect() throws PathNotFoundException {
    RegionModel test = findRM(3);
    RegionPathRecord air = ParserTestObjectUtils.region3air;
    RegionPathRecord sea = ParserTestObjectUtils.region3sea;

    assertEquals(air.activity(), test.getPathActivity("Air"));
    assertEquals(sea.activity(), test.getPathActivity("Sea"));
  }

  @Test
  void testRegion4ModelCorrect() {
    RegionModel test = findRM(4);
    RegionRecord base = ParserTestObjectUtils.region4;
    assertNull(test.getNextState());
//    assertEquals(base.baseAntagonistRate(), test.getAntagonistContribution());
    assertTrue(test.isOpen());
    assertFalse(test.hasAntagonist());

  }

  @Test
  void testRegion4ModelPopulationsCorrect() throws PopulationNotFoundException {
    RegionModel test = findRM(4);
    RegionState state = test.getCurrentState();
    RegionRecord base = ParserTestObjectUtils.region4;
    assertEquals(4000000, state.census().getPopulation("Healthy"));
    assertTrue(state.hasPopulation("Healthy"));
    assertEquals(0, state.census().getPopulation("Infected"));
    assertFalse(state.hasPopulation("Infected"));
    assertEquals(0, state.census().getPopulation("Dead"));
    assertFalse(state.hasPopulation("Dead"));
  }

  @Test
  void testRegion4ModelPathActivityCorrect() throws PathNotFoundException {
    RegionModel test = findRM(4);
    RegionPathRecord air = ParserTestObjectUtils.region4air;

    assertEquals(air.activity(), test.getPathActivity("Air"));
    assertThrows(PathNotFoundException.class, () -> {
      test.getPathActivity("Sea");
    });
  }
}
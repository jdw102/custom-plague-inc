package ooga.model.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.Modifiers;
import ooga.util.GreaterThanOperation;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

public class RegionModelTest {

  RegionModel region;

  public RegionModelTest() {
    Census c1 = new Census();
    c1.addPopulation("Pop1", 5000);
    c1.addPopulation("Pop2", 8000);
    c1.addPopulation("Pop3", 2000);

    Census c2 = new Census();
    c2.addPopulation("Pop1", 7000);
    c2.addPopulation("Pop2", 8000);
    c2.addPopulation("Pop3", 0);

    RegionState initState = new RegionState(c1, true, false);
    RegionState nextState = new RegionState(c2, true, false);
    PathActivityMap pathActivityMap = new PathActivityMap();
    pathActivityMap.addPath("Land", 0.05);
    region = new RegionModel(1, initState, pathActivityMap, 0.5, 0.5, 200);
    region.setNextState(nextState);
  }

  private static Stream<Arguments> populationRatios() {
    return Stream.of(
        arguments("Pop1", (1.0 * 5000) / 15000),
        arguments("Pop2", (1.0 * 8000) / 15000),
        arguments("Pop3", (1.0 * 2000) / 15000)
    );
  }

  private static Stream<Arguments> populationHasAny() {
    return Stream.of(
        arguments("Pop1", true),
        arguments("Pop2", true),
        arguments("Pop3", true)
    );
  }

  private static Stream<Arguments> populationWillHaveAny() {
    return Stream.of(
        arguments("Pop1", true),
        arguments("Pop2", true),
        arguments("Pop3", false)
    );
  }

  @Test
  public void testCensusUpdates() throws PopulationNotFoundException {
    List<Integer> amts = new ArrayList<>();
    List<String> names = new ArrayList<>();
    while (region.getNextState().census().hasNext()) {
      String name = region.getNextState().census().next();
      amts.add(region.getNextState().census().getPopulation(name));
      names.add(name);
    }
    region.updateState();
    for (int i = 0; i < amts.size(); i++) {
      assertEquals(amts.get(i), region.getCurrentState().census().getPopulation(names.get(i)));
    }
  }

  @Test
  public void testStatusInfoUpdates() {
    region.updateState();
    boolean expected1 = !region.getCurrentState().isOpen();
    boolean expected2 = !region.getCurrentState().hasAntagonist();
    RegionState nextState = new RegionState(region.getCurrentState().census(), expected1,
        expected2);
    region.setNextState(nextState);
    region.updateState();
    assertEquals(expected1, region.getCurrentState().isOpen());
    assertEquals(expected2, region.getCurrentState().hasAntagonist());
  }

  @Test
  public void testObserversUpdate() {
    double expected = (1.0 * 7000) / 15000;
    TestObserver observer = new TestObserver();
    region.addObserver(observer);
    region.updateState();
    assertEquals(expected, observer.getRatio());
  }

  @Test
  public void testObserversFailToUpdate() {
    double expected = 0;
    TestObserver observer = new TestObserver();
    region.addObserver(observer);
    region.removeObserver(observer);
    region.updateState();
    assertEquals(expected, observer.getRatio());
  }

  @Test
  public void testThrowsFactorNotFound() {
    assertThrows(FactorNotFoundException.class, () -> region.getFactor("p"));
  }

  @Test
  void testThrowsPathNotFound() {
    assertThrows(PathNotFoundException.class, () -> region.getPathActivity("a"));
  }

  @Test
  void testAntagonistContribution() throws ModifierNotFoundException {
    Antagonist antagonist = new Antagonist(0.0, new GreaterThanOperation());
    Modifiers modifiers = new Modifiers("Climate");
    modifiers.addModifier("Hot", 0.5);
    antagonist.addModifiers(modifiers);
    region.addFactor("Climate", "Hot");
    region.setNextState(new RegionState(region.getCurrentState().census(), true, true));
    region.updateState();
    double contribution = region.getAntagonistContribution(antagonist);
    assertEquals(100, contribution);
  }

  @Test
  void testGetFactor() throws FactorNotFoundException {
    region.addFactor("Climate", "Hot");
    assertEquals("Hot", region.getFactor("Climate"));
  }

  @Test
  void testCensusThrowsPopulationNotFound() throws PopulationNotFoundException {
    assertThrows(PopulationNotFoundException.class,
        () -> region.getCurrentState().census().getPopulation("1"));
  }

  private class TestObserver implements Observer {

    double ratio;

    @Override
    public void update() {
      RegionState state = region.getCurrentState();
      try {
        ratio = (1.0 * state.census().getPopulation("Pop1")) / state.census().getTotal();
      } catch (PopulationNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    public double getRatio() {
      return ratio;
    }
  }
}

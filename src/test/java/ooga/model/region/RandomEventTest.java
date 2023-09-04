package ooga.model.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ooga.model.actor.Protagonist;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;

public class RandomEventTest {

  RandomEvent popEvent;
  RandomEvent factorEvent;
  RandomEvent failedEvent;
  RegionModel regionModel;

  public RandomEventTest() {
    Census census = new Census();
    census.addPopulation("Pop1", 100);
    census.addPopulation("Pop2", 200);
    regionModel = new RegionModel(0, new RegionState(census, true, false), null, 1.0,
        1.0, 0);
    regionModel.addFactor("Climate", "Hot");
    Runnable popRunnable = makeEventRunnable("Pop2", "Pop1", 50, "", "", regionModel);
    Runnable factorRunnable = makeEventRunnable("Pop2", "Pop1", 0, "Climate", "Cold", regionModel);
    Runnable failedRunnable = makeEventRunnable("as", "a", 2, "", "", regionModel);
    popEvent = new RandomEvent(1.0, popRunnable, "PopTest");
    factorEvent = new RandomEvent(1.0, factorRunnable, "FactorTest");
    failedEvent = new RandomEvent(1.0, failedRunnable, "FailedTest");
  }

  private Runnable makeEventRunnable(String sourceName, String drainName, int amt, String factor,
      String level, RegionModel region) {
    Runnable runnable = () -> {
      RegionState state = region.getCurrentState();
      int actAmt = 0;
      try {
        actAmt = Math.min(state.census().getPopulation(sourceName), amt);
        region.getCurrentState().census().adjustPopulation(sourceName, -1 * actAmt);
        region.getCurrentState().census().adjustPopulation(drainName, actAmt);
      } catch (PopulationNotFoundException e) {
        throw new RuntimeException(e);
      }
      if (factor != null & level != null) {
        region.addFactor(factor, level);
      }
    };
    return runnable;
  }

  @Test
  void testPopulationEvent() throws InvalidEventException, PopulationNotFoundException {
    popEvent.activate();
    assertEquals(150, regionModel.getCurrentState().census().getPopulation("Pop1"));
    assertEquals(150, regionModel.getCurrentState().census().getPopulation("Pop2"));
    assertTrue(popEvent.isActivated());
    assertFalse(popEvent.isAvailable());
  }

  @Test
  void testFactorEvent() throws FactorNotFoundException, InvalidEventException {
    factorEvent.activate();
    assertEquals("Cold", regionModel.getFactor("Climate"));
    assertTrue(factorEvent.isActivated());
    assertFalse(factorEvent.isAvailable());
  }

  @Test
  void testThrowsInvalidEvent(){
    assertThrows(InvalidEventException.class, () -> failedEvent.activate());
  }

  @Test
  void testObserverUpdates() throws InvalidEventException {
    TestObserver testObserver = new TestObserver(popEvent);
    popEvent.addObserver(testObserver);
    popEvent.activate();
    assertEquals(popEvent.getMessage(), testObserver.getMessage());
  }

  private class TestObserver implements Observer {

    String message;
    RandomEvent randomEvent;

    public TestObserver(RandomEvent randomEvent) {
      this.randomEvent = randomEvent;
    }

    @Override
    public void update() {
      message = randomEvent.getMessage();
    }

    public String getMessage() {
      return message;
    }
  }
}

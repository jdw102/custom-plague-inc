package ooga.model.actor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ooga.model.region.Census;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.GreaterThanOperation;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;

public class AntagonistTest {

  Antagonist antagonist;

  public AntagonistTest() {
    antagonist = new Antagonist(0, new GreaterThanOperation());
  }

  private class TestObserver implements Observer {

    double amt;
    Antagonist antagonist;

    public TestObserver(Antagonist antagonist) {
      this.antagonist = antagonist;
    }

    @Override
    public void update() {
      amt = antagonist.getAmount();
    }

    public double getAmt() {
      return amt;
    }
  }

  @Test
  void testAntagonistUpdates() {
    antagonist.updateAmount(0.5);
    assertEquals(0.5, antagonist.getProgress());
  }

  @Test
  void testThresholdPasses() throws PopulationNotFoundException {
    Census census = new Census();
    double threshold = 0.5;
    antagonist.addTrackedPopulation("Pop1");
    census.addPopulation("Pop1",  200);
    census.addPopulation("Pop2", 100);
    assertEquals(true, antagonist.checkThreshold(census, threshold));
  }

  @Test
  void testAntagonistThrowsModifierNotFoundException() {
    assertThrows(ModifierNotFoundException.class, () -> antagonist.getModifier("a", "b"));
    assertThrows(ModifierNotFoundException.class, () -> antagonist.adjustModifier("a", "b", 0.3));
  }

  @Test
  void testAntagonistNotifiesObserver() {
    TestObserver testObserver = new TestObserver(antagonist);
    antagonist.addObserver(testObserver);
    antagonist.updateAmount(2);
    assertEquals(2, testObserver.getAmt());
  }


}

package ooga.model.actor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ooga.util.Observer;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.jupiter.api.Test;

public class ProtagonistTest {
  Protagonist protagonist;

  public ProtagonistTest(){
    protagonist = new Protagonist(0);
    Modifiers modifiers = new Modifiers("Path");
    modifiers.addModifier("Land", 0.5);
    protagonist.addModifiers(modifiers);
  }

  private class TestObserver implements Observer {

    int id;
    Protagonist protagonist;

    public TestObserver(Protagonist protagonist) {
      this.protagonist = protagonist;
    }

    @Override
    public void update() {
      id = protagonist.getId();
    }

    public double getId() {
      return id;
    }
  }

  @Test
  void testObserverUpdates() throws ModifierNotFoundException {
    TestObserver testObserver = new TestObserver(protagonist);
    protagonist.addObserver(testObserver);
    protagonist.adjustModifier("Path", "Land", 0.2);
    assertEquals(protagonist.getId(), testObserver.getId());
  }

  @Test
  void testThrowsException() {
    assertThrows(ModifierNotFoundException.class, () -> protagonist.getModifier("la", "a"));
    assertThrows(ModifierNotFoundException.class, () -> protagonist.adjustModifier("s","s", 0.3));
  }

  @Test
  void testPathModRetrieval(){
    Modifiers mods = protagonist.getTransmissionModifiers();
    assertEquals(mods.getModifier("Land"), 0,5);
  }

  @Test
  void testCoreModRetrieval(){
    Modifiers mods = new Modifiers("Core");
    mods.addModifier("Infectivity", 0.1);
    protagonist.addModifiers(mods);
    assertEquals(0.1, protagonist.getCoreModifiers().getModifier("Infectivity"));
  }
}

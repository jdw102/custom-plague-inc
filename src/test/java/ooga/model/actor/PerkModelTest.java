package ooga.model.actor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import ooga.controller.factories.InvalidPerkException;
import ooga.controller.parsers.perks.PerkRecord;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.model.gamestate.DefaultUserPoints;
import ooga.model.gamestate.UserPoints;
import ooga.util.GreaterThanOperation;
import ooga.util.Observer;
import org.junit.jupiter.api.Test;

public class PerkModelTest {

  PerkModel protagonistPerk1;
  PerkModel protagonistPerk2;
  PerkModel antagonistPerk;
  Protagonist protagonist;
  Antagonist antagonist;
  PerkTreeModel perkTree;
  UserPoints userPoints;

  public PerkModelTest() {
    perkTree = new PerkTreeModel();
    protagonist = new Protagonist(0);
    userPoints = new DefaultUserPoints(0.0, 100);
    Modifiers protagonistModifiers = new Modifiers("Climate");
    protagonistModifiers.addModifier("Hot", 0.3);
    protagonistModifiers.addModifier("Cold", 0.06);
    Modifiers antagonistModifiers = new Modifiers("Climate");
    antagonistModifiers.addModifier("Hot", 0.3);
    antagonistModifiers.addModifier("Cold", 0.06);
    protagonist.addModifiers(protagonistModifiers);
    antagonist = new Antagonist(0.0, new GreaterThanOperation());
    antagonist.addTrackedPopulation("Pop1");
    antagonist.addModifiers(antagonistModifiers);

    PerkRecord perkRecord1 = new PerkRecord(0, 0, "", "", 5,
        "Protagonist",
        Set.of(),
        List.of(new ModifierRecord("Climate", "Hot", 0.1)), "");
    protagonistPerk1 = makePerkModel(protagonist, perkRecord1, userPoints);
    PerkRecord perkRecord2 = new PerkRecord(1, 0, "", "", 3,
        "Antagonist",
        Set.of(),
        List.of(new ModifierRecord("Climate", "Cold", 0.05)), "");
    antagonistPerk = makePerkModel(antagonist, perkRecord2, userPoints);
    PerkRecord perkRecord3 = new PerkRecord(2, 0, "", "", 2,
        "Protagonist",
        Set.of(0),
        List.of(new ModifierRecord("Climate", "Hot", 0.1)), "");
    protagonistPerk2 = makePerkModel(protagonist, perkRecord3, userPoints);
    perkTree.addPerk(protagonistPerk1);
    perkTree.addPerk(protagonistPerk2);
    perkTree.addPerk(antagonistPerk);
  }

  private PerkModel makePerkModel(Actor actor, PerkRecord perkRecord, UserPoints userPoints) {
    BiPredicate<Double, Integer> activation = makeRunnable(perkRecord.factorModifiers(), actor,
        userPoints);
    PerkPrerequisites prerequisites = new PerkPrerequisites();
    for (Integer i : perkRecord.prereqPerks()) {
      prerequisites.addId(i);
    }
    PerkModel perk = new PerkModel(perkRecord.id(), activation, prerequisites,
        perkRecord.cost());
    return perk;
  }

  private BiPredicate<Double, Integer> makeRunnable(List<ModifierRecord> modifierRecords,
      Actor actor, UserPoints userpoints) {
    BiPredicate<Double, Integer> predicate = (p, i) -> {
      boolean worked = userpoints.adjustPoints(p);
      if (worked) {
        for (ModifierRecord record : modifierRecords) {
          try {
            actor.adjustModifier(record.factor(), record.name(), i * record.amount());
          } catch (ModifierNotFoundException e) {
            throw new RuntimeException(e);
          }
        }
      }
      return worked;
    };
    return predicate;
  }

  @Test
  void testActivateProtagonistPerkModifiers()
      throws InvalidPerkException, ModifierNotFoundException {
    double expected = 0.4;
    userPoints.adjustPoints(10);
    protagonistPerk1.toggleActivate();
    assertEquals(expected, protagonist.getModifier("Climate", "Hot"));
    assertTrue(protagonistPerk1.isActive());
  }

  @Test
  void testActivateAntagonistPerkModifiers()
      throws InvalidPerkException, ModifierNotFoundException {
    double expected = 0.11;
    userPoints.adjustPoints(10);
    antagonistPerk.toggleActivate();
    assertEquals(expected, antagonist.getModifier("Climate", "Cold"));
    assertTrue(antagonistPerk.isActive());
  }

  @Test
  void testRefundPerkModifiers() throws ModifierNotFoundException, InvalidPerkException {
    double expected = 0.06;
    antagonistPerk.toggleActivate();
    antagonistPerk.toggleActivate();
    assertEquals(expected, antagonist.getModifier("Climate", "Cold"));
    assertFalse(antagonistPerk.isActive());
  }

  @Test
  void testPerkMissingPreReqs() throws InvalidPerkException, ModifierNotFoundException {
    double expected = 0.3;
    perkTree.togglePerkActivation(2);
    assertFalse(protagonistPerk2.isActive());
    assertEquals(expected, protagonist.getModifier("Climate", "Hot"));
  }

  @Test
  void testPerkHasPreReqs() throws InvalidPerkException, ModifierNotFoundException {
    double expected = 0.5;
    userPoints.adjustPoints(10);
    protagonistPerk1.toggleActivate();
    perkTree.togglePerkActivation(2);
    assertTrue(protagonistPerk2.isActive());
    assertEquals(expected, protagonist.getModifier("Climate", "Hot"));
  }

  @Test
  void testObserversNotified() throws InvalidPerkException {
    userPoints.adjustPoints(10);
    TestObserver observer = new TestObserver(protagonistPerk1);
    protagonistPerk1.addObserver(observer);
    protagonistPerk1.toggleActivate();
    assertTrue(observer.isPerkActivated());
  }

  @Test
  void testPerkPointsSpent() throws InvalidPerkException {
    userPoints.adjustPoints(10);
    double expected = 5;
    protagonistPerk1.toggleActivate();
    assertEquals(expected, userPoints.getPoints());
  }

  @Test
  void testPerkPointsRefunded() throws InvalidPerkException {
    userPoints.adjustPoints(10);
    double expected = 10;
    protagonistPerk1.toggleActivate();
    protagonistPerk1.toggleActivate();
    assertEquals(expected, userPoints.getPoints());
  }

  @Test
  void testPerkRefundedAfterPrerequisitesRefunded() throws InvalidPerkException {
    userPoints.adjustPoints(10);
    perkTree.togglePerkActivation(0);
    perkTree.togglePerkActivation(2);
    perkTree.togglePerkActivation(0);
    assertFalse(protagonistPerk2.isActive());
    assertEquals(10, userPoints.getPoints());
  }

  @Test
  void testThrowsInvalidPerkException() {
    userPoints.adjustPoints(3);
    BiPredicate<Double, Integer> predicate = makeRunnable(
        List.of(new ModifierRecord("a", "b", 0.2)), protagonist, userPoints);
    PerkModel perkModel = new PerkModel(20, predicate, new PerkPrerequisites(), 2);
    assertThrows(InvalidPerkException.class, () -> perkModel.toggleActivate());
  }

  @Test
  void testSetActivation() throws InvalidPerkException {
    protagonistPerk1.toggleActivate();
    assertFalse(protagonistPerk1.isActive());
    protagonistPerk1.setActiveState();
    assertTrue(protagonistPerk1.isActive());
  }

  @Test
  void testPerkAvailability() {
    assertTrue(protagonistPerk1.isAvailable());
    assertFalse(protagonistPerk2.isAvailable());
  }

  @Test
  void testSetRefundable() throws InvalidPerkException {
    userPoints.adjustPoints(6);
    protagonistPerk1.setRefundable(false);
    protagonistPerk1.toggleActivate();
    assertEquals(1, userPoints.getPoints());
    protagonistPerk1.toggleActivate();
    assertEquals(1, userPoints.getPoints());
  }

  private class TestObserver implements Observer {

    boolean perkActivated;
    PerkModel perk;

    public TestObserver(PerkModel perk) {
      this.perk = perk;
      perkActivated = perk.isActive();
    }

    @Override
    public void update() {
      perkActivated = perk.isActive();
    }

    public boolean isPerkActivated() {
      return perkActivated;
    }
  }
}


package ooga.controller.factories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import ooga.controller.parsers.GameConfigParser;
import ooga.controller.parsers.ParserTestObjectUtils;
import ooga.controller.parsers.events.EventRecordsCollection;
import ooga.controller.parsers.perks.GroupNameMap;
import ooga.controller.parsers.perks.PerkData;
import ooga.controller.parsers.perks.PerkRecord;
import ooga.model.actor.Antagonist;
import ooga.model.actor.PerkModel;
import ooga.model.actor.PerkTreeModel;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.DefaultUserPoints;
import ooga.model.gamestate.UserPoints;
import ooga.model.region.Census;
import ooga.model.region.RegionMap;
import ooga.util.GreaterThanOperation;
import ooga.view.DisplayView;
import ooga.view.PerkPurchaser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

class PerkFactoryTest extends DukeApplicationTest {

  private PerkData perkData = new PerkData(List.of(ParserTestObjectUtils.air1Perk, ParserTestObjectUtils.coughingPerk, ParserTestObjectUtils.bird1Perk));
  private Protagonist protag = new Protagonist(0);
  private Antagonist antag = new Antagonist(0.5, new GreaterThanOperation());
  private UserPoints userPoints = new DefaultUserPoints(0, 100);
  private PerkFactory pf;
  DisplayView displayView;
  List<PerkModel> pms = new ArrayList<>();
  private PerkTreeModel perkTreeModel;

  public PerkFactoryTest() throws Exception {

  }
  @Override
  public void start(Stage primaryStage) {
    GroupNameMap gnm = new GroupNameMap();
    displayView = new DisplayView("Plague", "English");
    pf = new PerkFactory(perkData, protag, antag, userPoints, gnm, "Plague", new RegionMap(new Census()));
//    for (PerkRecord pr : perkData.getValidPerks()) {
//      try {
//        pms.add(pf.makePerkModel(pr, displayView));
//      } catch (InvalidPerkException | InvalidActorException e) {
//      }
//    }
    try {
      pf.make(displayView, displayView.getSettingsBundle(),  new EventRecordsCollection());
    } catch (InvalidActorException e) {
      throw new RuntimeException(e);
    } catch (InvalidPerkException e) {
      throw new RuntimeException(e);
    }
    perkTreeModel = pf.getPerkTreeModel();
  }

  @Test
  void correctperks() {
    assertEquals(3, perkTreeModel.getNumPerks());
  }

  @Test
  void air1perk() {
    PerkModel test = perkTreeModel.getPerk(1);
    assertEquals(0, test.getPrerequisites().size());
    List<String> lst = Arrays.asList("Infectivity", "Air", "Arid");
//    List<Double> vals = List.of(0.03,0.09,0.08);
//    for (int i = 0; i < vals.size(); i++) {
//      assertEquals(vals.get(i), test.getEffects().get(lst.get(i)));
//    }
//    List<String> actual = new ArrayList<>(test.getEffects().keySet());
    Collections.sort(lst);
//    Collections.sort(actual);
//    assertEquals(lst, actual);
  }

  @Test
  void coughingperk() {
    PerkModel test = perkTreeModel.getPerk(2);
    assertEquals(1, test.getPrerequisites().size());
    assertTrue(test.getPrerequisites().contains(1));
//    List<String> lst = Arrays.asList("Infectivity", "Severity", "Urban");
//    List<Double> vals = List.of(0.03,0.01,0.08);
//    for (int i = 0; i < vals.size(); i++) {
//      assertEquals(vals.get(i), test.getEffects().get(lst.get(i)));
//    }
//    List<String> actual = new ArrayList<>(test.getEffects().keySet());
//    Collections.sort(lst);
//    Collections.sort(actual);
//    assertEquals(lst, actual);
  }

  @Test
  void bird1perk() {
    PerkModel test = perkTreeModel.getPerk(3);
    assertEquals(2, test.getPrerequisites().size());
    assertTrue(test.getPrerequisites().contains(1));
    assertTrue(test.getPrerequisites().contains(2));
//    List<String> lst = Arrays.asList("Infectivity", "Land");
//    List<Double> vals = List.of(0.03,0.01,0.08);
//    for (int i = 0; i < vals.size(); i++) {
//      assertEquals(vals.get(i), test.getEffects().get(lst.get(i)));
//    }
//    List<String> actual = new ArrayList<>(test.getEffects().keySet());
//    Collections.sort(lst);
//    Collections.sort(actual);
//    assertEquals(lst, actual);
  }
}
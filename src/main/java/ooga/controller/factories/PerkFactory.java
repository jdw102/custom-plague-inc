package ooga.controller.factories;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.BiPredicate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ooga.controller.parsers.events.EventRecordsCollection;
import ooga.controller.parsers.events.PerkEventRecord;
import ooga.controller.parsers.perks.GroupNameMap;
import ooga.controller.parsers.perks.PerkData;
import ooga.controller.parsers.perks.PerkRecord;
import ooga.controller.parsers.protagonist_type.ModifierRecord;
import ooga.model.actor.Actor;
import ooga.model.actor.Antagonist;
import ooga.model.actor.ModifierNotFoundException;
import ooga.model.actor.PerkModel;
import ooga.model.actor.PerkPrerequisites;
import ooga.model.actor.PerkTreeModel;
import ooga.model.actor.Protagonist;
import ooga.model.gamestate.UserPoints;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.view.DisplayView;
import ooga.view.PerkPurchaser;
import ooga.view.PerkTree;
import ooga.view.PerkTreeNode;
import ooga.view.PerkTreeView;
import ooga.view.PerkView;
import ooga.view.map.RegionView;

/**
 * Class to create PerkModels and overall PerkTreeModel from data read from JSONS
 */
public class PerkFactory {

  //depends on
  private final PerkData perkData;
  private final UserPoints userPoints;
  //creating
  private final PerkTreeModel perkTreeModel;
  private final Map<Integer, List<PerkView>> perkGroupMap;
  private final Map<Integer, PerkTreeNode> perkGroupNodes;
  private final String gameType;
  private final GroupNameMap groupNameMap;
  private final Map<String, Actor> actorMap;
  private final RegionMap regionMap;

  /**
   * Constructor that takes in data classes PerkModels rely on
   *
   * @param perksData
   * @param unconfiguredProtagonist
   * @param userPoints
   */
  public PerkFactory(PerkData perksData, Protagonist unconfiguredProtagonist, Antagonist antagonist,
      UserPoints userPoints, GroupNameMap groupNameMap, String gameType, RegionMap regionMap) {
    this.groupNameMap = groupNameMap;
    perkData = perksData;
    this.regionMap = regionMap;
    perkTreeModel = new PerkTreeModel();
    this.userPoints = userPoints;
    perkGroupMap = new HashMap<>();
    perkGroupNodes = new HashMap<>();
    this.gameType = gameType;
    actorMap = new HashMap<>();
    actorMap.put("Protagonist", unconfiguredProtagonist);
    actorMap.put("Antagonist", antagonist);
  }

  /**
   * Overall method to create each PerkModel
   *
   * @return
   */
  public PerkTreeModel make(DisplayView displayView, ResourceBundle gameSettings,
      EventRecordsCollection eventRecordsCollection)
      throws InvalidActorException, InvalidPerkException {
    PerkPurchaser perkPurchaser = displayView.getPerkPopUpView().getPerkPurchaser();
    Iterator<PerkEventRecord> perkEventRecordIterator = eventRecordsCollection.getPerkEventRecordIterator();
    while (perkEventRecordIterator.hasNext()) {
      PerkEventRecord record = perkEventRecordIterator.next();
      PerkModel perkModel = makeEventPerk(record, displayView);
      addToMap(record.id(), record.groupId(), perkModel, record.name(), record.description(),
          record.imagePath(), record.cost(), gameSettings, perkPurchaser);
      perkTreeModel.addPerk(perkModel);
    }
    for (PerkRecord pr : perkData.getValidPerks()) {
      PerkModel perkModel = makePerkModel(pr, displayView);
      addToMap(pr.id(), pr.groupId(), perkModel, pr.name(), pr.description(), pr.imagePath(),
          pr.cost(), gameSettings, perkPurchaser);
      perkTreeModel.addPerk(perkModel);
    }
    for (int i : perkGroupMap.keySet()) {
      PerkTreeNode node = makePerkTreeNode(perkGroupMap.get(i), 0);
      perkGroupNodes.put(i, node);
    }
    return perkTreeModel;
  }

  public void addToMap(int id, int groupId, PerkModel perkModel, String name, String description,
      String imagePath, double cost, ResourceBundle gameSettings, PerkPurchaser perkPurchaser) {
    perkGroupMap.putIfAbsent(groupId, new ArrayList<>());
    List<PerkView> list = perkGroupMap.get(groupId);
    if (id == 0) {
      list.add(null);
    } else {
      PerkView perkView = new PerkView(id, perkModel, name, description,
          makePerkImage(imagePath), perkPurchaser, cost, gameSettings);
      list.add(perkView);
      perkModel.addObserver(perkView);
    }
    perkGroupMap.put(groupId, list);
  }


  private ImageView makePerkImage(String imagePath) {
    String path = String.format("games/%s/%s", gameType, imagePath);
    // load the graphic using resources
    InputStream iconPath = RegionView.class.getClassLoader().getResourceAsStream(path);
    ImageView imageView = new ImageView(new Image(iconPath));
    return imageView;
  }

  /**
   * Helper method to create single PerkModel
   *
   * @param pr
   * @return
   */
  public PerkModel makePerkModel(PerkRecord pr, DisplayView displayView)
      throws InvalidActorException, InvalidPerkException {
    BiPredicate<Double, Integer> activation = makePredicate(pr.actorName(), pr.id(),
        pr.factorModifiers(), displayView);
    PerkPrerequisites perkPre = makePerkPrereqs(pr.prereqPerks());
    PerkModel prk = new PerkModel(pr.id(), activation, perkPre, pr.cost());
    prk.setEffects(makeEffectsMap(pr.factorModifiers()));
    return prk;
  }

  private PerkModel makeEventPerk(PerkEventRecord perkEventRecord, DisplayView displayView) {
    PerkPrerequisites preReqs = makePerkPrereqs(perkEventRecord.prereqIds());
    BiPredicate<Double, Integer> activation = makeEventPerkPredicate(perkEventRecord, displayView);
    PerkModel perkModel = new PerkModel(perkEventRecord.id(), activation, preReqs,
        perkEventRecord.cost());
    perkModel.setRefundable(false);
    return perkModel;
  }

  /**
   * Helper method to create single PerkPrereqs for some PerkModel
   *
   * @param ids
   * @return
   */
  private PerkPrerequisites makePerkPrereqs(Set<Integer> ids) {
    PerkPrerequisites perkPrereqs = new PerkPrerequisites();
    for (Integer i : ids) {
      perkPrereqs.addId(i);
    }
    return perkPrereqs;
  }

  /**
   * Creates a runnable that causes the adjustment of the modifier.
   *
   * @param modifierRecords the modifier factor
   */
  private BiPredicate<Double, Integer> makePredicate(String actorName, int id,
      List<ModifierRecord> modifierRecords, DisplayView displayView) throws InvalidActorException {
    Actor actor = actorMap.get(actorName);
    if (actor == null) {
      throw new InvalidActorException(actorName);
    }
    BiPredicate<Double, Integer> predicate = (p, i) -> {
      boolean worked = userPoints.adjustPoints(p);
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

  private BiPredicate<Double, Integer> makeEventPerkPredicate(PerkEventRecord record,
      DisplayView displayView) {
    Set<Integer> regionIds = record.regionIDs();
    String sourceName = record.sourcePop();
    String drainName = record.drainPop();
    String factor = record.factor();
    String level = record.level();
    int amt = record.amount();
    BiPredicate<Double, Integer> predicate = (p, i) -> {
      boolean worked = userPoints.adjustPoints(p);
      if (worked) {
        for (Integer id : regionIds) {
          RegionModel region = regionMap.getRegionModelByID(id);
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
        }
      }
      return worked;
    };
    return predicate;
  }

  /**
   * Helper method to create map of factors modified -> numerical effect. Mostly for testing and
   * unnecessary for actual perk operation--could be deleted in future.
   *
   * @param mrs
   * @return
   */
  private Map<String, Double> makeEffectsMap(List<ModifierRecord> mrs) {
    Map<String, Double> m = new HashMap<>();
    for (ModifierRecord mr : mrs) {
      m.put(mr.name(), mr.amount());
    }
    return m;
  }

  public PerkTreeNode makePerkTreeNode(List<PerkView> list, int i) {
    PerkTreeNode root = null;
    // Base case for recursion
    if (i < list.size()) {
      PerkView perkView = list.get(i);

      root = new PerkTreeNode(perkView);
      // insert left child
      root.setLeft(makePerkTreeNode(list, 2 * i + 1));

      // insert right child
      root.setRight(makePerkTreeNode(list, 2 * i + 2));
    }
    return root;
  }

  public PerkTreeModel getPerkTreeModel() {
    return perkTreeModel;
  }

  public void addPerkTrees(PerkTreeView perkTreeView, ResourceBundle gameSettings) {
    for (int id : perkGroupNodes.keySet()) {
      PerkTree perkTree = new PerkTree(groupNameMap.getName(id), gameSettings);
      perkTree.addPerks(0, 0, perkGroupNodes.get(id), true, 0);
      perkTreeView.addPerkTree(perkTree);
    }
  }
}

package ooga.model.actor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ooga.controller.factories.InvalidPerkException;

/**
 * A wrapping class for the PerkModel classes that also handles logic for activating the perk and
 * prerequisites.
 */
public class PerkTreeModel {

  private final Map<Integer, PerkModel> perkModelMap;

  public PerkTreeModel() {
    perkModelMap = new HashMap<>();
  }

  public void addPerk(PerkModel perk) {
    perkModelMap.put(perk.getId(), perk);
    perk.updateAvailability(isPerkAvailable(perk));
  }

  public PerkModel getPerk(int id) throws NullPointerException {
    return perkModelMap.get(id);
  }

  public int getNumPerks() {
    return perkModelMap.keySet().size();
  }

  /**
   * Activates a perk if the perk is available for purchase.
   *
   * @param id the id of the perk
   */
  public void togglePerkActivation(int id) throws InvalidPerkException {
    PerkModel perk = perkModelMap.get(id);
    boolean available = isPerkAvailable(perk);
    perk.updateAvailability(available);
    if (available) {
      perk.toggleActivate();
    }
    preparePerks();
  }

  /**
   * Disables perks that are active when they are not available. Used to handle situation where user
   * refunds prerequisite perk of another perk.
   */
  public void preparePerks() throws InvalidPerkException {
    for (PerkModel perk : perkModelMap.values()) {
      boolean isAvailable = isPerkAvailable(perk);
      perk.updateAvailability(isAvailable);
      boolean isActive = perk.isActive();
      if (!isAvailable && isActive) {
        perk.toggleActivate();
      }
    }
  }

  /**
   * Checks the prerequisites of a perk by checking if the perks with listed ids are active.
   *
   * @param perk the perk to be checked
   */
  private boolean isPerkAvailable(PerkModel perk) {
    PerkPrerequisites prerequisites = perk.getPrerequisites();
    boolean ret = true;
    while (prerequisites.hasNext()) {
      int id = prerequisites.next();
      PerkModel preReq = perkModelMap.get(id);
      ret = ret && preReq.isActive();
    }
    return ret;

  }

  public Iterator<Integer> getIdIterator() {
    return perkModelMap.keySet().iterator();
  }


}

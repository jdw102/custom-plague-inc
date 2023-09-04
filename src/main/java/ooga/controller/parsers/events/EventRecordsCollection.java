package ooga.controller.parsers.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A data class that contains a collection of records for random and perk events to be used in the
 * perk and event factories respectively.
 */
public class EventRecordsCollection {

  private final Collection<RandomEventRecord> randomEvents;
  private final Collection<PerkEventRecord> perkEvents;

  public EventRecordsCollection() {
    randomEvents = new ArrayList<>();
    perkEvents = new ArrayList<>();
  }

  public void addRandomEvent(RandomEventRecord e) {
    randomEvents.add(e);
  }

  public void addPerkEvent(PerkEventRecord e) {
    perkEvents.add(e);
  }

  public Iterator<RandomEventRecord> getRandomEventRecordIterator() {
    return randomEvents.iterator();
  }

  public Iterator<PerkEventRecord> getPerkEventRecordIterator() {
    return perkEvents.iterator();
  }


}

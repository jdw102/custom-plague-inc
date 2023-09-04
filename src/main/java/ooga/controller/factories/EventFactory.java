package ooga.controller.factories;

import java.util.Iterator;
import java.util.Set;
import ooga.controller.parsers.events.EventRecordsCollection;
import ooga.controller.parsers.events.RandomEventRecord;
import ooga.model.gamestate.GameModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.model.region.RandomEvent;
import ooga.model.region.RegionMap;
import ooga.model.region.RegionModel;
import ooga.model.region.RegionState;
import ooga.view.DisplayView;

public class EventFactory {

  private final RegionMap regionMap;

  public EventFactory(RegionMap regionMap) {
    this.regionMap = regionMap;
  }

  public void make(EventRecordsCollection eventRecordsCollection, DisplayView displayView,
      GameModel gameModel) {
    Iterator<RandomEventRecord> randomEventRecordIterator = eventRecordsCollection.getRandomEventRecordIterator();
    while (randomEventRecordIterator.hasNext()) {
      RandomEvent randomEvent = makeRandomEvent(randomEventRecordIterator.next(), displayView);
      displayView.addEvent(randomEvent);
      gameModel.addEvent(randomEvent);
      randomEvent.addObserver(displayView.getEventPopUp());
    }
  }

  private RandomEvent makeRandomEvent(RandomEventRecord record, DisplayView displayView) {
    Runnable runnable = makeEventRunnable(record);
    return new RandomEvent(record.probability(), runnable, record.message());
  }

  private Runnable makeEventRunnable(RandomEventRecord record) {
    String sourceName = record.sourcePop();
    String drainName = record.drainPop();
    int amt = record.amount();
    String factor = record.factor();
    String level = record.level();
    Set<Integer> regionIds = record.regionIds();
    Runnable runnable = () -> {
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
    };
    return runnable;
  }
}

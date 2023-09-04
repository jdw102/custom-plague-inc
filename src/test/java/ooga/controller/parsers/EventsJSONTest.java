package ooga.controller.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Iterator;
import ooga.controller.parsers.events.EventRecordsCollection;
import ooga.controller.parsers.events.EventsJSON;
import ooga.controller.parsers.events.PerkEventRecord;
import ooga.controller.parsers.events.RandomEventRecord;
import org.junit.jupiter.api.Test;

public class EventsJSONTest {

  GameConfigParser gameConfigParser;
  EventsJSON eventsJSON;

  public EventsJSONTest() {
    File f = new File(getClass().getResource("/json_tests/eventstest.json").getPath());
    gameConfigParser = new GameConfigParser();
    eventsJSON = new EventsJSON(gameConfigParser.parseJSONConfig(f));
  }

  @Test
  void testParsing() {
    eventsJSON.parseAllData();
    EventRecordsCollection eventRecordsCollection = eventsJSON.getEventRecordsCollection();
    Iterator<RandomEventRecord> randomEventRecordIterator = eventRecordsCollection.getRandomEventRecordIterator();
    Iterator<PerkEventRecord> perkEventRecordIterator = eventRecordsCollection.getPerkEventRecordIterator();
    while (randomEventRecordIterator.hasNext()) {
      assertEquals("There has been a coup in Eurasia!", randomEventRecordIterator.next().message());
    }
    while (perkEventRecordIterator.hasNext()) {
      assertEquals("Launch a brief TV campaign in Western Europe.",
          perkEventRecordIterator.next().description());
    }
  }


}

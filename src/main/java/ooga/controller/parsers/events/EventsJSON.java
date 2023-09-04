package ooga.controller.parsers.events;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.Set;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;

/**
 * A class that parses the events json config file and creates records for both random events and
 * perk events.
 */
public class EventsJSON extends DataFileParser {

  private final EventRecordsCollection eventRecordsCollection;

  public EventsJSON(JsonNode topNode) {
    super(topNode);
    eventRecordsCollection = new EventRecordsCollection();
  }

  @Override
  public void parseAllData() {
    JsonNode eventsNode = topNode.get("events");
    for (JsonNode eventNode : eventsNode.get("randomEvents")) {
      eventRecordsCollection.addRandomEvent(createRandomEvent(eventNode));
    }
    try {
      for (JsonNode eventNode : eventsNode.get("perkEvents")) {
        eventRecordsCollection.addPerkEvent(createPerkEvent(eventNode));
      }
    } catch (NullPointerException e) {

    }

  }

  public EventRecordsCollection getEventRecordsCollection() {
    return eventRecordsCollection;
  }

  private RandomEventRecord createRandomEvent(JsonNode eventNode) {
    Set<Integer> ids = new HashSet<>();
    for (JsonNode id : eventNode.get("regionIds")) {
      ids.add(id.asInt());
    }
    double probability = eventNode.get("probability").asDouble();
    String source = ParserUtils.getNonBlankString(eventNode, "sourcePop");
    String drain = ParserUtils.getNonBlankString(eventNode, "drainPop");
    int amount = eventNode.get("amount").asInt();
    String factor = null;
    String level = null;
    try {
      factor = ParserUtils.getNonBlankString(eventNode, "factor");
      level = ParserUtils.getNonBlankString(eventNode, "level");
    } catch (NullPointerException e) {

    }
    String message = ParserUtils.getNonBlankString(eventNode, "message");
    return new RandomEventRecord(ids, probability, source, drain, amount, factor, level, message);
  }

  private PerkEventRecord createPerkEvent(JsonNode eventNode) {
    int groupId = eventNode.get("groupId").asInt();
    String name = ParserUtils.getNonBlankString(eventNode, "name");
    String imagePath = ParserUtils.getNonBlankString(eventNode, "imagePath");
    String description = ParserUtils.getNonBlankString(eventNode, "description");
    double cost = eventNode.get("cost").asDouble();
    Set<Integer> preReqs = new HashSet<>();
    for (JsonNode node : eventNode.get("preReqIDs")) {
      preReqs.add(node.asInt());
    }
    String source = ParserUtils.getNonBlankString(eventNode, "sourcePop");
    String drain = ParserUtils.getNonBlankString(eventNode, "drainPop");
    int amount = eventNode.get("amount").asInt();
    String factor = null;
    String level = null;
    try {
      factor = ParserUtils.getNonBlankString(eventNode, "factor");
      level = ParserUtils.getNonBlankString(eventNode, "level");
    } catch (NullPointerException e) {

    }
    Set<Integer> ids = new HashSet<>();
    for (JsonNode id : eventNode.get("regionIds")) {
      ids.add(id.asInt());
    }
    int id = eventNode.get("id").asInt();
    return new PerkEventRecord(id, groupId, ids, name, imagePath, description, cost, preReqs,
        source,
        drain, amount, factor, level);
  }
}

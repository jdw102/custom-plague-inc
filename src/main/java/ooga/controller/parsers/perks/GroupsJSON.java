package ooga.controller.parsers.perks;

import com.fasterxml.jackson.databind.JsonNode;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;

public class GroupsJSON extends DataFileParser {

  private final GroupNameMap groupNameMap;

  public GroupsJSON(JsonNode jsonNode) {
    super(jsonNode);
    groupNameMap = new GroupNameMap();
  }

  @Override
  public void parseAllData() {
    JsonNode groupsNode = getTopNode().get("groups");
    for (JsonNode groupNode : groupsNode) {
      String name = ParserUtils.getNonBlankString(groupNode, "name");
      int id = ParserUtils.getIntValue(groupNode, "id");
      groupNameMap.addGroup(id, name);
    }
  }

  public GroupNameMap getGroupNameMap() {
    return groupNameMap;
  }

}

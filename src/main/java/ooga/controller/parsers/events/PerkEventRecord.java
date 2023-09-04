package ooga.controller.parsers.events;

import java.util.Set;

public record PerkEventRecord(int id, int groupId, Set<Integer> regionIDs, String name,
                              String imagePath,
                              String description, double cost, Set<Integer> prereqIds,
                              String sourcePop, String drainPop, int amount, String factor,
                              String level) {

}

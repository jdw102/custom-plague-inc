package ooga.controller.parsers.events;

import java.util.Set;

public record RandomEventRecord(Set<Integer> regionIds, double probability, String sourcePop,
                                String drainPop,
                                int amount,
                                String factor, String level, String message) {

}

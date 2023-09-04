package ooga.model.gamestate;

import ooga.model.region.PopulationNotFoundException;
import ooga.util.Observable;

/**
 * Interface to be implemented by classes that can be tracked by progress bars and act as condition
 * checkers for end game conditions.
 */

public interface Progressor extends Observable {

  double getProgress() throws PopulationNotFoundException;

  boolean checkCondition(Condition condition) throws PopulationNotFoundException;

  double getProgress(String s) throws PopulationNotFoundException;
}

package ooga.model.gamestate;

import java.util.ArrayList;
import java.util.Collection;
import ooga.util.Observer;

public class DefaultUserPoints extends UserPoints {

  private final Collection<Observer> myObservers;

  public DefaultUserPoints(double points, double maxPoints) {
    super(points, maxPoints);
    myObservers = new ArrayList<>();
  }

  @Override
  public boolean adjustPoints(double val) {
    double amt = getPoints() + val;
    if (amt < 0) {
      return false;
    }
    amt = Math.min(amt, getMaxAmt());
    super.adjustPoints(amt);
    notifyObservers();
    return true;
  }

  @Override
  public void addObserver(Observer observer) {
    myObservers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    myObservers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : myObservers) {
      observer.update();
    }
  }

  @Override
  public boolean checkCondition(Condition condition) {
    return condition.check(getPoints());
  }
}

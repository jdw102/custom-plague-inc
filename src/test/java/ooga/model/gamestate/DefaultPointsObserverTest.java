package ooga.model.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ooga.util.Observer;
import org.junit.jupiter.api.Test;

public class DefaultPointsObserverTest {

  UserPoints userPoints;

  public DefaultPointsObserverTest() {
    userPoints = new DefaultUserPoints(0.0, 100);
  }

  private class TestObserver implements Observer {

    double points;
    UserPoints userPoints;

    private TestObserver(UserPoints userPoints) {
      this.userPoints = userPoints;
      points = userPoints.getPoints();
    }

    @Override
    public void update() {
      points = userPoints.getPoints();
    }

    public double getPoints() {
      return points;
    }
  }

  @Test
  void testObserverUpdatesPoints() {
    TestObserver observer = new TestObserver(userPoints);
    userPoints.addObserver(observer);
    double expected = 10;
    userPoints.adjustPoints(expected);
    assertEquals(expected, observer.getPoints());
  }
}

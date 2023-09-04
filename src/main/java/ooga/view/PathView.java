package ooga.view;

import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;
import ooga.model.region.PathModel;
import ooga.model.region.PopulationNotFoundException;
import ooga.util.Coordinate;
import ooga.util.Observer;

public abstract class PathView implements Observer {

  private final String[] trackedPopulations;
  private final Color trailColor;
  private final double trailSize;
  private final double trailOpacity;
  private final double pathSpeed;
  private final PathTransition pathTransition;
  private final PathModel pathModel;
  private final double distance;
  private final Duration duration;
  private final ResourceBundle gameSettings;

  public PathView(Coordinate origin, Coordinate destination, ImageView image, PathModel pathModel,
      Group mapRoot, ResourceBundle gameSettings) {
    this.gameSettings = gameSettings;
    trackedPopulations = gameSettings.getString("PathTrailPopulations").split(" ");
    trailColor = Color.valueOf(gameSettings.getString("PathTrailColor"));
    trailSize = Double.parseDouble(gameSettings.getString("PathTrailSize"));
    trailOpacity = Double.parseDouble(gameSettings.getString("PathTrailOpacity"));
    pathSpeed = Double.parseDouble(gameSettings.getString("PathSpeed"));
    distance = calcDistance(origin, destination);
    mapRoot.getChildren().add(image);
    this.pathModel = pathModel;
    Path path = createPath(origin, destination);
    Circle pen = new Circle(origin.x(), origin.y(), trailSize, trailColor);
    pen.setOpacity(trailOpacity);
    pen.translateXProperty().addListener((ov, t, t1) -> trail(pen, mapRoot, path));
    pen.translateYProperty().addListener((ov, t, t1) -> trail(pen, mapRoot, path));
    duration = Duration.seconds(distance / pathSpeed);
    pathTransition = new PathTransition(duration, path, pen);
  }

  protected Duration getDuration() {
    return duration;
  }

  protected double getDistance() {
    return distance;
  }

  protected double getPathSpeed() {
    return pathSpeed;
  }

  private double calcDistance(Coordinate origin, Coordinate destination) {
    double xD = origin.x() - destination.x();
    double yD = origin.y() - destination.y();
    return Math.sqrt(xD * xD + yD * yD);
  }

  private Coordinate calcCriticalPoint(Coordinate origin, Coordinate destination) {
    double x1 = Math.min(origin.x(), destination.x());
    double x2 = Math.max(origin.x(), destination.x());
    double y1 = Math.min(origin.y(), destination.y());
    double y2 = Math.max(origin.y(), destination.y());
    double x3 = x1 + 0.5 * (x2 - x1);
    double y3 = y1 + 0.5 * (y2 - y1);
    return new Coordinate(x3, y3);
  }

  protected Path createPath(Coordinate origin, Coordinate destination) {
    Path path = new Path();
    Coordinate criticalPoint = calcCriticalPoint(origin, destination);
    path.getElements().add(new MoveTo(origin.x(), origin.y()));
    path.getElements().add(
        new QuadCurveTo(criticalPoint.x() + 80, criticalPoint.y() - 80, destination.x(),
            destination.y()));
    return path;
  }

  private void showError(Exception e) {
    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
    alert.show();
  }

  private void trail(Circle pen, Group root, Path path) {
    double x = pen.getCenterX() + pen.getTranslateX();
    double y = pen.getCenterY() + pen.getTranslateY();
    Circle point = new Circle(x, y, pen.getRadius(), pen.getFill());
    if (path.contains(x, y)) {
      root.getChildren().add(point);
    }
  }

  @Override
  public void update() {
    for (String s : trackedPopulations) {
      try {
        if (pathModel.checkNewlyTransferred(s)) {
          pathTransition.play();
        }
      } catch (PopulationNotFoundException e) {
        showError(e);
      }
    }
  }

  public abstract void pause();

  public abstract void play();

  public abstract void adjustRate(double amt);

}
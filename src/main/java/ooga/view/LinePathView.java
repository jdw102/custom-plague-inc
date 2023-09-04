package ooga.view;

import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.SequentialTransition;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import ooga.model.region.PathModel;
import ooga.util.Coordinate;

public class LinePathView extends PathView {

  private final Coordinate origin;
  private final Coordinate destination;
  private final ImageView image;
  private SequentialTransition animation;

  public LinePathView(Coordinate origin, Coordinate destination, ImageView imageView,
      PathModel pathModel, Group root, ResourceBundle gameSettings) {
    super(origin, destination, imageView, pathModel, root, gameSettings);
    this.origin = origin;
    this.destination = destination;
    this.image = imageView;
    image.setX(origin.x());
    image.setY(origin.y());
    createAnimation();
  }

  private void createAnimation() {
    Path path = createPath(origin, destination);
    animation = new SequentialTransition();
    Duration transitionDuration = Duration.seconds(getDuration().toSeconds() * 0.8);
    Duration fadeDuration = Duration.seconds(getDuration().toSeconds() * 0.1);
    PathTransition transition = new PathTransition(transitionDuration, path, image);
    FadeTransition fadeIn = new FadeTransition(fadeDuration, image);
    FadeTransition fadeOut = new FadeTransition(fadeDuration, image);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    transition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
    double angle = calcAngle(origin, destination);
    image.setRotate(angle);
    animation.getChildren().addAll(fadeIn, transition, fadeOut);
    animation.setOnFinished(event -> {
      image.setX(origin.x());
      image.setY(origin.y());
    });
  }

  private double calcAngle(Coordinate origin, Coordinate destination) {
    double angle = Math.atan2(destination.y() - origin.y(),
        origin.x() - destination.x());
    return 180 - Math.toDegrees(angle);
  }

  @Override
  public void update() {
    super.update();
    image.setVisible(true);
    animation.play();
  }

  @Override
  public void pause() {
    animation.pause();
  }

  @Override
  public void play() {
    animation.play();
  }

  @Override
  public void adjustRate(double amt) {
    animation.setRate(amt);
  }
}

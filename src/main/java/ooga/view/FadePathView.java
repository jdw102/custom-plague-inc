package ooga.view;

import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import ooga.model.region.PathModel;
import ooga.util.Coordinate;

public class FadePathView extends PathView {

  private final Coordinate origin;
  private final Coordinate destination;
  private final ImageView image;
  private SequentialTransition animation;

  public FadePathView(Coordinate origin, Coordinate destination, ImageView imageView,
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
    animation = new SequentialTransition();
    FadeTransition fadeOut = makeFadeTransition(1.0, 0.0);
    FadeTransition fadeIn = makeFadeTransition(0.0, 1.0);
    FadeTransition finalFadeOut = makeFadeTransition(1.0, 0.0);
    fadeOut.setOnFinished(event -> {
      image.setX(destination.x());
      image.setY(destination.y());
    });
    finalFadeOut.setOnFinished(event -> {
      image.setX(origin.x());
      image.setY(origin.y());
    });
    animation.getChildren().addAll(fadeIn, fadeOut, finalFadeOut);
  }

  private FadeTransition makeFadeTransition(double from, double to) {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(getDuration());
    fadeTransition.setFromValue(from);
    fadeTransition.setToValue(to);
    fadeTransition.setNode(image);
    return fadeTransition;
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

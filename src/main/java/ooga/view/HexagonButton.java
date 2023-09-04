package ooga.view;


import static java.lang.Math.cos;
import static java.lang.Math.sin;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class HexagonButton {

  private final double deg = Math.toRadians(60.0);
  private final Button hexButton;
  private final ViewFactory viewFactory;
  private double radius;
  private double setx = 100;
  private double sety = 100;
  private Shape shape;

  public HexagonButton(String name) {
    viewFactory = new ViewFactory();
    Tooltip tooltip = new Tooltip(name);
    this.hexButton = viewFactory.createButton(name, String.format("%sButton", name));
    Tooltip.install(hexButton, tooltip);
  }

  public double getRadius() {
    return radius;
  }

  public Button getButton() {
    return hexButton;
  }

  private Polygon makeHexagon(double radius) {
    Polygon polygon = new Polygon();
    polygon.getPoints().addAll(setx, sety,
        (setx - radius), sety,
        setx - (radius * cos(deg)), sety - (radius * sin(deg)),
        setx + (radius * cos(deg)), sety - (radius * sin(deg)),
        (setx + radius), sety,
        setx + (radius * cos(deg)), sety + (radius * sin(deg)),
        setx - (radius * cos(deg)), sety + (radius * sin(deg)),
        (setx - radius), sety);
    return polygon;
  }

  public void makeButton(double x, double y, double radius, ImageView imageView) {
    this.radius = radius;
    this.setx = x;
    this.sety = y;
    shape = makeHexagon(radius);
    imageView.setFitWidth(radius);
    imageView.setFitHeight(radius);
    hexButton.setGraphic(imageView);
    hexButton.setPrefWidth(radius);
    hexButton.setPrefHeight(radius);
    this.hexButton.setShape(shape);
    this.hexButton.setLayoutX(setx);
    this.hexButton.setLayoutY(sety);
  }

}

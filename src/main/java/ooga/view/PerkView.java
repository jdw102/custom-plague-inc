package ooga.view;

import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import ooga.model.actor.PerkModel;
import ooga.util.Observer;

public class PerkView implements Observer {

  private final int id;
  private final PerkModel perkModel;
  private final HexagonButton button;
  private final String name;
  private final String description;
  private final double radius;
  private final ImageView imageView;
  private final String activeColor;
  private final String inactiveColor;
  private final String selectColor;
  private final String cost;
  private boolean selected;

  public PerkView(int id, PerkModel perkModel, String name, String description, ImageView image,
      PerkPurchaser perkPurchaser, double cost, ResourceBundle gameSettings) {
    this.name = name;
    this.description = description;
    this.id = id;
    this.cost = Double.toString(cost);
    this.perkModel = perkModel;
    this.imageView = image;
    this.radius = Double.parseDouble(gameSettings.getString("PerkSize"));
    button = new HexagonButton(name);
    activeColor = gameSettings.getString("PerkActiveColor");
    inactiveColor = gameSettings.getString("PerkInactiveColor");
    selectColor = gameSettings.getString("PerkSelectColor");
    button.getButton().setOnAction(event -> onClick(perkPurchaser));
  }

  private void onClick(PerkPurchaser perkPurchaser) {
    perkPurchaser.setPerk(this);
  }

  public void makeButton(double x, double y) {
    button.makeButton(x, y, radius, imageView);
    button.getButton().setOnMouseEntered(event -> onHover());
    button.getButton().setOnMouseExited(event -> offHover());
  }

  public HexagonButton getButton() {
    return button;
  }

  @Override
  public void update() {
    if (perkModel.isAvailable()) {
      fadeInButton();
    } else {
      fadeOutButton();
    }
    if (perkModel.isActive()) {
      setButtonColor(activeColor);
    } else {
      setButtonColor(inactiveColor);
    }
  }

  private void setButtonColor(String s) {
    button.getButton().setStyle(String.format("-fx-background-color: %s; ", s));
  }

  private void onHover() {
    adjustScale(1.1);
  }

  private void offHover() {
    if (!selected) {
      adjustScale(1.0);
    }
  }

  public void adjustScale(double amt) {
    button.getButton().setScaleX(amt);
    button.getButton().setScaleY(amt);
  }

  private void fadeOutButton() {
    button.getButton().setOpacity(0.5);
  }

  private void fadeInButton() {
    button.getButton().setOpacity(1.0);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isActive() {
    return perkModel.isActive();
  }

  public boolean isAvailable() {
    return perkModel.isAvailable();
  }

  public int getId() {
    return id;
  }

  public void select() {
    selected = true;
    setButtonColor(selectColor);
  }

  public void unselect() {
    selected = false;
    if (perkModel.isActive()) {
      setButtonColor(activeColor);
    } else {
      setButtonColor(inactiveColor);
    }
    adjustScale(1.0);
  }

  public String getCost() {
    return cost;
  }
}

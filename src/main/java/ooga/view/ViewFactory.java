package ooga.view;

import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class ViewFactory {

  public void adjustScene(Pane pane, String style, String resourceBundle) {
    pane.getStyleClass().add(style);
    pane.getStylesheets().add(resourceBundle);
  }

  public Button createButton(String id, String style) {
    Button result = new Button();
    result.setId(id);

    if (style != null) {
      result.getStyleClass().add(style);
    }
    return result;
  }

  public TextArea createTextArea(String val, String id) {
    TextArea textArea = new TextArea(val);
    textArea.setEditable(false);
    textArea.setId(id);
    return textArea;
  }

  public TextField createTextField(String val, String id) {
    TextField textField = new TextField(val);
    textField.setEditable(false);
    textField.setId(id);
    return textField;
  }

  public Label createLabel(String val, String id) {
    Label label = new Label(val);
    label.setId(id);
    return label;
  }

  public Slider createSlider(int min, int max, int interval, String id) {
    Slider slider = new Slider(min, max, interval);
    slider.setShowTickLabels(true);
    slider.setId(id);
    return slider;
  }


  public ComboBox createComboBox(String promptText, List choices, String id) {
    ComboBox comboBox = new ComboBox();
    comboBox.setPromptText(promptText);
    comboBox.getItems().addAll(choices);
    comboBox.setId(id);
    return comboBox;
  }

  public ProgressBar createProgressBar(String id) {
    ProgressBar progressBar = new ProgressBar(0);
    progressBar.setId(id);
    return progressBar;
  }

  public Dialog createDialog(String description) {
    Dialog<String> dialog = new Dialog();
    dialog.setContentText(description);
    return dialog;
  }

  public void updateProgressBar(ProgressBar progressBar, int max, int current) {
    progressBar.setProgress(current / max);
  }

  public void setMinWindow(Stage stage, Double height, Double width) {
    stage.setMinHeight(height);
    stage.setMinWidth(width);
  }

  public RadialGradient createColorGradient(Color startColor, Color endColor, Shape shape) {
    RadialGradient gradient = new RadialGradient(0,
        .1,
        shape.getLayoutBounds().getCenterX(),
        shape.getLayoutBounds().getCenterY(),
        Math.max(shape.getLayoutBounds().getWidth(), shape.getLayoutBounds().getHeight()),
        false,
        CycleMethod.NO_CYCLE,
        new Stop(0, startColor),
        new Stop(2, endColor));
    return gradient;
  }
}

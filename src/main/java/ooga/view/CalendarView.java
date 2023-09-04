package ooga.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.scene.control.TextField;
import ooga.model.gamestate.GameData;
import ooga.util.Observer;

public class CalendarView implements Observer {

  private final TextField date;
  private final String startingDate;
  private final SimpleDateFormat simpleDateFormat;
  private GameData gameData;
  private final Calendar calendar;
  private final ViewFactory viewFactory;

  public CalendarView(String dateValue, String dateID, String dateStyle, DisplayView displayView) {
    viewFactory = new ViewFactory();
    date = viewFactory.createTextField(dateValue, dateID);
    startingDate = date.getText();
    date.getStyleClass().add(dateStyle);
    simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    calendar = Calendar.getInstance();
    try {
      calendar.setTime(simpleDateFormat.parse(startingDate));
    } catch (ParseException e) {
      displayView.showError(new DateException(startingDate));
    }
  }

  private String getNewDate(int amount) {
    try {
      calendar.setTime(simpleDateFormat.parse(startingDate));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    calendar.add(Calendar.DATE, amount);
    return simpleDateFormat.format(calendar.getTime());
  }

  public void setGameData(GameData gameData) {
    this.gameData = gameData;
  }

  public TextField getDateField() {
    return date;
  }


  @Override
  public void update() {
    date.setText(getNewDate(gameData.getDay()));
  }
}

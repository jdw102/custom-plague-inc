package ooga.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import ooga.controller.Controller;
import ooga.controller.database.DatabaseException;
import ooga.model.gamestate.GameData;
import ooga.model.gamestate.GameState;
import ooga.model.gamestate.UserPoints;
import ooga.model.region.RandomEvent;
import ooga.util.Observer;
import ooga.view.map.MapView;
import ooga.view.map.RegionView;

public class DisplayView implements Observer {

  public static final String PLAGUEVIEWSTYLE = "plagueview";
  public static final int WIDTH = 1920;
  public static final int HEIGHT = 1080;
  public static final String LEFT_PROGRESS_BAR_ID = "UserPoints";
  public static final String LEFT_PROGRESS_BAR_STYLE = "leftprogressbar";
  public static final String PERK_ID = "Perk";
  public static final String PERK_STYLE = "perkbutton";
  public static final String DONE = "DONE";
  public static final String TEST_DIALOG = "Test Dialog";
  public static final String LEFT_TEXT_AREA_VALUE = "Infected";
  public static final String LEFT_TEXT_AREA_ID = "UserSpread";
  public static final String LEFT_TEXT_AREA_STYLE = "lefttextarea";
  public static final String COUNTRY_DATA_ID = "CountryData";
  public static final String COUNTRY_DATA_STYLE = "countrybutton";
  public static final String RIGHT_TEXT_AREA_VALUE = "Dead";
  public static final String RIGHT_TEXT_AREA_ID = "DeadPeople";
  public static final String RIGHT_TEXT_AREA_STYLE = "righttextarea";
  public static final String RIGHT_PROGRESS_BAR_ID = "AntagonistProgress";
  public static final String RIGHT_PROGRESS_BAR_STYLE = "rightprogressbar";
  public static final String WORLD_DATA_ID = "WorldData";
  public static final String WORLD_DATA_STYLE = "worldbutton";
  public static final String SAVE_BUTTON_ID = "SaveButton";
  public static final String SAVE_BUTTON_STYLE = "savebutton";
  public static final String LOAD_BUTTON_ID = "LoadButton";
  public static final String LOAD_BUTTON_STYLE = "loadbutton";
  public static final String PLAY_BUTTON_ID = "PlayButton";
  public static final String PLAY_BUTTON_STYLE = "playbutton";
  public static final String PAUSE_BUTTON_ID = "PauseButton";
  public static final String PAUSE_BUTTON_STYLE = "pausebutton";
  public static final String FAST_FORWARD_BUTTON_ID = "FastForwardButton";
  public static final String FAST_FORWARD_BUTTON_STYLE = "fastforwardbutton";
  public static final String HEADLINE_VALUE = "This will have some headlines";
  public static final String HEADLINE_ID = "Headlines";
  public static final String HEADLINE_STYLE = "headlinebox";
  public static final String DATE_ID = "Date";
  public static final String DATE_STYLE = "datebox";
  public static final String SETTINGS_PATH = "games.%s.properties.Settings";
  public static final String LANGUAGE = "games.%s.properties.languages.%s";
  private static final String STYLESHEET = "/games/%s/stylesheets/DisplayView.css";
  private static final String SAVE_PATH = "/games/%s/save.json";
  private final double DEFAULT_SPEED;
  private final double FRAMES_PER_SECOND;
  private final double SECOND_DELAY;
  private final List<Double> speeds = List.of(1.0, 1.5, 2.0, 4.0);
  private final String language;
  private final String gameType;
  private final ViewFactory viewFactory;
  private final RegionInfoView regionInfoView;
  private final PerkTreeView perkTreeView;
  private final PerkPurchaser perkPurchaser;
  private final PathViewCollection pathViewCollection;
  private EndMessagePopUp endMessagePopUp;
  private ProtagonistView protagonistView;
  private ResourceBundle languageBundle;
  private BorderPane root;
  private Scene scene;
  private Stage myStage;
  private Controller controller;
  private ProgressBarView leftProgressBar;
  private ProgressBarView rightProgressBar;
  private MapView mapView;
  private Timeline animation;
  private GameState gameState;
  private ScrollPane scrollPane;
  private LabelView leftCounter;
  private LabelView rightCounter;
  private PerkPopUpView myPerkPopUpView;
  private CalendarView calendarView;
  private WorldDataView worldDataView;
  private int speedCounter;
  private Tooltip speedTooltip;
  private HBox topBar;
  private Alert errorAlert;
  private ResourceBundle gameSettings;
  private ExitPopUp exitPopUp;
  private EventPopUp eventPopUp;


  //dummy DV for testing
  public DisplayView() {
    DEFAULT_SPEED = 0;
    FRAMES_PER_SECOND = 0;
    SECOND_DELAY = 0;
    language = "";
    gameType = "";
    viewFactory = null;
    regionInfoView = null;
    perkTreeView = null;
    perkPurchaser = null;
    pathViewCollection = null;

  }

  public DisplayView(String gameType, String language) {
    this.gameType = gameType;
    gameSettings = ResourceBundle.getBundle(String.format(SETTINGS_PATH, gameType));
    speedCounter = 0;
    pathViewCollection = new PathViewCollection();
    languageBundle = ResourceBundle.getBundle(
        String.format(LANGUAGE, gameType, language));
    DEFAULT_SPEED = Double.parseDouble(gameSettings.getString("DefaultSpeed"));
    FRAMES_PER_SECOND = Double.parseDouble(gameSettings.getString("FrameRate"));
    errorAlert = new Alert(AlertType.ERROR);
    errorAlert.getDialogPane().setId("AlertPane");
    SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    this.language = language;
    this.viewFactory = new ViewFactory();
    regionInfoView = new RegionInfoView(String.format(STYLESHEET, gameType), languageBundle);
    perkTreeView = new PerkTreeView();
    perkPurchaser = new PerkPurchaser(languageBundle);
    myPerkPopUpView = new PerkPopUpView(perkTreeView, perkPurchaser,
        languageBundle, gameType);
    worldDataView = new WorldDataView(languageBundle, gameType);
    mapView = new MapView(gameType, gameSettings);
    topBar = createTopBar();
    eventPopUp = new EventPopUp(languageBundle);
  }

  public void setController(Controller controller) {
    this.controller = controller;
    regionInfoView.setController(controller);
    perkPurchaser.setController(controller);
  }

  public void setProtagonistView(ProtagonistView protagonistView) {
    this.protagonistView = protagonistView;
    myPerkPopUpView.setProtagonist(protagonistView);
  }

  public Scene makeScene(Stage stage, EventHandler<ActionEvent> goBack) {
    setUpRegionsOnClick();
    myStage = stage;
    root = new BorderPane();
    exitPopUp = new ExitPopUp(languageBundle, gameState);
    exitPopUp.setActions(event -> deleteSave(), goBack, event -> saveGame());
    endMessagePopUp = new EndMessagePopUp(languageBundle, goBack);
    topBar.getStyleClass().add("topbar");
    root.setTop(topBar);
    root.setCenter(scrollPane);
    scrollPane = new ScrollPane(mapView.getGroup());
    BorderPane.setAlignment(mapView.getMapPane(), Pos.CENTER);
    root.setCenter(scrollPane);
    root.getStyleClass().add(PLAGUEVIEWSTYLE);
    scene = new Scene(root, WIDTH, HEIGHT);
    stage.setResizable(true);
    scene.getStylesheets()
        .add(getClass().getResource(String.format(STYLESHEET, gameType)).toExternalForm());
    GameInfoPopUp gameInfoPopUp = new GameInfoPopUp(languageBundle);
    gameInfoPopUp.open();
    return scene;
  }

  private void adjustSpeeds() {
    speedCounter++;
    if (speedCounter >= speeds.size()) {
      speedCounter = 0;
    }
    double speed = speeds.get(speedCounter);
    Iterator<PathView> iterator = pathViewCollection.getIterator();
    while (iterator.hasNext()) {
      iterator.next().adjustRate(speed);
    }
    animation.setRate(speed);
  }

  public double getAnimationSpeed() {
    return animation.getCurrentRate();
  }

  private double getSpeed() {
    double speed = speeds.get(speedCounter);
    return speed;
  }

  public void showError(Exception exception) {
    errorAlert.setContentText(exception.getMessage());
    pauseAnimations();
    errorAlert.show();
  }

  public HBox createTopBar() {
    leftProgressBar = makeProgressBar("LeftProgressBar");
    leftProgressBar.getNode().setId(LEFT_PROGRESS_BAR_ID);
    leftProgressBar.getNode().getStyleClass().add(LEFT_PROGRESS_BAR_STYLE);
    Button perkButton = createButton(PERK_ID, PERK_STYLE);
    ButtonType type1 = new ButtonType(DONE, ButtonData.OK_DONE);
    Dialog<String> perkDialog = viewFactory.createDialog(TEST_DIALOG);
    perkDialog.getDialogPane().getButtonTypes().add(type1);

    perkButton.setOnAction(event -> {
      myPerkPopUpView.open();
    });
    leftCounter = new LabelView("LeftLabelSubpopulation", gameSettings, languageBundle);
    leftCounter.getNode().setId(LEFT_TEXT_AREA_ID);
    leftCounter.getNode().getStyleClass().add(LEFT_TEXT_AREA_STYLE);

    Button exitButton = createButton("ExitButton", "exitbutton");
    exitButton.setOnAction(event -> exitPopUp.open());

    //Will have to be changed to be more general
    ButtonType type2 = new ButtonType(DONE, ButtonData.OK_DONE);
    Dialog<String> countryDialog = viewFactory.createDialog(TEST_DIALOG);
    countryDialog.getDialogPane().getButtonTypes().add(type2);

    rightCounter = new LabelView("RightLabelSubpopulation", gameSettings, languageBundle);
    rightCounter.getNode().setId(RIGHT_TEXT_AREA_ID);
    rightCounter.getNode().getStyleClass().add(RIGHT_TEXT_AREA_STYLE);

    rightProgressBar = makeProgressBar("RightProgressBar");
    rightProgressBar.getNode().setId(RIGHT_PROGRESS_BAR_ID);
    rightProgressBar.getNode().getStyleClass().add(RIGHT_PROGRESS_BAR_STYLE);

    // World Data View
    Button worldDataButton = createButton(WORLD_DATA_ID, WORLD_DATA_STYLE);
    Dialog worldDataDialog = worldDataView.getDialog();//
    worldDataButton.setOnAction(event -> worldDataDialog.showAndWait());

    ComboBox<String> styleSelector = viewFactory.createComboBox("Theme", List.of("Light", "Dark"),
        "StyleSelector");
    styleSelector.setOnAction(e -> root.getStyleClass().add(styleSelector.getValue()));
    styleSelector.getStyleClass().add("styleselector");
    HBox gameDataBox = new HBox();
    gameDataBox.getChildren()
        .addAll(leftProgressBar.getNode(), perkButton, leftCounter.getNode(),
            regionInfoView.getRegionButton(),
            rightCounter.getNode(), worldDataButton, rightProgressBar.getNode());
    gameDataBox.getStyleClass().add("gamedatabox");
    HBox settingsBox = new HBox(exitButton, styleSelector);
    settingsBox.getStyleClass().add("settingsbox");
    VBox timeBox = createTimeInputs();
    HBox topBox = new HBox(settingsBox, gameDataBox, timeBox);
    topBox.getStyleClass().add("topbox");
    return topBox;
  }

  private Button createButton(String id, String style) {
    Button button = viewFactory.createButton(id, style);
    button.getStyleClass().add(style);
    return button;
  }

  public VBox createTimeInputs() {
    calendarView = new CalendarView(gameSettings.getString("StartDate"), DATE_ID, DATE_STYLE,
        this);
    Button playButton = createButton(PLAY_BUTTON_ID, PLAY_BUTTON_STYLE);
    playButton.setOnAction(event -> playAnimations());
    Button pauseButton = createButton(PAUSE_BUTTON_ID, PAUSE_BUTTON_STYLE);
    pauseButton.setOnAction(event -> pauseAnimations());
    Button fastForwardButton = createButton(FAST_FORWARD_BUTTON_ID, FAST_FORWARD_BUTTON_STYLE);

    speedTooltip = new Tooltip(String.valueOf(speeds.get(speedCounter)));
    Tooltip.install(fastForwardButton, speedTooltip);

    HBox buttonBox = new HBox(playButton, pauseButton, fastForwardButton);
    buttonBox.getStyleClass().add("timebuttonbox");
    fastForwardButton.setOnAction(event -> {
      adjustSpeeds();
      speedTooltip.setText(String.valueOf(getSpeed()));
    });
    VBox headlineBar = new VBox();
    headlineBar.getChildren().addAll(calendarView.getDateField(), buttonBox);
    headlineBar.setAlignment(Pos.BOTTOM_CENTER);
    headlineBar.getStyleClass().add("timebox");
    return headlineBar;
  }

  private void pauseAnimations() {
    if (animation != null) {
      animation.pause();
      Iterator<PathView> iterator = pathViewCollection.getIterator();
      while (iterator.hasNext()) {
        iterator.next().pause();
      }
    }
  }

  private void playAnimations() {
    animation.play();
    Iterator<PathView> iterator = pathViewCollection.getIterator();
    while (iterator.hasNext()) {
      iterator.next().play();
    }
  }

  @Override
  public void update() {
    if (gameState.isRunning()) {
      animation.play();
    } else {
      animation.stop();
      displayEndMessage(gameState.getEndMessage());
      deleteSave();
    }
  }

  private void deleteSave() {
    try {
      File file = new File(getClass().getResource(String.format(SAVE_PATH, gameType)).getPath());
      file.delete();
    } catch (NullPointerException e) {
    }
  }

  private void displayEndMessage(String message) {
    endMessagePopUp.open(message);
  }

  private ProgressBarView makeProgressBar(String key) {
    String type = gameSettings.getString(String.format("%s%s", key, "Type"));
    String className = String.format("ooga.view.%s", type);
    Class<?> clazz = null;
    ProgressBarView progressBar = null;
    try {
      clazz = Class.forName(className);
      progressBar = (ProgressBarView) clazz.getDeclaredConstructor(String.class, String.class,
              ResourceBundle.class, ResourceBundle.class)
          .newInstance(key, type, gameSettings, languageBundle);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException |
             NoSuchMethodException e) {
      showError(new ProgressBarException(type));
      progressBar = new PointsProgressBar(key, "Points", gameSettings, languageBundle);
    }
    return progressBar;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
    animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames()
        .add(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> controller.update()));
    animation.setRate(DEFAULT_SPEED);
  }

  public void addRegionView(int id, RegionView regionView) {
    mapView.addRegionView(id, regionView);
  }

  public void addPathView(PathView pathView) {
    pathViewCollection.addPathView(pathView);
  }

  public Group getMapRoot() {
    return mapView.getGroup();
  }

  public void setUpRegionsOnClick() {
    mapView.setUpOnClick(regionInfoView);
  }

  public void displayPerks() {
    myPerkPopUpView.displayPerks();
  }

  public void setUserPoints(UserPoints userPoints) {
    myPerkPopUpView.setUserPoints(userPoints);
  }

  public PerkPopUpView getPerkPopUpView() {
    return myPerkPopUpView;
  }

  public void setGameData(GameData gameData) {
    leftCounter.setGameData(gameData);
    rightCounter.setGameData(gameData);
    calendarView.setGameData(gameData);
    worldDataView.setGameData(gameData);
  }

  public LabelView getLeftCounter() {
    return leftCounter;
  }

  public LabelView getRightCounter() {
    return rightCounter;
  }

  public CalendarView getCalendarView() {
    return calendarView;
  }

  public ProgressBarView getLeftProgressBar() {
    return leftProgressBar;
  }

  public ProgressBarView getRightProgressBar() {
    return rightProgressBar;
  }

  public WorldDataView getWorldDataView() {
    return worldDataView;
  }

  public ResourceBundle getSettingsBundle() {
    return gameSettings;
  }

  public ResourceBundle getLanguageBundle() {
    return languageBundle;
  }

  private void saveGame() {
    TextInputDialog td = new TextInputDialog();
    td.setHeaderText("Enter username");
    td.showAndWait();
    String username = td.getEditor().getText().replaceAll("\\s+", "");
    if (username.isBlank()) {
      showError(new DatabaseException("Username cannot be blank"));
      return;
    }
    controller.saveGame(protagonistView.getUserName(), protagonistView.getId(), username);
  }

  public void addEvent(RandomEvent e) {
    eventPopUp.addEvent(e);
  }

  public EventPopUp getEventPopUp() {
    return eventPopUp;
  }
}

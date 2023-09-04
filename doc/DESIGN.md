# OOGA Design Final

### Eka Ebong, Diego Miranda, Anika Mitra, Eloise Sinwell, Kevin Tu, Jerry Worthy

## Team Roles and Responsibilities

* Jerry Worthy - Backend design and implementation, frontend design and implementation

* Eloise Sinwell - Parsing and validating JSON, creating backend from JSON, cloud database

* Anika Mitra - Frontend design and implementation (splash screens, display view bar), TestFX

* Eka Ebong - Frontend design and implementation (splash screens, perk view), TestFX

* Diego Miranda - Parser implementation, map reader/generator, and world statistics view

* Kevin Tu - Backend design, calculators for plague growth, spread and point generation

## Design goals

- Our primary goal was to implement a Model, View, Controller design that is as extendable to
  developers
  as possible.
- This means providing a robust API that allows simpler variations of the game to be created
  by changing the many config files that set up the games, and allow more complicated variations of
  the
  game to be created by extending existing interfaces and classes.
- These config files include ones that control the information associated with each region, a
  protagonist, an antagonist, perks,
  paths of transmission, winning and losing conditions, region factors, and the formulas for
  computing
  new region populations and user points.
- These existing interfaces include a hierarchy of models and
  views that interact primarily through observer-observable interfaces and a controller that
  connects them upon
  instantiation and allows the user to interact with the model.

## High-level Design

- Our current design revolves around a series of parsing and data classes in the controller that
  read in the JSON config files,
  instantiate all the model classes, and connect the appropriate model classes to their view
  classes.
- This is done using an observer-observable interface relationship, where the models act as
  observables and the views observe them,
  only updating when the models notify their observers. This allows there to be a constant flow of
  information between the backend and
  front end without the need to directly pass back and forth a data structure of information.
- We have made the core modules for our game general enough that the three variations we implement
  can be entirely
  made with only different JSON files, however we recognize that future developers may want to
  implement more complicated features that require
  the extension of classes.
- The primary classes that we expect to be open for extension include some of those found in the
  state handler package,
  specifically the growth calculator, spread calculator, and points calculator used by their
  respective model classes,
  as their may be too big of differences in how populations grow, spread, and points are distributed
  across the different variations.
- Other classes open for extension include the UserPoints class that tracks the points of the game,
  the PointsModel which is what holds the PointsCalculator
  and updates the UserPoints, and the GrowthModel, which uses the GrowthCalculator and
  SpreadCalculator to update the states of the RegionModels.
- Specifically our design is currently open to different games/scenarios that have different
  populations than just healthy, infected and dead.
  We can also support different perks/upgrade trees, different modes of spreading, and different
  maps through JSON files.
- We can also change game balance parameters like spreading rate, point generation, and difficulty
  through JSON files as well as
  set different win and lose conditions for different scenarios that are based on different factors
  such as time passed or value of a population.
- There are also the operation classes, which are instantiated using reflection from the JSON config
  files to allow for different mathematical
  operations to be done on the different stats of the protagonist of the game.
- The overall game model we expected to remain closed however, as well as the parsing of the actual
  config files.

### Core Classes

Within the Model, View, and Controller packages, our core classes will be as follows.

#### Model

- GameModel
    - Contains a collection of RandomEvent instances, a PointsModel, GrowthModel, GameState,
      Protagonist,
      Antagonist, RegionMap, and RegionPaths.
    - Calls the update methods of the PointsModel and GrowthModel, determines which RandomEvents (if
      any) will be
      activated, and allows for starting of the game and toggling of the GameState from running to
      not running through
      the controller.
- GrowthModel
    - Uses the GrowthCalculator and SpreadCalculator to determine the next RegionState of each
      RegionModel in the RegionMap
      by calculating the growth of each population and randomly determining how the
      SpreadablePopulations will spread.
    - Also updates the progress of the Antagonist if it is active in any of the RegionModels.
- PointsModel
    - Uses the current and next RegionState to calculate how many points the UserPoints class will
      be adjusted by.
- GameState
    - Holds the GameData class and keeps track of whether the game is currently running and whether
      the user has lost
      or won a game by holding two instances of EndConditions, one for winning and one for losing,
      and checking these conditions
      after each iteration. It also updates the GameData class.
- GameData
    - Tracks the amount of days since the game has started (amount of updates) and tracks the total
      census of the populations over time for use by the front end view classes.
- PerkTreeModel
    - Contains and handles the activation and refunding of the PerkModel classes through controller
      method
      calls from the frontend by the user. Acts independently of the GameModel.
- Protagonist
    - Implements the actor class in that it contains Modifiers for the different region factors that
      affect how different populations grow, as well as how they spread through Path factors, and
      other
      calculations involving its Core factors as determined by the JSON config files/
- Antagonist
    - Also implements the actor class in that it contains Modifiers for only different region
      factors
      that determine how much an individual RegionModel contributes to its overall progress.

#### View

- DisplayView
    - Primary view class that wraps all other view classes.
- ProgressBarView
    - Abstract view class that has 4 child classes, PointsProgressBar, TimeProgressBar,
      PopulationProgressBar, and AntagonistProgressBar.
    - Observes class that implement the Progressor interface and allow tracking of respective
      progress.
    - Has max value and color set in config files.
    - A left and right progress bar are found in the DisplayView and can be configured
      to track any of the 4 progressors above through the GameSettings config file.
- LabelView
    - Tracks population as a counter. Observes the GameData class. Population chosen can be
      configured.
    - Two are found in DisplayView and observe GameData.
- RegionView
    - Observes individual RegionModel classes and changes opacity to reveal a different color
      based on the amount of a configured population.
- RegionInfoView
    - A pop-up that depending on the region selected displays the region factors, population
      distributions,
      and description of a region.
- PerkPopUpView
    - Contains the PerkTreeView, which contains different tabs containing different PerkTrees, which
      contain different PerkViews that observe their respective PerkModels.
    - Also has Purchaser for allowing the user to purchase a perk and observes the UserPoints
      for immediate update of points.
      -CalenderView
    - Observes GameData and converts integer day to date since starting date configured in config.
- WorldDataView
    - Observes the GameData and displays a graph of the population distributions over time.
- ProtagonistSelector
    - Links to controller and allows user to select the ProtagonistType and transfer its
      respective ProtagonistView to the DisplayView upon game start.

#### Controller

- Controller
    - Handles the instantiation of the Model classes and links them to their respective Observers in
      the DisplayView. Contains instance of DisplayView to show error messages.
    - Contains DataCollector class that wraps all JSON parsing classes to collect the necessary
      information
      for the factory classes to instantiate.
        - Parses JSON files in a specific order to account for inter-file data dependencies.
          Validates all
          information to prevent later errors occurring at actual game runtime and provides thorough
          error
          handling and value substitution when possible.
    - Holds the GameModel and PerkTreeModel to allow for updating of the state of the game and
      to allow the user to purchase perks by id when clicking on a PerkView and start the game
      by the id of a region by clicking on a RegionView.
    - Update method is called by animation timeline step method in front end.
- SaveFileWriter & SaveFileLoader
    - Takes information from RegionMap, PerkTreeModel, UserPoints, Antagonist, GameData, and
      ProtagonistView to save
      the state of the RegionModel instances, the ids of all the activated perks, the name of the
      protagonist and id, and the amount of points, antagonist progress, and days passed there
      currently are in a JSON Object.
    - The loader can take a JSON Object of the same structure as what is saved and distribute the
      information
      to the correct classes to load in a state.
    - Held in Controller and called by user using controller methods.

## Assumptions that Affect the Design

- We assume that games support English, Spanish, and French
- Users can only save 1 instance of a game to the cloud under a single username (new saves for that
  username of that game will overwrite the old one)
- Users can not save local games, only to the cloud

### Features Affected by Assumptions

- The save/load feature has a simpler implementation that only allows 1 instance of a game to be
  saved per username. If a user tries to save a game that they already have a save for, the new save
  will overwrite the old one.
- The possible languages are not configurable - they are set resource bundles in each game
- However, a new language could be added to all games by adding a resource bundle to each of their language folders.

## Significant differences from Original Plan

- Had planned to include a game area editor as our challenging extension (before implementing saving
  games to the web), which would allow the user to draw a map, which the game would use as its map
  configuration
- SpreadModel combined into GrowthModel.
- StateCalculator split into Growth, Spread, and Points Calculator
- Separate PointsModel for handling point generation.
- Slight rearrangement of hierarchy where GameModel holds the RegionMap and RegionPaths and passed
  them into the Points and Growth models.
- Only one bigger controller rather than a separate one for perks.
- PointsModel contains UserPoints.
- GameData now only tracks global census over time and the day, rather than the protagonist stats over time.

## New Features HowTo

- In order to add all the features listed below in "Easy to Add Features", we simply edit the
  corresponding JSON config files
- To create a new game the user would need to create a folder in the "games" folder found in resources
  and create an instance of each JSON config file, CSS file, and properties files with corresponding
  images. 
- Users can define their different protagonists options in protagonists.json, perks in perks.json,
  random events and perk events in events.json, win and lose conditions in win and lose.json, regions
  in regions.json, methods of transmission in paths.json, and the specifics by which populations grow,
  spread, and produce user points in growth, spread, and points.json effectively. Antagonist specifics
  are edited in antagonist.json and region factors are specified fully in factors.json.
- Some other features we expect to be fairly universal to most games, but if a new game needs these
  to be changed, the relevant code is open to extension
    - For instance, the default Growth and Spread models are already highly configurable through
      config files with different "BiOperations", however if some new game requires a drastically
      different model, one can create a new class which implements the relevant interface

### Easy to Add Features

- New maps, with different methods of spread and spread paths. Can also modify map specific features
  like climate
- Different game modes (and respective style classes/display view) (e.g. Plague, Money, Slander,
  Monster)
- Distinct win and loss conditions (can be based off of protagonist and antagonist spread)
- Different parameters for how quickly the protagonist spreads/antagonist growth, and how region
  properties like climate affect it
- Different upgrades for the perk tree for each game mode
- Different perks that cause events in specific regions that can change population distributions and factors.
- Adding factors to regions through event perks.
- New math operations can be added in the util package by implementing Operation and BiOperation. Operation
  is for operations on a single value, BiOperation is for two values. These are used in totalling factor
  collections among other calculations. You can simply type the name of the operation where you want it in
  the config file.
- New conditional operations can be added by similarly implementing the ConditionalOperation.
- Population tracking ProgressBars can be added by configuring the properties files correctly.

### Other Features not yet Done

- Planned headlines (read a JSON of headlines that correspond to calendar dates), this will require
  a new parser and modifying the headline view class
- When the user opens a new window, pop-ups (headlines and antagonist information) from both screens
  will continue to show because both games are running. To fix this, we would add one line of code
  that pauses other games when you open a new window
- Tracking protagonist stats over time and saving those stats to the database.
- Saving random event status in database.
- Saving global census over time in database.
- Cure mode was unable to be implemented because of how vastly different from the regular mode it is,
  this possibly could have been implemented through the Event perks with more time.
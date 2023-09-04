# OOGA Backlog
### Team Plague Inc.
### Eka Ebong, Diego Miranda, Anika Mitra, Eloise Sinwell, Kevin Tu, Jerry Worthy

Here we outline six use cases per team member that describe specific features each person expects to 
complete, focused on features that we want to complete within the first two sprints.

### Eka Ebong 
1.	Path Animations
      a.	User clicks a game version that is verified by the system
      b.	User inputs their actor’s name.
      c.	System generates a map based on game given
      d.	User clicks on first country for spread.
      e.	Protagonist spread begins and system reads in pngs.
      f.	These pngs are shown on the map as a connection between ports (such as airplane and boat animation.
2.	Region Animation
      a.	User clicks on a game version that is verified by the system
      b.	User inputs their actor’s name.
      c.	System generates a map based on game given
      d.	User clicks on first country for spread.
      e.	Protagonist spread begins as system calculates infection spread.
      f.	Based on the region’s population, the opacity of the region is changed to reflect the exposed population vs the unexposed population.
3.	Perk Tree
      a.	User clicks a first region for spread
      b.	System generates user points based on exposed population
      c.	User clicks on User Point icon
      d.	System generates and displays current actor information and possible perk trees
      e.	User clicks on first perk tree to affect infectivity
      f.	System generates a perk tree
      g.	System calculates the current unlocked perks and displays all locked and unlocked perks.
      h.	System calculates the current perks that can be unlocked based on current user points.
      i.	User clicks on a button to buy a perk
      j.	System calculates the effect of the perk on effectiveness on actor factors
      k.	System calculates new infection numbers based on region factors and the unlocked perk’s effect on those region factors.
4.	Image Creation
      a.	User clicks a button to select version of game
      b.	System finds config file for version of game
      c.	System shows the game with specific images
5.	File Creation
      a.	User clicks on a save button
      b.	Save button triggers a save method
      c.	File Chooser launches, allowing user to pick a save location
      d.	System send filepath to the backend and file writer notes the game version, and current important values such as User Points, infected and uninfected, and date
6.	Showing All Available Games
      a.	User opens game
      b.	System shows first welcome splash screen with play button
      c.	User clicks play button
      d.	System responds with method that creates a new scene
      e.	System locates all config files and creates buttons with thumbnails accordingly

### Diego Miranda
_Parsing JSON files_

Create JSON parser wrappers that provide easy ways for 
salient models (like actor, perks, regions) to obtain data.
1. Create an **abstract** JSON parser wrapper class 
   1. This abstract class will provide an abstraction for more parsers that handle parsing for specific files, such as the region, antagonist, actor, antagonist, and perk models.
   2. 
2. Create an extended wrapper for WorldModel
3. Create an extended wrapper for RegionModel
4. Create an extended wrapper for RegionStateModel
5. Create an extended wrapper for PathModel
6. Create an extended wrapper for PerkModel

### Anika Mitra
1. Work on Headlines feature (still considering how to implement this - resource file, config file?)
   1. If resource package - think about how to implement headlines (as a function of time? region?),
otherwise have config file write all possible headlines
   2. Implement model logic that decides when a given headline should be displayed
   3. Create the view class (scrolling bar?) to display these messages
2. Set up PerkView (which Eka will use to create the Perk Trees)
   1. Parser/controller reads perk config file
   2. Parser/controller creates some kind of data structure to hold all relevant information
   3. Iteratively cycle through all perks and create PerkViews using name and image
   4. These will be arranged by PerkTreeView and using the logic of PerkTreeModel
   5. As a user: there will be a Perk tab that opens up a tree of possible perks. You must obtain
all parents of a perk to have it be available. Not available perks should be greyed out and not clickable.
3. Set up StatsView
   1. Parser/controller constantly updates the stats for region & global infected and dead
   2. StatsView calls on this controller every step/update
   3. Create some kind of stats view (button, progress bar, box, ?)
   4. Consider how the user should be able to interact with these features
4. Create a SplashScreen for game variation
   1. Should allow user to choose between the Plague, the Cure, and So You Want to be a Billionaire?
   2. FileChooser to upload all necessary config files
   3. Load correct animation view for the given selection
5. Making a map view from the map generator 
6. Consider the Cure Model
    1. What factors will need to change/be more general between variations>

### Eloise Sinwell
1. Map Generator: modifying region data through drawing colors on grid in MapGen program
   1. User selects a color from UI Colorpicker
   2. User clicks and drags over some cells in GridView, coloring cells this color
   3. Program checks if this color is assigned to an existing known region
      1. If yes: adds number of cells painted this color to number of cells for this Region and removes these cells from the Region they were part of before
      2. If no: creates a new Region entry with sequential ID and color set to this color, then does the same as above--moves appropriate number of cells to part of this Region
   4. Program checks that sum of num cells for each region type equals 10,000 (expected val): if not, throw an error
      1. Maybe later we will set up more advanced cell-region tracking to know which cell exactly is present for multiple regions, and be able to deal with this more intelligently
2. Map Generator: modifying Region data from TableView in MapGen
   1. User clicks on an editable cell in TableView, such as Name (or Color)
      1. Cells that are not editable: ID, num of Cells
   2. Cell becomes a textField, user inputs new value for Name and commits change
      1. Color input will also be a String (hex val, user will just have to write this. Maybe later we can more directly connect a click on a Color cell and then a click on the ColorPicker)
   3. TableView calls controller -> controller checks that value is valid as it holds overall information (for Colors, check that it's a valid RGB hex val)
   4. If valid, controller calls backend RegionData object and sets this RegionData's Name(/Color) property to the new value
      1. If invalid, it just gets ignored--nothing happens
   5. Controller marks its observableList of RegionData as updated, triggers observer (TableView) to update and display this new set value
3. Map Generator: save painted Map from MapGen to csv to use in actual Game
   1. User clicks some "save" button
   2. Save button triggers some save method
      1. Launches a FileChooser fileDialog, allowing user to pick save location and filename for csv file
      2. Then chosen filepath is sent to backend and some file writer goes through GridModel's cells and writes these each to a CSV
         1. As it goes through these, it checks that values for each cell are valid/known Region IDs. If it is not, writes a 0 for this cell (no Region)
4. Map Generator: delete some RegionData
   1. User inputs desired ID of Region to delete to some TextField
   2. TextField checks this value
      1. If not an integer, ignore
      2. If not an ID of an existing RegionData, ignore
   3. If it is a valid Region's ID, delete this RegionData
      1. Controller tells GridModel to set all Cells of this ID in the Model to default ID value, 0
      2. Controller moves that number of cells from num cells in this RegionData to num cells in 0's RegionData
      3. Controller deletes this RegionData from its list of all RegionDatas
      4. Controller tells GridView to update colors of cells (cells whose IDs are newly set to 0 will be updated to the color that maps to 0, which should be WHITE)
5. Parsing JSON: API that creates RegionModels from map file and Region JSON file
   1. Preread files: Map, RegionFactors, Path
   2. Region JSON file read. For each entry in Region JSON file, API creates a RegionModel
      1. Checks that most vital values are specified, such as ID, and that these values are valid (ID must > 0)--if not, no region gets created
      2. Populates values expected by default, such as ID, birthrate, name, etc
         1. If some non-vital default value is  not specified, use some known default value--likely set within JSON file. We haven't decided this for sure yet, though
      3. Checks that path connections defined for this region are valid path IDs. (An additional API will create appropriate PathModels for each of these as they're read).
         1. If not a valid Path ID, ignore
      4. Checks and sets up Region factors
         1. If valid value for valid region factor, set this up
         2. For invalid situations (as specified below), will just use default value for this region factor as specified in RegionFactors JSON
            1. Invalid value for valid region factor
            2. Invalid region factor
            3. No region factor
      5. (Another API will set up RegionView model for these Regions)
   3. After setting up all Regions:
      1. Checks if there are any IDs specified in Map file that did not get defined in a region. If so, ignore this--these cells essentially become 0s.
         1. Perhaps later we could set up some "default" region that gets created for unknown, unspecified regions
      2. Checks if any Regions are claimed to have path connections to other Regions for path types not set for that Region. If so, delete these paths (or maybe only create Paths at the end? Up to Path creation functionality)
6. Parsing JSON: API that creates PerkModels from Perk JSON file
   1. Preread files: RegionFactors, Paths, ProtagonistTypes
   2. Perk JSON file read. For each entry in Perk JSON file, API creates a PerkModel
      1. Checks that most vital values are specified, such as ID, and that these values are valid (ID must >= 0)--if not, no Perk gets created
      2. Populates values expected by default, such as ID, cost, description, etc
         1. If some non-vital default value is not specified, use some known default value--likely set within JSON file. We haven't decided this for sure yet, though
      3. Checks and sets up factors modified
         1. (Expecting naming conventions to work as such: modifiers for region factor values will be {regionfactortype}Effectiveness (such as aridEffectiveness), modifiers for actor factors will be {protagonistfactor} (such as infectivity), modifiers for path types will be {pathtype}Effectiveness (such as airEffectiveness))
         2. Checks that each factor specified is a valid one, as expected from naming conventions and known existing factors from pre-read files. There will likely be enums created for these
            1. If not a valid factor, ignore
            2. If is a valid factor, save this to the PerkModel
   3. Once all PerkModels are created, go through each and check that the ID specified in each Perk's prereqID list is a valid ID for a perk. If not, ignore this prereq. If a prereqID list ends up empty, the perk will have available set to true and be available by default.
      1. (Another API will likely join this process to construct our PerkTree model)

### Kevin Tu
1. Create GrowthMathModels abstract class
   1. Child classes use some sort of math algorithm to determine the next RegionState
   2. Contains determineNextState abstract method, takes in a Region object, a Protagonist object, and generates thje next RegionState object at the next time step
   3. Contains calculate points abstract method, which takes in a region, and finds the probability that it will generate a point, based on infection/spreading parameters
   4. Contains calculate antagonist rate abstract method should use math to calculate rate of cure progress in a region
1. Create various instance of GrowthMathModels subclasses for different types of spread models
   1. Create a linear model or an exponential model for calculating death rate
   2. Create a exponential model for spread rate
   3. Create a logarithmic model for points. (Since spread is exponential, we don't want too many points to be given away)
2. Write parsers for which GrowthMathModel to use
   1. Write parser that uses reflection to choose the right GrowthMathModel subclass to use for each item we are tracking
   2. Checks resource file for appropriate MathModel to use for internal spreading.
2. Create SpreadMathModel
   1. Has a method which takes in a Path object and protagnist object. Depending on the
3. Create WorldDataModel
   1. Keeps track of infection statistics at each point in the game
   2. Contains Map of subpopulation objects which keeps track of how many healthy, infected, and dead people there are in the world at each point in time.
   3. WorldDataModel object can later be used to generate graphs of infections/deaths over time
   4. WorldDataModel object can also be used to evaluate different win conditions, contains checkWinCondition method
   5. Also can keep track on constants like total world population
   6. Keep track of number of days passed
   7. Keep track of the total number of infected regions
   8. Expose stats from Map using an API with getter methods so that frontend can display infected stats
   9. Expose stats like number of infected countries and days passed so frotnend can display it.
7. Work on create json files associated with parameters related to spreading. Create reasonable spreading parameters, and work on math.json which contains which growthMathModel to use for each parameter we are spreading. 

### Jerry Worthy
1. Each infected region has its subpopulations changed appropriately between days, in the base game
   this means the number of infected, dead, and total grows or shrinks.
2. If the user selects a perk, the actor has the stats associated with those perks updated by the
    appropriate modifier.
3. The world model has its stats updated correctly between in game days, this includes global
    populations for each subpopulation.
4. After each in game day, all possible paths are iterated through and randomly some of them trigger path animations, of
   those that trigger path animations, some randomly infect their destination regions.
5. When a Path is meant to have its destination region infected, it is able to properly update the next state of that region.
6. After each iteration of the day, the losing and winning conditions of the WorldDataModel are checked and the
   antagonist's status is checked to determine if the game is lost as well. Different methods exist for winning and losing
   within the GameModel, and a view class that observers these stats is triggered when it occurs.
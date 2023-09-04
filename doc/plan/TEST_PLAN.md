# OOGA Test Plan
## Team Plague Inc.
### Eka Ebong, Diego Miranda, Anika Mitra, Eloise Sinwell, Kevin Tu, Jerry Worthy

### Strategies to make our APIs more testable
1. Keep parameter lists short so as not to violate the Single Responsibility design principle. 
2. Prevent state leaks across test by avoiding forms of global mutable state.

### Test Scenarios

#### Eka
1. Testing Path Animations: writing TestFX that check that the right animations are showing up at the right time based on information given in the back end.
2. Perk Trees: Creating a test in which the user tries to unlock a perk that they do not have the points to access. This would
   be an TestFX that should fail due to information in the backend.
3. Testing Available Names: providing the code with different number of config files that should generate a different number of games based on the actual games available.

#### Diego
1. Testing for valid resource filepaths that are used by the JSON parser.
2. Testing for invalid data types provided. Invalid required data raises exceptions that describe which
data was needed, but was found to be invalid. Unnecessary data entries are simply ignored, such as comments.
3. Tests that ensure all required/expected values are valid. This ensures that in the case of missing 
data entries within the JSON files, a set of default values is used.

#### Anika
1. Testing headline logic through HeadlineView (test "sad" case where headline does not affect animation -> is this okay?)
2. Testing StatsView at win & loss conditions
3. Testing PerkView when perks are not available/already been used

#### Eloise
1. Testing basics of map generator output functionality: that output CSV matches grid as held within map generator program, including that all numbered cells are correct and that port symbols are added correctly
2. Testing map generator UI/JavaFX: writing TestFX to make sure that map generator operates as expected (choosing colors, painting with new/existing colors and creating/modifying RegionData correspondingly, clicking on TableView and modifying from there)
3. Testing that RegionModels are set up correctly from JSON files, including that unrecognized values are ignored/set to defaults AND that JSON files so far from expected values as to be unuseable (missing vital values such as ID) throw errors

#### Kevin

#### Jerry
1. Testing the GameController: this would involve making sure the model that tracks the satistics of the game
    is correctly updating its view and that any methods that change the model from the view update the model
   properly as well.
2. Testing the PerkController: this would involve making sure that the on-click events of the
    view classes for the perks are triggering the correct changes in their respective models through
   the controller method, such as if they are being set to active and actually modifying the actor 
    model.
3. Testing the RegionController: this would involve making sure that the controller is properly
   linking the region models with their views by checking the observers of the models after set up,
   and also making sure the correct factors are read into them after parsing the JSON files.
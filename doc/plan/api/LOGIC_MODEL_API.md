# Logic Model API Design Overview
## Team Plague Inc.
### Jerry Worthy

#### Overview
The goal of the Logic Model API is to handle the changes that occur in the many stats of the overall game, the user, 
and the regions between each in game day. 

#### Classes
Classes include the overall GameModel abstract class, the RegionGrowthModel interface, and the RegionSpreadModel 
interface, all of which can be found in the model.region package.

#### Example
An example of how this API would work would be in stepping through the animation of the game and progressing to 
the next day. This would cause the update method to be called in the larger game model, where the RegionGrowthModel calls
the handleGrowthStates on the region map it contains and the RegionSpreadModel calls handlePathStates on the PathModels it contains, after each call
the RegionGrowthModel updates the states of all the RegionModels. 

#### Details
This API represents the actual logic of the game that is taking place between each in game day and is triggered passively over time as long
as the user does not pause the game. Every time the update() method of the GameModel is called, the logic of how the changes will take place
unfolds. Ideally, in most of our games this will involve calling the handlers in both the RegionGrowthModel and RegionSpreadModel that set the 
next states of each RegionModel according to their own internal logic. Because they are interfaces, this logic could be changed depending on implementations
of different games, offering us more flexibility down the road.

#### Consider
Issues that need to be addressed include specifying the logic contained within each Model, and how the GameModel will relate to and pass data to other classes
such as the view classes that need to display it. Our initial idea for solving this is having almost all data the needs to be displayed to the user
regarding the state and stats of the Game to be contained within the WorldDataModel API, which will be observed by whatever view classes need that information.


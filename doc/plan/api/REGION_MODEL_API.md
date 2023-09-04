# Region Model API Design Overview
## Team Plague Inc.
### Jerry Worthy

#### Overview
The goal of the Region Model API is to allow access to all the regions within the game,
as well as access to and the ability change the state of their subpopulations, antagonist progression,
and their behavior with paths between other countries.

#### Classes
Classes include the RegionMap abstract class that implements iterator, the RegionNode
interface, the Region abstract class that implements observable and RegionNode, the RegionState 
class that holds state information of the region, and the Census abstract class that is held by the RegionState
class and that holds the population of each subpopulation. All of these can be found in the model.region package.

#### Example
An example of how this API would work would be in the actual logic of the game. For instance, each Region object held
in the RegionMap would be iterated through, their next states would be changed one by one by some larger logic model, and then
they would be iterated through again to update their current states to their next states.

#### Details
This API represents what is being edited by the larger logic models, and will trigger events in the view based on 
how they are changed. For example, every increase in the infected population of a RegionModel will trigger an 
increase in opacity of a certain color in the RegionView through the observer-observable relationship. This one operation
depends on all the components of the API working, with iteration through the RegionMap, updating of the RegionState
objects contained in the Region class through the RegionNode interface, and changing the Census parameter
to indicate the change of a subpopulation.


#### Consider
Issues that need to be addressed include how to determine which subpopulation fraction will be reported to the 
RegionView, which can be decided in a JSON file, determining a primary infection population that will be kept track
of in the View. However, this brings out another issue, that being how transfer the data from the JSON into the
datastructures and classes used in the API. Which will likely revolve around classes in the controller. THis

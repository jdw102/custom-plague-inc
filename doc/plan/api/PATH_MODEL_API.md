# Path Model API Design Overview
## Team Plague Inc.
### Diego Miranda

#### Overview
The goal of the PathModel API is to provide a pathway that connects two regions, and allows
the spread of a actor from one region to the next. The Path API allows for there to be different 
types of paths that connect two regions, A and B, together. These path types have inherently unique 
actor spreading properties that provide great flexibility across various game types.

#### Classes
Classes:
* `ooga/model/path/PathModel.java`

The following is a list of the method headers:

    Region getRegionA()
    Region getRegionB()
    PathType getPathType()
    boolean isInfected()
    void infectDestination()

#### Example
Path creation:

    Path examplePath = new Path(regionA, regionB, new PathType("sea"), false);
    if (mathModelDecidesRegionShouldBeInfected){
        examplePath.infectDestination();
    }

#### Details
The example I've provided shows how the Path API can be used to infect a specific region. Since the
Path API only maintains the path information between two regions, any external logic may be used here 
to determine whether the target destination should be affected. This is where the beauty of the
abstraction lies, since it enables the transmission of the actor to any target destination
based on any randomEvent, such as an infection spread from one country to the next (in pandemic mode), or
an advertisement being rolled out in a random country that causes a product to sell (in capitalist mode).

Another area of control the user has here is in the exact implementation of the `infectDestination()` method.
Since this class has access to the target region and its state, it is able to update the state on its own terms
when it comes to infecting it. For example, it may decide to only increase the infected population by 1, or by 100. 

#### Consider
Some further design considerations to improve this API would be to add extended functionality and control
over the target destination's subpopulation. For example, if when in pandemic mode and a cure is found,
we may want to increase the healthy population of the region and therefore decrease the infected population.
Having a readily available API call to adjust different populations may add this flexibility.
# Math API Design Overview
## Team Plague Inc.
### Anika Mitra

#### Overview
This API handles the math for both the inter- and intra- region spread of the actor. This upholds the
Interface Segregation Principle because we separate the interfaces for spreading the actor within 
and between regions.

#### Classes
GrowthMathModel: src/main/java/ooga/model/math/GrowthMathModel.java
SpreadMathModel: src/main/java/ooga/model/math/SpreadMathModel.java

The classes contained are GrowthMathModel which handles the math for user points and antagonist rate for 
spread inside a given region, and SpreadMathModel which determines whether a path is triggered and
a destination is infected in a given path using a probability calculator and a random number generator.

#### Example
One example of how this API would be used before calling any view transportation animations, we will need to
check whether a path is a) triggered and b) whether the destination region is infected.

Path path = new Path(A, B, type, false);
if (pathTriggered(path)) {
    PathView pathView = new PathView(path, type);
    if (destinationInfected(path)) {
        path.infectDestination();
    path.notifyObservers();

#### Details
This API will have to interact heavily with the Region Model API to be able to access the changing states
of every region, and thus the world. It will be called on whenever we update a subPopulation because
any change in the number of dead or infected can potentially change the user points.

#### Consider
We definitely need to consider the mathematic logic in these Models, and how to have this API interact with the
View classes that will display various paths and/or infections.
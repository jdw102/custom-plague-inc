# User Model API Design Overview
## Team Plague Inc.
### Anika Mitra

#### Overview
The goal of the User Model API is to handle the math when the user points are affected by any factor.
This upholds the single responsibility principle as this class only does one thing and therefore only has
one reason to change.

#### Classes
UserModel: src/main/java/ooga/model/math/UserModel.java

The main class is UserModel interface, which extends Observable as there will have to be a UserPointView that
extends Observed. It will also perhaps include a UserPointsModel that contains the logic for defining user points.
This class will have to throw Exceptions if the logic makes the user points less than 0.

#### Example
One example of how this API would be used is after randomEvent that has heavily affected the subpopulations
of a region, the UserPointsModel would calculate the user points to be shown by the UserPointsView.
So, if a path has just gone from Region A to Region B, and Region B is now infected, as infected and
dead subpopulations increase, we will calculate the user points using this API.

    handlePathStates(actor, userModel) {
    while(RegionPaths.hasNext()) {
    path = Region.next();
    if (mathModel.pathTriggered(path)) { // SpreadMathModel
    if (mathModel.destinationInfected(path)) {
    path.infectDestination();
    path.notifyObservers();
    userModel.adjustPoints(path.getPoints()); // path.getPoints reads them from the JSON file

#### Details
This API represents the mathematical logic of deciding if other regions are infected given a path, and how
transmission within a region affects points. Thus, this API will be in direct communication with the path view,
user points, and world data model. Since they are interfaces, we can easily change the mathematical logic, allowing
us to make paths and transmission more or less likely - this could be used to create levels.

#### Consider
We can consider whether this API will also include public methods for the UserPointsView class. Given that we
are separating most of the model and view classes into separate API's this seems like an obvious solution,
especially because we must also uphold the Open-Closed Principle.
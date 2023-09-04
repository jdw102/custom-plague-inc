# OOGA Design Plan

### Team Plague Inc.

### Eka Ebong, Diego Miranda, Anika Mitra, Eloise Sinwell, Kevin Tu, Jerry Worthy

# Design Goals

Our primary goal is to implement a Model, View, Controller design that is as extendable to developers 
as possible. This means providing a robust API that allows simpler variations of the game to be created 
by changing the many config files that set up the games, and allow more complicated variations of the 
game to be created by extending existing interfaces and classes. These config files include ones that 
control the information associated with each region, a actor (infection, cure, product), perks, 
paths of transmission, winning and losing conditions, region factors, and the formulas for computing 
new region populations and user points. These existing interfaces include a hierarchy of models and 
views that interact primarily through observer-observable interfaces and multiple controllers.

# Primary Architecture

The primary interfaces that allow for direct modification of the view by the model are the Observable 
and Observer interfaces. Each model class that's changed will trigger a change in the view will implement 
Observable, while the respective view class will implement Observer. We aim to keep these interfaces 
closed. The primary architecture of our design consists of a hierarchy of model classes. This includes 
a larger model that contains a model representing the user and handles the points the user will accrue 
over the course of the game and models that handle the majority of the logic regarding the spreading 
of the actor. This larger model contains abstractions for a region and the state of that 
region, an abstraction that holds all possible paths between regions, a model that handles calculations 
across days, a model that handles an antagonist (such as cure progress), and a model that wraps all 
the models for the perks. The view API would consist of the observers of these models. A larger display 
class will wrap all of these and observe an abstraction that holds the stats we want displayed to the 
user and produce any messages to the user. Contained within this will be smaller region views, perk views, 
and the path animations components that all observe their respective models. The constructing and linking 
of each observer with its observable would occur in multiple controllers. This could include a basic 
game logic controller, a perk controller, and a region controller that create the instances of each 
model and view, populates them with the correct JSON data, and then links them together.

# Extendability
The extendability of our design is most easily done using the JSON files. Developers could create entire 
new game types with different ways the actor (infection, cure, product, etc.) can be inhibited 
or promoted by different region factors, different path types, different actor parameters, different 
subpopulations, and different parameters that affect how these subpopulations grow between days. Beyond this,
if a future developer wanted to change the way the probability of certain events was calculated they would need
to create a new implementation of the math model interface. Similarly, for the view, if a future developer wanted
to add new animation types for paths between regions, they would need to extend the components related to the 
path view. Also, if they wanted to add a new statistic to track or update not related to subpopulations or user points, then they would need to extend or the model in 
the backend handling the tracking of these statistics and extends the view component that observes it so that it
can display the new stats. This is true of any core aspect of how the game models communicate with the game views needs to
be changed or if the is information or animations that need to be displayed that are not currently accounted for 
in either the model or view. 

# UML Doc

[This is a link to our UML document](https://lucid.app/lucidchart/a93faab1-f459-4818-8dcd-2f0a5402a8cc/edit?viewport_loc=-5068%2C260%2C4440%2C2076%2C0_0&invitationId=inv_827731cd-dc27-4bbd-accc-6008e6fd1f76)
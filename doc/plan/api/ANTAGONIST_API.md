# Antagonist API
## Team Plague
### Author: Kevin Tu


#### Overview

In this game, we consider the "antagonist" to be the cure

The purpose of this API is to provide an interface to track cure progress in the disease. In particular, the antagonist has a cure rate, that can be updated. THe user can also get the value of the cure rate and see if the cure is complete.

#### Classes
Antagonist.java: src/main/java/ooga/antagonist/Antagonist.java


#### Methods

double getRate();

double updateRate(RegionMap regionMap, Protagonist actor);

double update();

boolean isComplete();

#### Example

antagonist.updateRate(regionMap, actor);
antagonist.update()
if(antagonist.isComplete()){
    System.out.println("Cure complete")
}




#### Details
double getRate();

Gets the current cure rate

double updateRate(RegionMap regionMap, Protagonist actor);

Updates the current cure rate based on characteristics of each region, and actor characteristics, e.g. infectivity
A lot of flexibility in implementation, but characteristics of each region can be complicated 

double update();

Update cure progress based on the cure rate

boolean isComplete();

Check if the cure is complete. If true, then cure if finished, and with typical win conditions, we consider this as a loss for the player



#### Consider

This API can be extended in the future in order to support a "The Cure" gamemode, where a player can play as the cure.


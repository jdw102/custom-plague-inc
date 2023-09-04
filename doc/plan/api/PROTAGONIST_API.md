# Protagonist API
## Team Plague
### Author: Kevin Tu


#### Overview

In this game, we consider the "actor" to be the disease which is intending to infect the world.

The Protagonist API is designed to interface with the actor to view/modify attributes of the actor.

The API is designed to allow users to fetch data for various modifiers the disease has (e.g. infectivity, cold resistance, etc.), depending on the requirements of the game/properties of the disease.

The API also allows users to change/update these modifiers, as well as get a list of all available modifiers.

The methods in this API are generic to any possible modfiers a disease could have, and are open to being implemented by any disease, following open-closed principle. Code that deals with instances of disease objects should operate on Protagonist objects, following single-responsibility principle.

#### Classes
Protagonist.java: src/main/java/ooga/actor/Protagonist.java


#### Methods
int getModifier(String modifier);


int multiplyModifier(String modifier, double amount);

int setModifier(String modifier, double amount);

List<String> getFields();

#### Example

multiplyModifier(String "infectivity", double 2);
This code may be called when the user buys a perk which double's the disease's infectivity.

for(String field: actor.getFields()){
    setModifier(field, 0)
}
This code will set all of a protagnist's modifiers to 0. Probably not useful.


#### Details

int getModifier(String modifier);

Gets the value of the modifier with name modifier from the actor

int multiplyModifier(String modifier, double amount);

Multiplies the value of the modifier with name modifier from the actor by amount. Returns updated value of modifier

int setModifier(String modifier, double amount);

Sets the value of the modifier with name modifier from the actor to amount. Returns updated value of modifier

List<String> getFields();

Gets all the actor fields of the actor



#### Consider

This API will throw exceptions if the user calls getModifier, applyModifier, or setModifier on a modifier that doesn't exist.


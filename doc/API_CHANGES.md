## OOGA API Changes
### Team 8
### Jerry Worthy, Eloise Sinwell, Diego Miranda, Kevin Tu, Anika Mitra, Eka Ebong


#### API #1 Region (RegionModel & RegionMap)

* Method changed: hasAny, willHaveAny in RegionModel

    * Why was the change made? Made to remove redundancy and code repetition, moved 
      checking of population existence into RegionState.

    * Major or Minor (how much they affected your team mate's code): Minor

    * Better or Worse (and why): Better, removes code duplication but requires more method calls
      to get same result from RegionModel.


* Method changed: getProgress, checkCondition in RegionMap have been removed as the RegionMap no longer implements Progressor.

    * Why was the change made?: The total game census is already tracked by GameData which makes observing RegionMap as a Progressor redundant.

    * Major or Minor (how much they affected your team mate's code): Major, controller had to be refactored to account for change.

    * Better or Worse (and why): Better, much simpler to understand, more single-responsibility in region map.


#### API #2 GameData

* Method changed: all methods related to actor modifier tracking have been deleted.

    * Why was the change made? Did not have time to implement proper feature and was deemed unnecessary.

    * Major or Minor (how much they affected your team mate's code): Minor as nothing related had 
      been integrated yet.

    * Better or Worse (and why): Better in that it is simpler to understand GameData purpose. Worse
      in that there is less overall functionality for data visualization.



#### API #3  StateHandler (FactorCollection & TargetPopulation)

* Method changed: Added totalMod method in FactorCollection that sums all the modifiers of an actor for a specific region model.

    * Why was the change made? Removes the need to perform repeated calculation in calculator classes.

    * Major or Minor (how much they affected your team mate's code): Major, much easier to implement factor collections.

    * Better or Worse (and why): Better, much less code repetition.


* Method changed: TargetPopulation now holds collection of DrainPopulations to allow for multiple drains and includes methods to add and retrieve these DrainPopulations.

    * Why was the change made?: To allow for more flexibility in game configurations.

    * Major or Minor (how much they affected your team mate's code): Major, had to refactor config files and parsing to account for this change.

    * Better or Worse (and why): Better in that it allows for more flexible game design, worse that in that it resulted in
      slighly more nested loops.


#### API #4 Perks (PerkModel)

* Method changed: added setActiveState method that allows direct activation without user points adjustment.

    * Why was the change made?: To allow for perks to be unlocked properly when a save state is loaded.

    * Major or Minor (how much they affected your team mate's code): Minor, does not affect other code.

    * Better or Worse (and why): Better in that allows for proper loading functionality, and potential cheats.

    

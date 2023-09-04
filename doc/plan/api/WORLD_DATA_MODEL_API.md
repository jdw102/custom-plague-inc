# World Data Model API Design Overview
## Team Plague Inc.
### Diego Miranda

#### Overview
The goal of the World Data Model API is to provide a high-level overview of the game's state and
progress. In a nutshell, it provides statistics of overall population, subpopulations, affected regions,
and keeps track of the user's progress. These statistics can all be used to determine if a win
or loss condition is met throughout the game's runtime. Additional world data that may be controlled
are elapsed days, which can be used to trigger events, or be used to determine win or loss conditions.

#### Classes
Classes:
* `ooga/model/WorldDataModel.java`

The following is a list of the method headers:

    public abstract int getTotalPopulation();
    public abstract int getTotalInfectedRegions();
    public abstract int setPoints(int amount);
    public abstract void setTotalInfectedRegions(int amount);
    public abstract void updateSubPopulations(RegionMap regions);
    public abstract void nextDay();
    public abstract boolean checkWinCondition();
    public abstract boolean checkLoseCondition();

#### Example
Path creation:

    void update(WorldDataModel worldData){
        worldData.setPoints(UserMode.getPoints());
        worldData.setInfectedRegion(RegionGameModel.getRegionMap());
        worldData.updateSubpopulation(RegionGrowthModel.getRegionMap());

        if(worldData.checkLossCondition()){ 
            handleGameLossEvent(); 
        }

        if(worldData.checkWinCondition()){ 
            handleGameLossEvent(); 
        }

        worldData.nextDay();
    }

#### Details
The example I've provided demonstrates an example of using the WorldDataModel to handle the control
logic of a Plague Inc. simulation. This particular example shows how to use WorldDataModel in an  `update()`
function, where the world data's information is updated by external sources. The external sources used
here are external models, which wrap the information being updated, as opposed to passing in exposed, unwrapped data.
In this simulation update function, the WorldDataModel is also used to determine if a win or loss condition has
been met. The WorldDataModel in this case may use the information that has been updated by the external
models, and then run its own implemented win or loss condition checks. If either condition has been met, its
status is returned as a simple boolean and the program can handle the outcome of either randomEvent accordingly.
At the end of the update method, the WorldDataModel also receives an update to its internal timekeeping,
which may also be used to determine a win or loss condition.


#### Consider
One design consideration to improve this API would be to improve its timekeeping. As opposed to having
the API directly increment its internal calendar with `nextDay()`, it may be worth to implement
a new model that keeps track of the simulation's time. This would enable other models within the
game to use time-based data to enact events, such as the news headlines that may affect specific regions.

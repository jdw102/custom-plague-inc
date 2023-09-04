# API Review: GameData API

## Part 1
* The GameData encapsulates our design in that it centralizes a lot of the relevant data that will be displayed to
  the user outside the RegionView class, allowing us to take advantage of our Observable-Observer interfaces with it
  being the observable of many of the view classes.
* The abstractions it utilizes are the Observable and ConditionChecker interfaces, as the Observable is what allows
  to have and notify observers of changes, and the condition checker allows for time conditions to be checked as this class
  tracks the days.
* This API is not entirely meant to be flexible outside world census, points, antagonist progress, protagonist stats, and time 
  tracking, as these are universal things that will be the same between games. It is meant mostly as a data class, however depending 
  on how the user sets up the config files, it will hold different population information and act as a condition checker for ending
  time conditions.
* The only error cases that might occur are if the user tries to retrieve data from a day that has not happened yet, which would result
  in a null value. Error handling needs to be added that addresses this in the front end. Although, how the front end
  classes are set up, this should never happen as they should retrieve the max day value from the class itself.

## Part 2
* The GameData is intuitive to learn in that it is a very simple class with a very simple purpose, to track all the important
  data and distribute it to the front end. Methods have very descriptive names of what they do and the update method simply passes 
  parameters given to it to their respective data structures. 
* I think the method names are descriptive enough that it tells the programmer exactly the information they are getting.
  This means the update methods of the observers (where the GameData class will most likely have its methods called) will be
  very easy to understand, specifically it will be easy to know what information is being used to change the view.
* The GameData API is hard to misuse in that there are no specific setter methods, only getter methods for the different
  data that will be passed to the view. There is one update method that is called by the GameState when it updates, but it would not
  make logical sense for a view class to update all the game data directly.
* I believe it follows good design because it follows many of the programming design principles. Firstly, it follows
  the single responsibility principle because its sole purpose is to hold important game data notify observers when it changes. It also
  follows the open closed principle in that this class will not need to be modified, however if we soon decide that it will need
  need to be modified across variations of the game, we can implement an abstract class. Lastly, it follows the interface separation 
  principle in that it uses all the methods provided to it by each of its interfaces.
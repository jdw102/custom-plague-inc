# OOGA Retrospective Discussion
### Names
Eloise Sinwell  
Diego Miranda  
Kevin Tu  
Anika Mitra  
Eka Ebong  
Jerry Worthy  

## Project's current progress

* Stringing together parsing & actual java object creation
* Logic flow from uploading a file -> parsing JSON, making data -> making Java classes
* Figuring out parsing abstractions
* Figuring out “optionals” for JSONs: how we might want to handle/support
* Integrating model & controller into frontend → functional buttons and text areas
* TESTFX testing
* MapReader using proper CSV parsing library
* Use CSV parsing library for robustness, i.e. not depending on our own String splitting logic
* Standardize expected map data format. Duvall (and us, really) would probably appreciate a map data file creation protocol, i.e. “label regions by numbers, use 0 for the ocean, add ‘*’ for airports, ‘&’ for seaports, seaports must be by the ocean” etc.
* Abstraction in the view package
* Animation in the view
* Need to integrate some kind of logic for region infection
* Animation for airplanes and boats
* View dialogs
* Country data (infected, dead)
* World data
* Perk Tree - consider ways to do this? Just placement on a screen/algorithmically?


## Current level of communication

Good intra-team communication, but need more regular meetings between sub-teams to ensure
everyone is on the same page, and the big picture of the project comes together.

## Satisfaction with team roles

* Model team →  more pair programming for logic.
* Model team →  more individual testing.
* Frontend team → need to clearly delineate tasks
* Frontend team → prioritize testing earlier on
* Parsing team →  make more clear goals for this week


## Teamwork that worked well

* Thing #1
  Good separation of tasks between teams.
* Thing #2
  Good intra-team communication


## Teamwork that could be improved

* Thing #1
  Big merges → single issue merge requests, causes confusion between sub-teams.

* Thing #2
  Not necessarily for last week but going forward, more communication between teams as our work becomes more intertwined

## Teamwork to improve next Sprint
* At the beginning of the sprint, create more general issues for the model package.
* At the beginning of the sprint, have discussion with every member present about which issues will be assigned to who.
* More meetings mid-sprint for teams to update other teams on their progress and any design changes.
* Normalize checking in progress and asking other members where they are at with their work.

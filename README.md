ooga
====

This project implements a player for multiple related games.

Names: Eka Ebong, Diego Miranda, Anika Mitra, Eloise Sinwell, Kevin Tu, Jerry Worthy

### Timeline

Start Date: 11/02/2022

Finish Date: 12/13/2022

Hours Spent: 550

### Primary Roles
- Eka:
- Diego: JSON parsing, map generator, world data view graph
- Anika: splash screens, display view top bar, TestFX
- Eloise: json parsing, factories for Java classes from json objects, online database
- Kevin: math and backend
- Jerry: Backend design and implementation, frontend design and implementation


### Resources Used
- Jackson: JSON reading library
- https://www.baeldung.com/jackson-json-node-tree-model
- Google Firebase
- https://developer.android.com/reference/java/net/HttpURLConnection
- Icons
- https://www.iconfinder.com/
- https://www.flaticon.com/
- Occasional coding buddy
- chat.openai.com
- Binary Tree Visualization Based On This
  -https://stackoverflow.com/questions/53584661/how-to-draw-a-binary-tree-in-javafx


### Running the Program

Main class: Main.java

Steps For Starting a Game:
Press play, or new window to open a new instance of the application
Select the game type and language, and press next to continue to the protagonist selection screen, or press load to open the load game screen to retrieve a previous save from the database.
Select the protagonist from the dropdown menu and enter the name.
Press start and wait for the map to build.
Read the start prompt and select the region you wish to start in.



Data files needed: A full set of game files, as can be found in src/main/resources/games. This includes:
- JSON Config:
- antagonist
- events
- factors
- groups
- growth
- lose
- paths
- perks
- points
- protagonists
- regions
- spread
- win
- map.csv
- Stylesheets:
- DisplayView
- GameDataView
- PerkView
- ProtagonistSelection
- Properties:
- Languages: expecting English, Spanish, French
- ProtagonistMaxValues
- Settings
- All icons for paths, perks, ports, and protagonists.
  Please look at previously existing game folders for reference of how these should be structured.

Features implemented:
- Fully configurable population spread-based game, able to model spread of any phenomenon across a map.
- Ability to configure subpopulations and how they grow within regions, spread phenomena and how it affects various spread factors, point generation, and perks that can modify any factors.
- Can save and load instances of games to an online database by username.
- Can configure the structure of perk trees through config files. Perk Trees are read in a preorder traversal by group id, where the id 0 is reserved for null children in the tree.
- Can create perks that affect the antagonist, protagonist, and directly impact regions.
- Can create random events with certain probability that will impact a region by adjusting populations or changing factors and display a message to the user.
- A graph of the population distribution over time can be viewed by the user.
- A region info pop up contains all relevant information about a region, including its population distributions, name, description, and factors.
- Can pause, play, and fast-forward the timeline.
- The population counters at the top can be configured to track any population, as well as the region color changing, path trails, and population progress bars.
- The progress bars at the top can be configured to track points, antagonist progress, time, and populations.
- This is done by changing LeftProgressBarType and RightProgressBarType in Settings.properties and specifying
  a tracked population if it is a population type.
- Both the win and lose conditions for the games can be configured, revolving around time, population, points, and antagonist conditions.
- Users can select between multiple different protagonists with varying stats and developers can add new protagonists through config files.
- Users can select between English, Spanish, and French for any game.
- Maps can be changed through CSV creation.




### Notes/Assumptions

Assumptions or Simplifications:
- We assume that games support English, Spanish, and French
- Users can only save 1 instance of a game to the cloud under a single username (new saves for that username of that game will overwrite the old one)
- Users can not save local games, only to the cloud.

Known Bugs/Issues:
- Initial loading of the game after selecting a protagonist takes around a minute, but do not close applications as it will eventually load.
- For some displays the application will open not in the proper size and the screen will be slightly shifted to the right until set to fullscreen mode.
- If game is not ended or paused when exited it will continue to play in background, causing pop-ups
  from the closed game to appear. This bug is an easy fix but because of bug fix requirements we were
  not able to implement a fix because it proved too difficult to test.
- This bug could be fixed by pausing the previous game upon exit.

Challenge Features:
- Basic extensions: load games, save games, and multiple games at once
- Challenging extension: save game data in web


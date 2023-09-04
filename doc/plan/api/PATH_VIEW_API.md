# Path View API Design Overview
## Team Plague Inc.
### Eka Ebong

#### Overview

The Path View API the interfaces between the model classes of PathType and Path in order to show the correct 
sprites on the UI for the different types of port available in each game type. 

The current methods available in the API follow the single responsibility principle and are versatile to any version of the game. 
They are not dependent on hardcoded values and can be easily changed. 

#### Classes
PathView: src/main/java/ooga/view/PathView.java

'void setAnimation(PathType type);'
* Null Exception
  'void getAnimation(PathType type, ResourceBundle bundle);'
* Illegal Argument Exception

#### Example
getAnimation(Plane, "So You Want to be a Billionaire?); 
setAnimation(Plane); 

In this code example for something as simple as the "So You Want to be A Billionaire?" game option, the methods find the 
correct animation to play for the plane pathtype based on the name of that pathtype and the information for the game found in the 
resource bundle. 
#### Details
This API could be extended to figure out how to implement somthing such as point bubbles, found in the original plague inc. When 
a significant number of people are infected, the game creates a bubble to add User Points, which creating that as a path type, 
we could simulate the same thing with this class.

#### Considerations
The shortcomings of this interface is that there is likely a couple more functions that are important to implements to have a full understanding of 
where the path should be and where the animations should show up. 
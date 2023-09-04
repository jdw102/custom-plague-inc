# Perk Tree View API Design Overview
## Team Plague Inc. 
### Eka Ebong 

#### Overview 
This is an API that gets the perk view and uses the perk tree model to create a view of the perk tree.
The public functions required to do so involve the connections between perks, the current perks that are unlocked and locked.
As well as more perks are unlocked, new PerkViews need to be added to the perk tree.

The public methods inside this class follow the single responsibility principle and should not need to be mutated, that is the interface 
is closed. The code is versatile for the different formats of the game that are available.

#### Classes
PerkTreeView: src/main/java/ooga/view/PerkTreeView.java
PerkTree: src/main/java/ooga/view/PerkTree.java

'int getUserPoints();'
* Null Exception 
'int updateUserPoints(int val, UserPointsModel userPoints, PerkView perkPopUpView);'
* Illegal Argument Exception
'void removePerkView(PerkView perkPopUpView, PerkTreeModel treeModel);'
* Null Exception error would be thrown

'void addPerkView(PerkView perkPopUpView, PerkTreeModel treeModel);'

'void updateTreeView();'
#### Example

getUserPoints(); 
updateUserPoints(13, DNAPoints,lungs);
getUserPoints();
addPerkView(pnemonia, infectivityModel); 
updateTreeView(); 

This short bit of code simulates what might happen when a user clicks on a perk to purchase it. First, the current User Point amount is obtained 
and updated to adjust for the point "price" of the perk. The same method is called to update the view once more. And because the 
perk "lungs" in this example is connected to "pnemonia" by unlocking lungs, pnemonia gets added to the perk tree view and the 
view updates itself to reflect that change. 

#### Details 

This API is important in bridging the Perk View of the individual possible perks, as well as the User Point Model and the Perk Tree Model. 

#### Considerations
Many issues would need to be addressed including the specifics of how the perkPopUpView specifically interacts with the Perk Tree 
as well as what infomations it knows and if it interfaces directly with the model or with the general view. 
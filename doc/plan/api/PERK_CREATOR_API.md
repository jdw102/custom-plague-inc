# Perk Creator API Design Overview
## Team Plague Inc.
### Eloise Sinwell

### Overview
This API will be used to create Perk objects from the Perk JSON and validate the values held in this file. This interface sets up checking for the most core values of Perks--the ones that must be present in any different Perk extension--but allows for actual Perk Creator classes that implement this API to add to these validators or possible values with whatever other factors they care about for that game. It also allows the users to completely define *what* a valid value for these params are, allowing for lots of configurability.
### Classes
controller/parsers/PerkCreator
models/perk/PerkCreationException
models/perk/Perk
### Example
When reading through a ProtagonistTypes file, parser would run:

    addEfficiencyModifierType("urbanEffectiveness");

for all of the factors specified for a actor, and this mapping would be stored however the class that implements PerkCreator wants to store it.
Later, when creating the Perks in makePerks, for some newPerk would run:

    addPerkModifier(newPerk, "urbanEffectiveness", 0.08);
This method would likely be implemented using  the checker `checkIfValidEfficiencyModifier("urbanEffectiveness")`, and, if so, put this in newPerk's perkEffects data structure. If it is not a recognized efficiency modifier, it would probably just skip creating an effect for this factor for this perk--or, it could use some default value specified in the actual implementation of addPerkModifier. And then we would set up PerkCreationExceptions if values for the Perk as defined in the file are so far from the expected values that a perk cannot be created.

### Details
This will likely collaborate with other parser/setup APIs such as ones controlling ProtagonistTypes parsing and the other types of files that need to be read before reading Perk files. It could be extended to validate game-specific Perk params as explained above, then allowing for multiple types of games to run on this system as specified in the project description. Because of this, a user could define any sort of perk and effect that they want, as long as the associated factor the perk affects is a valid one from ProtagonistTypes. Perhaps in the future we might have perks that could directly affect things like the user's point balance. We also envision that this interface can be used to detect whether prereq IDs for some perk are valid using the checkIfPerkIDExistsForProtagonistType method, because we would not want some perk to have a prereq that only exists for an unrelated actor type, meaning you might not have a game scenario where both of these perks would be available, meaning that the secondary perk might be unavailable. If this is the case, whoever implements this interface can decide how they might want to handle these situations, which is another feature of how this interface allows for expandability.
### Considerations
This definitely would not be able to be implemented straightaway--we need to figure out how overall parsing is organized and structured. 
# Region Creator API Design Overview
## Team Plague Inc.
### Eloise Sinwell

### Overview
This API will be used to create Region objects from the Region JSON and validate the values held in this file. This interface sets up checking for the most core values of Region--the ones that must be present in any different Region extension--but allows for actual Region Creator classes that implement this API to add to these validators with whatever other factors they care about for that game. It also allows the users to completely define *what* a valid value for these params are, allowing for lots of configurability.
### Classes
controller/parsers/RegionCreator
models/region/RegionCreationException
models/region/Region
### Example
When reading through a RegionFactors file, parser would run:

    addRegionFactorTypeValue("climate", "arid");

and this mapping would be stored however the class that implements RegionCreator wants to store it.
Later, when creating the Regions in makeRegions, for some newRegion would run:

    setRegionFactor(newRegion, "climate", "arid");
This method would likely be implemented with whatever default values were set for the corresponding region factors in the region factor JSON, and this would likely use the checker `checkIfValidRegionTypeValue("climate", "arid")`, and, if so, put this in newRegion's regionFactors data structure, and if not, put the default value in newRegion's regionFactors data structure.
### Details
This will likely collaborate with other parser/setup APIs such as ones controlling RegionFactors parsing and the other types of files that need to be read before reading Region files. It could be extended to validate game-specific Region params as explained above, then allowing for multiple types of games to run on this system as specified in the project description. And then we would set up RegionCreationExceptions if values for the Region as defined in the file are so far from the expected values that a region cannot be created.
### Considerations
This definitely would not be able to be implemented straightaway--we need to figure out how overall parsing is organized and structured. 
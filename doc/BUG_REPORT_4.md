## Description

Summary of the feature's bug (without describing implementation details)

## Expected Behavior

Describe the behavior you expect

## Current Behavior

Describe the actual behavior

## Steps to Reproduce

Provide detailed steps for reproducing the issue.

1. Upload a map that is not 25x25 for map height = 800, map width = 1400 (standard map size in our Settings.properties)
2. Do MapReader.getRectangleWidth(), MapReader.getRectangleHeight() -- these will equal the above values / 25 
   2. basically: 25 was hardcoded
3. this then causes the scaling of the map to be off in the view

## Failure Logs

## Hypothesis for Fixing the Bug

Test that will show it: essentially exactly above
How to fix: instead of hardcoding 25, do csvData.size() to get number of rows and csvData.get(0).size() to get number of cols
## Description

MapReader takes an extremely long time to load the map.

## Expected Behavior

Should load in under a minute.

## Current Behavior

Takes well over a minute to load.

## Steps to Reproduce

Provide detailed steps for reproducing the issue.

1. Attempt to start any game after selecting a protagonist.

## Failure Logs

You can see the time between the CSV read in and the parser log is extremely long. 

## Hypothesis for Fixing the Bug

Get rid of border randomization.
## Description

GameInfoPopUp was not appearing, was accidentally deleted from DisplayView makeScene method.

## Expected Behavior

After some growth at least one point is produced.

## Current Behavior

No points are produced.

## Steps to Reproduce

Provide detailed steps for reproducing the issue.

1. Start application
2. Press play.
3. Select any slander game type and language.
4. Observe that after some playtime no points are produced.

## Failure Logs

N/A

## Hypothesis for Fixing the Bug

TotalPoints var in PointsCalculator was given type int when it should've been a double.
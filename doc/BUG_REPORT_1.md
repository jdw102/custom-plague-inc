## Description

GameInfoPopUp was not appearing, was accidentally deleted from DisplayView makeScene method.

## Expected Behavior

A pop-up appears at the start of the game explaining the game's rules.

## Current Behavior

The pop-up does not appear.

## Steps to Reproduce

Provide detailed steps for reproducing the issue.

1. Start application
2. Press play.
3. Select any game and any language.
4. Select any protagonist and type in any name.
5. Start the game.
6. Observe no pop-up.

## Failure Logs

N/A

## Hypothesis for Fixing the Bug

Add ID to pop-up dialog pane and test if it is visible. Simply need  to instantiate GamePopUp in display view makeScene method and open it.
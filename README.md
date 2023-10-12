# Minesweeper
An online minesweeper game programmed in java during my last year at the Ecole des Mines de Saint-Etienne school.

## Offline mode

It's a basic minesweeper with 4 different difficulty levels (modifying the number of boxes and mines).
The time since the start of the game and the number of flags remaining are displayed on the left.

## Online mode

It's a competitive game mode, where each discovered square will earn you points.
A preparation phase allows you to wait for players to arrive and change the difficulty of the game.
During the game, players are ranked by number of points.
Propagation is disabled, and you do not see exploded mines or flags placed by other players.
The game ends when all the squares without mines are discovered, or all players have lost.

When you log in online, you must have a unique nickname.
Otherwise, you will be assigned a unique default nickname automatically.
You can then change your nickname normally between 2 games, respecting its uniqueness.

## Menu

#### Switch button

The first button on the right allows you to connect (or disconnect) from online mode.
It is green once connected, gray otherwise.
For the moment, an error message is returned if a connection attempt is made without a response from the server, but this does not prevent the game from continuing in offline mode afterwards.

#### Difficulty button

This button allows you to change the difficulty of the game (easy, normal, hard, diabolical).
In offline mode, the game starts immediately when changing difficulty.
In online mode, changing difficulty is only available during the preparation phase and does not launch the game directly.

#### Settings button

Not set for the moment (soon !).

#### Start game button

This button allows you to start a new game on the difficulty currently selected.
In online mode, this button is not available during a game.

#### Player button
    
On the right, you can see your pseudo, and change it by clicking on it.



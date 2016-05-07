# Bowling Score Board

## Introduction
This is a JAVA program that helps to score a game of bowling. Rules for scoring is sourced from [Ten-pin bowling](https://en.wikipedia.org/wiki/Ten-pin_bowling).

## Example
The ScoreBoard object exposes a couple of useful methods, recordPoints(int points) and scoreBoardTotal().

``` java
ScoreBoard board = new ScoreBoard();
board.recordPoints(4);
board.recordPoints(5);
board.recordPoints(10);

...

board.scoreBoardTotal();

```
Recording points that blows up the allotted points in a frame would get InvalidScoreException.


## How to build the project and run tests
This project is written in JAVA 8 and uses [maven](https://maven.apache.org/download.cgi#Installation) to build.

```
mvn clean install

```

## Assumption
The score board assumes that the user will input scores in sequence as points are accumulated. The current frame in the score board is automatically advanced. For example, when a strike is rolled, the score board automatically advances to the next frame. The scores for frames are updated whenever possible.


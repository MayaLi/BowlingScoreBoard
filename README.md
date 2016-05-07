# Bowling Score Board

## Introduction
This is a JAVA program that helps to score a game of bowling. Rules for scoring is sourced from [Ten-pin bowling](https://en.wikipedia.org/wiki/Ten-pin_bowling).


## How to build the project and run tests
This project is written in JAVA 8 and uses [maven](https://maven.apache.org/download.cgi#Installation) to build.

```
mvn clean install

```

## Assumption
The score board assumes that the user will input scores in sequence as points are accumulated. The current frame in the score board is automatically advanced. For example, when a strike is rolled, the score board automatically advances to the next frame. The scores for frames are updated whenever possible.


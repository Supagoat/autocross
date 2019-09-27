# Autocross tools

This repo contains a scoring program and a work-in-progress worker assignment tool.

The scoring program takes as inputs the TSV registration file used as input to Axware and the TSV export of event result from Axware.  

Scoring is customized for the Boston BMW CCA club where BMWs are classed separately from other cars and no PAX is applied to their times.  

PAX is applied to all cars in other classes.

## Usage

Main class: q.autocross.results.ResultsProcessor

Arguments:

-m # of morning runs
-a # of afternoon runs
-p Path to registration file
-r Path to results file
-o Path to the directory you want the output to be placed in

## Build

Use gradle to build.  If you have gradle installed, run `gradle build copyToLib`.  If you don't have it installed you can use the gradlew script instead.
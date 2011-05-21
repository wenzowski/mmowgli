#!/bin/sh

# Location of script
path=`dirname $0`

# Directory containing jar files
libdir=$path/lib

cpvars=.:$libdir/selenium-grid-hub-standalone-vaadin-testbench-2.2.0.jar

java -cp "$cpvars" com.thoughtworks.selenium.grid.hub.HubServer

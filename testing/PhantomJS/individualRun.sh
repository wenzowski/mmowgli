#!/bin/bash

# This is designed by be run on a unix host. In a clustered environment, eg
# on Rocks (http://www.rocksclusters.org/wordpress/) you can submit this
# script to Sun grid engine (sge) or other scheduler of your choice. This
# is done in the script cluster.sh. Using sge/rocks allows multiple replications
# to run at once.
#
# @author DMcG

# This should be commented out if run in a clustered environment vi cluster.sh.
USERNAME=bot1
PASSWORD=bot1

# Build the classpath. Note the directory we cd to.

cd /Users/mcgredo/projects/GhostDriver
JARS=`ls lib`
CLASSPATH=build/classes

for ELEMENT in $JARS
do
CLASSPATH=$CLASSPATH:lib/$ELEMENT
done

#source /etc/profile
#source /home/mcgredo/.bashrc

# Run the program. Java entry point, URL to go to, username to log in as, password to use,
# and the platform this will run on, eg mac_osx, linux_64, or windows.

java -classpath $CLASSPATH ghostdriver.HealthCheck http://mmowgli.ern.nps.edu/piracy $USERNAME $PASSWORD  mac_osx


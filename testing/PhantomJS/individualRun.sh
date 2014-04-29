#!/bin/bash
cd /home/mcgredo/GhostDriver
JARS=`ls lib`
CLASSPATH=build/classes

for ELEMENT in $JARS
do
CLASSPATH=$CLASSPATH:lib/$ELEMENT
done

source /etc/profile
source /home/mcgredo/.bashrc

HOST=`hostname`
echo Running on host $HOST

echo $CLASSPATH

java -classpath $CLASSPATH ghostdriver.GhostDriver

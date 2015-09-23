#!/bin/bash
cd /home/mcgredo/GhostDriver
JARS=`ls lib`
CLASSPATH=build/classes

for ELEMENT in $JARS
do
CLASSPATH=$CLASSPATH:lib/$ELEMENT
done

#source /etc/profile
#source /home/mcgredo/.bashrc


java -classpath $CLASSPATH ghostdriver.HealthCheck https://mmowgli.nps.edu/training foo bar mac_osx > /dev/null

RESULT=$?

echo result is $RESULT

if [ ${RESULT} -eq "0" ]; then
 echo "Nothing to see here"
else echo Something to see here
fi

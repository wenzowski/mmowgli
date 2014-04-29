#!/bin/bash
#
# Performs a nagios style health check on a mmowlgi game page. Looks
# for the existence of a login button on the page. Returns 0 if the
# login button is there, non-zero otherwise.
#
# Takes as an argument the web page to be tested 
# (eg https://mmowgli.nps.edu/training) and the platform the test is
# running under (mac_osx, linux, windows.) Makes use of the PhantomJS
# javascript headless browser feature, along with selenium.
#
# Arguments: $1 = web page to test
#            $2 = platform the test is running on
#
# @author DMcG


export MMOWGLI_WEB_PAGE=$1
export TEST_PLATFORM=$2

echo page and platform: ${MMOWGLI_WEB_PAGE} ${TEST_PLATFORM}

#cd /home/mcgredo/GhostDriver
JARS=`ls lib`
CLASSPATH=build/classes

for ELEMENT in $JARS
do
  CLASSPATH=$CLASSPATH:lib/$ELEMENT
done

#source /etc/profile
#source /home/mcgredo/.bashrc


java -classpath $CLASSPATH ghostdriver.HealthCheck ${MMOWGLI_WEB_PAGE} foo bar ${TEST_PLATFORM} >  /dev/null

RESULT=$?

#echo result is $RESULT

if [ ${RESULT} -eq "0" ]; then
 echo Web page at ${MMOWGLI_WEB_PAGE} works
else echo Something to see here
fi

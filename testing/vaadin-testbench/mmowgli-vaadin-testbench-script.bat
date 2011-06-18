@REM mmowgli-vaadin-testbench.script

@REM Authors Don Brutzman and Terry Norbraten
@REM created 14 May 2011

@REM	cd not needed since in same directory
@REM	cd H:\vaadin-testbench

IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=.\apache-ant-1.8.2
IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.2
IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.1
IF NOT EXIST  "%ANT_HOME%"    PAUSE Error, Ant not found!
IF NOT EXIST  "%ANT_HOME%"    EXIT
@ECHO ANT_HOME=%ANT_HOME%

IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=..\java\jdk1.6.0_25
IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_26
IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_25
IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24
IF NOT EXIST "%JAVA_HOME%"    PAUSE Error, Java not found!
IF NOT EXIST "%JAVA_HOME%"    EXIT

SET PATH=%ANT_HOME%\bin;%JAVA_HOME%\bin;%Path%;
@ECHO PATH=%PATH%

@ECHO Kill all prior Ant and Java tasks...
start /B "kill.all.java"	ant kill.all.java

@REM --------------------------------------
@ECHO  Shutting down, no tests launched.
@REM   EXIT
@REM --------------------------------------

@ECHO Clearing cache Internet Explorer...
RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255 

@REM Diagnostics:
@REM SET
@REM PAUSE

start "run.hub"		ant run.hub

REM pause 5 seconds
PING 1.1.1.1 -n 1 -w 5000 > NUL

start "run.rc"		ant run.rc

REM pause 5 seconds
PING 1.1.1.1 -n 1 -w 5000 > NUL

@REM choose number of tests based on duration, typical launch periodicity is 60 minutes
@REM    start "run.tests"	ant run.tests
@REM    start "run.5tests"	ant run.5tests
@REM    start "run.15tests"	ant run.15tests
@REM    start "run.25tests"	ant run.25tests
@REM    start "run.50tests"	ant run.50tests
@REM    start "run.100tests"	ant run.100tests

start "run.25tests"	ant run.100tests

REM pause 25 minutes = 1500 seconds = 1500000 milliseconds
REM    or 55 minutes = 3300 seconds = 3300000 milliseconds
PING 1.1.1.1 -n 1 -w 3300000 > NUL

@ECHO killing all running java tasks
ant kill.all.java

@REM PAUSE to inspect console output
@REM exit

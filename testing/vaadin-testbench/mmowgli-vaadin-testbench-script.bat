@REM mmowgli-vaadin-testbench.script

@REM Authors Don Brutzman and Terry Norbraten
@REM created 14 May 2011

@REM IF NOT EXIST %JAVA_HOME%	JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24

@REM	cd not needed since in same directory
@REM	cd H:\vaadin-testbench

@REM debug these statements, probably overkill anyway:
IF NOT EXIST  %ANT_HOME%    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.1
IF NOT EXIST  %ANT_HOME%    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.2
IF NOT EXIST  %ANT_HOME%    @ECHO Error, Ant not found!
IF NOT EXIST  %ANT_HOME%    EXIT
IF NOT EXIST %JAVA_HOME%    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24
IF NOT EXIST %JAVA_HOME%    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_25
IF NOT EXIST %JAVA_HOME%    @ECHO Error, Java not found!
IF NOT EXIST %JAVA_HOME%    EXIT

SET Path=%ANT_HOME%\bin;%JAVA_HOME%\bin;%Path%;
@ECHO PATH=%PATH%

@ECHO Killing prior Ant and Java tasks...
start "kill.all.java"	ant kill.all.java

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

start "run.15tests"	ant run.15tests

REM pause 25 minutes = 1500 seconds = 1500000 milliseconds
REM    or 55 minutes = 3300 seconds = 3300000 milliseconds
PING 1.1.1.1 -n 1 -w 3300000 > NUL

@ECHO killing all running java tasks
ant kill.all.java

@REM PAUSE to inspect console output
@REM exit

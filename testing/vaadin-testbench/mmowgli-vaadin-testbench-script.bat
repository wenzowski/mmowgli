@REM mmowgli-vaadin-testbench.script

@REM Authors Don Brutzman and Terry Norbraten
@REM created 14 May 2011

@REM debug these statements, probably overkill anyway:
@REM IF NOT EXIST  %ANT_HOME%	 ANT_HOME=C:\Program Files\apache-ant-1.8.2
@REM IF NOT EXIST %JAVA_HOME%	JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24

@REM	cd not needed since in same directory
@REM	cd H:\vaadin-testbench

SET  ANT_HOME=C:\Program Files\apache-ant-1.8.2
SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24
SET Path=%ANT_HOME%\bin;%JAVA_HOME%\bin;%Path%;

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

start "run.tests"	ant run.tests

@REM PAUSE
@REM exit

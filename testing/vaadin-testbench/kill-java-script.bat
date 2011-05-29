@REM mmowgli-vaadin-testbench.script

@REM Authors Don Brutzman and Terry Norbraten
@REM created 14 May 2011

@REM debug these statements, probably overkill anyway:
@REM IF NOT EXIST  %ANT_HOME%	 ANT_HOME=C:\Program Files\apache-ant-1.8.2
@REM IF NOT EXIST %JAVA_HOME%	JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24

@REM	cd not needed since in same directory
@REM	cd H:\vaadin-testbench

IF NOT EXIST  %ANT_HOME% SET  ANT_HOME=C:\Program Files\apache-ant-1.8.2
IF NOT EXIST %JAVA_HOME% SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24
SET Path=%ANT_HOME%\bin;%JAVA_HOME%\bin;%Path%;

@ECHO killing all running java tasks
start "kill.all.java"	ant kill.all.java

REM pause 5 seconds
PING 1.1.1.1 -n 1 -w 5000 > NUL

TASKLIST

@REM PAUSE to inspect console output
@REM exit

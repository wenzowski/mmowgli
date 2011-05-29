@REM kill-java-script.bat

@REM Authors Don Brutzman and Terry Norbraten
@REM created 14 May 2011

@REM debug these statements, probably overkill anyway:
@REM IF NOT EXIST  %ANT_HOME%	 ANT_HOME=C:\Program Files\apache-ant-1.8.2
@REM IF NOT EXIST %JAVA_HOME%	JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24

@REM	cd not needed since in same directory
@REM	cd H:\vaadin-testbench

IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=.\apache-ant-1.8.2
IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.2
IF NOT EXIST  "%ANT_HOME%"    SET  ANT_HOME=C:\Program Files\apache-ant-1.8.1
IF NOT EXIST  "%ANT_HOME%"    PAUSE Error, Ant not found!
IF NOT EXIST  "%ANT_HOME%"    EXIT
@ECHO ANT_HOME=%ANT_HOME%

IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=..\java\jdk1.6.0_25
IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_25
IF NOT EXIST "%JAVA_HOME%"    SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_24
IF NOT EXIST "%JAVA_HOME%"    PAUSE Error, Java not found!
IF NOT EXIST "%JAVA_HOME%"    EXIT

SET Path=%ANT_HOME%\bin;%JAVA_HOME%\bin;%Path%;
@ECHO PATH=%PATH%

@ECHO  kill all running java tasks:
start "kill.all.java"	ant kill.all.java

REM pause 5 seconds
PING 1.1.1.1 -n 1 -w 5000 > NUL

TASKLIST

PAUSE to inspect console output, press any key to exit

@REM exit

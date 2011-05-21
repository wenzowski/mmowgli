@echo off
setlocal
@echo Running version: 2.2.0

set USEREXTENSIONS=user-extensions.js
set cpVars=lib\selenium-grid-remote-control-standalone-vaadin-testbench-2.2.0.jar;lib\selenium-server-1.0.1.jar
java -cp "%cpVars%" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -userExtensions %USEREXTENSIONS%  -ensureCleanSession

endlocal
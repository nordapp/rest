@echo off
setlocal enableextensions
set name=OfficeBase Registry
set parent=%~dp0
set parent=%parent:~0,-1%

rem Start the microservice
shift
start "%name%" "%parent%\start-up.cmd" %*

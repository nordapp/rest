@echo off
setlocal enableextensions
set name=OfficeBase Registry
set parent=%~dp0
set parent=%parent:~0,-1%

rem the start loop to restart after a quit
shift

:loop
start /wait "%name%" "%parent%\start-up.cmd" %*
timeout /t 5 /NOBREAK
goto loop


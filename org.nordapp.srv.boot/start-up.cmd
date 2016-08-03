@echo off
setlocal enableextensions
set name=Micro-Controller Bootfile
set parent=%~dp0
set parent=%parent:~0,-1%

set OS_AS_SIZE=0x10000000
set OS_IGNORE_SUSPENDTHREAD_ERROR=1

call "%parent%\find-java.cmd" 18
echo %JVM%
shift

rem usage
rem -h [Volume:]path      Sets the local home directory
rem -c [Volume:]path      Path to the crontab file
rem -b host               The ip or name the server binds to
rem -p port               The port the server is listening

set VMARGS=
set VMARGS=%VMARGS% -jar "%parent%\org.nordapp.srv.boot.jar"
set VMARGS=%VMARGS% -h "%parent%"
set VMARGS=%VMARGS% -c "%parent%\server.conf"
set VMARGS=%VMARGS% -d "%parent%\boot"
set VMARGS=%VMARGS% -v true
set VMARGS=%VMARGS% %*

"%JVM%\bin\java" -version
"%JVM%\bin\java" %VMARGS%

REM exit


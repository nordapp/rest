@echo off

SET JDK_VERSION=jdk1.5.*
if "%1"=="16" (
  SET JDK_VERSION=jdk1.6.*
)
if "%1"=="17" (
  SET JDK_VERSION=jdk1.7.*
)
if "%1"=="18" (
  SET JDK_VERSION=jdk1.8.*
)

REM JDK
for /D %%i IN ("%ProgramFiles%\java\%JDK_VERSION%") DO set JVM=%%~fi

if "%JAVA_HOME%" == "" (
  for /D %%i IN ("%SystemDrive%\%JDK_VERSION%") DO set JVM=%%~fi
)

if "%JAVA_HOME%" == "" (
  for /D %%i IN ("%ProgramFiles%\%JDK_VERSION%") DO set JVM=%%~fi
)
if "%JAVA_HOME%" == "" (
  echo Java %JDK_VERSION% NOT FOUND
)

@REM ----------------------------------------------------------------------------
@REM  Copyright 2001-2006 The Apache Software Foundation.
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
@REM   reserved.

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup
set REPO=


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\repo

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\org\openjfx\javafx-controls\11\javafx-controls-11.jar;"%REPO%"\org\openjfx\javafx-controls\11\javafx-controls-11-win.jar;"%REPO%"\org\openjfx\javafx-graphics\11\javafx-graphics-11.jar;"%REPO%"\org\openjfx\javafx-graphics\11\javafx-graphics-11-win.jar;"%REPO%"\org\openjfx\javafx-base\11\javafx-base-11.jar;"%REPO%"\org\openjfx\javafx-base\11\javafx-base-11-win.jar;"%REPO%"\org\openjfx\javafx-fxml\19\javafx-fxml-19.jar;"%REPO%"\org\openjfx\javafx-fxml\19\javafx-fxml-19-win.jar;"%REPO%"\commons-cli\commons-cli\1.4\commons-cli-1.4.jar;"%REPO%"\org\apache\commons\commons-csv\1.5\commons-csv-1.5.jar;"%REPO%"\com\googlecode\json-simple\json-simple\1.1.1\json-simple-1.1.1.jar;"%REPO%"\com\google\code\gson\gson\2.8.9\gson-2.8.9.jar;"%REPO%"\org\glassfish\javax.annotation\3.1.1\javax.annotation-3.1.1.jar;"%REPO%"\org\glassfish\jersey\core\jersey-client\3.1.0\jersey-client-3.1.0.jar;"%REPO%"\jakarta\ws\rs\jakarta.ws.rs-api\3.1.0\jakarta.ws.rs-api-3.1.0.jar;"%REPO%"\org\glassfish\jersey\core\jersey-common\3.1.0\jersey-common-3.1.0.jar;"%REPO%"\jakarta\annotation\jakarta.annotation-api\2.1.1\jakarta.annotation-api-2.1.1.jar;"%REPO%"\org\glassfish\hk2\osgi-resource-locator\1.0.3\osgi-resource-locator-1.0.3.jar;"%REPO%"\jakarta\inject\jakarta.inject-api\2.0.1\jakarta.inject-api-2.0.1.jar;"%REPO%"\org\glassfish\jersey\inject\jersey-hk2\3.1.0\jersey-hk2-3.1.0.jar;"%REPO%"\org\glassfish\hk2\hk2-locator\3.0.3\hk2-locator-3.0.3.jar;"%REPO%"\org\glassfish\hk2\external\aopalliance-repackaged\3.0.3\aopalliance-repackaged-3.0.3.jar;"%REPO%"\org\glassfish\hk2\hk2-api\3.0.3\hk2-api-3.0.3.jar;"%REPO%"\org\glassfish\hk2\hk2-utils\3.0.3\hk2-utils-3.0.3.jar;"%REPO%"\org\javassist\javassist\3.28.0-GA\javassist-3.28.0-GA.jar;"%REPO%"\jakarta\activation\jakarta.activation-api\2.0.1\jakarta.activation-api-2.0.1.jar;"%REPO%"\org\json\json\20210307\json-20210307.jar;"%REPO%"\org\mongodb\mongodb-driver-sync\4.5.1\mongodb-driver-sync-4.5.1.jar;"%REPO%"\org\mongodb\mongodb-driver-core\4.5.1\mongodb-driver-core-4.5.1.jar;"%REPO%"\org\mongodb\bson\4.5.1\bson-4.5.1.jar;"%REPO%"\fr\ul\miage\musique\0.0.1-SNAPSHOT\musique-0.0.1-SNAPSHOT.jar

set ENDORSED_DIR=
if NOT "%ENDORSED_DIR%" == "" set CLASSPATH="%BASEDIR%"\%ENDORSED_DIR%\*;%CLASSPATH%

if NOT "%CLASSPATH_PREFIX%" == "" set CLASSPATH=%CLASSPATH_PREFIX%;%CLASSPATH%

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS%  -classpath %CLASSPATH% -Dapp.name="run" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" musique.Launcher %CMD_LINE_ARGS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%

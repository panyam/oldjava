@echo off
set BASE_DIR=.
set OUT_DIR=%BASE_DIR%\classes
rem set JFLAGS=-g -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JFLAGS=-O -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JCC=javac
echo Building Games Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\games\*.java %BASE_DIR%\com\sri\games\snake\*.java %BASE_DIR%\com\sri\games\scrambler\*.java %BASE_DIR%\com\sri\games\mine\*.java 
pushd .
cd %OUT_DIR%
jar -cvf games.jar com\sri\games
popd

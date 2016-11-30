@echo off
set BASE_DIR=.
set OUT_DIR=%BASE_DIR%\classes
rem set JFLAGS=-g -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JFLAGS=-O -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JCC=javac
echo Building MML Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\apps\mml\*.java
pushd .
cd %OUT_DIR%
jar -cvf mml.jar com\sri\apps\mml
popd

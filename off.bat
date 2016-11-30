@echo off
set BASE_DIR=.
set OUT_DIR=%BASE_DIR%\classes
rem set JFLAGS=-g -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JFLAGS=-O -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JCC=javac
echo Building OFF Viewer Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\apps\off\*.java 
pushd .
cd %OUT_DIR%
jar -cvf off.jar com\sri\apps\off
popd

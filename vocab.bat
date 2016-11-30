@echo off
set BASE_DIR=.
set OUT_DIR=%BASE_DIR%\classes
rem set JFLAGS=-g -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JFLAGS=-O -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JCC=javac
echo Building Games Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\apps\vocab\*.java %BASE_DIR%\com\sri\apps\vocab\filters\*.java %BASE_DIR%\com\sri\apps\vocab\orderers\*.java
pushd .
cd %OUT_DIR%
jar -cvf vocab.jar com\sri\apps\vocab
popd

copy %OUT_DIR%\*.jar z:\sri\www\personal\java

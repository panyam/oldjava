cls
@echo off
set JDK_LOC=c:\dev\j2eedk\jdk
set XML_LOC=c:\dev\xerces-1_4_4
set ORA_LOC=c:\orant
set SRC_DIR=src
set OUT_DIR=classes
set DOC_DIR=docs
set JFLAGS=-g

set SOURCES=%SRC_DIR%\svtool\*.java %SRC_DIR%\svtool\core\*.java %SRC_DIR%\svtool\core\adt\*.java %SRC_DIR%\svtool\data\*.java %SRC_DIR%\svtool\data\models\*.java %SRC_DIR%\svtool\data\populators\*.java %SRC_DIR%\svtool\gui\*.java %SRC_DIR%\svtool\gui\anim\*.java %SRC_DIR%\svtool\gui\dialogs\*.java %SRC_DIR%\svtool\gui\views\*.java

if not exist %OUT_DIR% mkdir %OUT_DIR%
if not exist %DOC_DIR% mkdir %DOC_DIR%

echo Packaging Source files...
%JDK_LOC%\bin\jar -cvf src.jar src

echo Compiling Source files...
%JDK_LOC%\bin\javac %JFLAGS% -d %OUT_DIR% -classpath %XML_LOC%\xerces.jar;%ORA_LOC%\jdbc\lib\classes12.zip;ext\macos.jar;ext\napkinlaf.jar;ext\kunststoff.jar;package\svtool.jar;.  %SOURCES%

echo Packaging All Classes...
cd classes
%JDK_LOC%\bin\jar -cvf svtool.jar .
move svtool.jar ..
cd ..
if not exist package mkdir package
if not exist package\ext mkdir package\ext
rem move *.jar package
rem copy ext\*.jar package\ext
rem copy *.bat package

echo Generating Documentation...
%JDK_LOC%\bin\javadoc -sourcepath %SOURCES% -d %DOC_DIR% 

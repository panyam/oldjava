@echo off
set BASE_DIR=.
set OUT_DIR=%BASE_DIR%\classes
rem set JFLAGS=-g -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JFLAGS=-O -extdirs "c:\program files\jdk\jre\lib\ext" -d %OUT_DIR% 
set JCC=javac
pushd .
if not exist %OUT_DIR% mkdir %OUT_DIR%
rem cd %OUT_DIR%
if not exist %OUT_DIR%\images mkdir %OUT_DIR%\images
if not exist %OUT_DIR%\sounds mkdir %OUT_DIR%\sounds
if not exist %OUT_DIR%\cases mkdir %OUT_DIR%\cases
popd
copy %BASE_DIR%\images\*.* %OUT_DIR%\images
copy %BASE_DIR%\com\sri\apps\netsim\cases\*.* %OUT_DIR%\cases
echo Building Core GUI Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\gui\core\*.java %BASE_DIR%\com\sri\gui\core\button\*.java %BASE_DIR%\com\sri\gui\core\containers\*.java %BASE_DIR%\com\sri\gui\core\containers\tabs\*.java %BASE_DIR%\com\sri\gui\core\layouts\*.java %BASE_DIR%\com\sri\gui\core\tooltips\*.java
echo Building Extension GUI Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\gui\ext\*.java %BASE_DIR%\com\sri\gui\ext\drawing\stencils\*.java %BASE_DIR%\com\sri\gui\ext\backgrounds\*.java %BASE_DIR%\com\sri\gui\ext\borders\*.java %BASE_DIR%\com\sri\gui\ext\dialogs\*.java %BASE_DIR%\com\sri\gui\ext\drawing\*.java %BASE_DIR%\com\sri\gui\ext\graph\*.java %BASE_DIR%\com\sri\gui\ext\graph\graphable\*.java %BASE_DIR%\com\sri\gui\ext\graph\renderer\*.java %BASE_DIR%\com\sri\gui\ext\drawing\selectors\*.java
echo Building Utility Classes...
%JCC% %JFLAGS% %BASE_DIR%\com\sri\utils\*.java %BASE_DIR%\com\sri\utils\adt\*.java
pushd .
cd %OUT_DIR%
jar -cvf core_gui.jar com\sri\gui\core
jar -cvf ext_gui.jar com\sri\gui\ext
jar -cvf utils_gui.jar com\sri\utils
popd

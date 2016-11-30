@echo off
set JDK_LOC=c:\dev\j2eedk\jdk
set XML_LOC=c:\dev\xerces-1_4_4
set ORA_LOC=c:\orant
rem set EXT_CLASSES=
set EXT_CLASSES=ext\macos.jar;ext\napkinlaf.jar;ext\kunststoff.jar;ext\slaf.jar;ext\metouia.jar;ext\skinlf.jar;ext\3DLF.jar
set CLASSES=%XML_LOC%\xerces.jar;%ORA_LOC%\jdbc\lib\classes12.zip;%EXT_CLASSES%;svtool.jar;.

%JDK_LOC%\bin\java.exe -classpath %CLASSES% svtool.gui.AppFrame config\config.xml

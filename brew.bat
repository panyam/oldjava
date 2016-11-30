set JAVA_LOC=c:\dev\j2eedk\jdk
set XML_LOC=c:\dev\xml4j
set CLASS_PATH=%EXT_PATH%;%XML_LOC%\xmlParserAPIs.jar;%XML_LOC%\xercesImpl.jar;
set EXT_PATH=ext\macos.jar;ext\napkinlaf.jar;ext\kunststoff.jar
set JFLAGS=
set ARGS=

%JAVA_LOC%\bin\java %JFLAGS% -classpath %CLASS_PATH% com.sri.apps.brewery.wizard.Main %ARGS%

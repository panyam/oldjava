mkdir j:\classes
javac -g:none -classpath %CLASSPATH%;j:\classes -d j:\classes test\*.java basic\*.java basic\button\*.java basic\containers\*.java basic\splitters\*.java basic\tooltips\*.java basic\layouts\*.java basic\tree\*.java ext\*.java ext\dialogs\*.java ext\drawing\*.java ext\eqn\*.java ext\graph\*.java ext\backgrounds\*.java ext\selectors\*.java ext\borders\*.java ext\graph\renderer\*.java ext\graph\graphable\*.java ext\graph\renderer\coordsys\*.java 
copy gui.html j:\classes

# ------------ Variables defined here ------------------

#ALL the directories are defined here

SRC= ViewManager.java Symbol.java ContainerSymbol.java \
	NavigateableSymbol.java TokenSymbol.java BasicSymbol.java \
	MultiCharToken.java EquationArea.java Cursor.java\
	ParagraphSymbol.java Justification.java Cursor.java \
	Embelishment.java StyleRange.java RangeTree.java \
	RangeList.java

# Arguments for CC
#
JAVAC=jikes
JFLAGS=
OTHERS=Makefile 

all: tar
	#$(JAVAC) $(JFLAGS) $(SRC)
	jikes *.java

lc:
	wc $(SRC) 

tar:
	tar -cvf eqn.tar *.java $(OTHERS) #$(SRC) $(OTHERS)
	gzip -q eqn.tar
	mv eqn.tar.gz ..

clean:
	/bin/rm -f *.class

# ------------ Variables defined here ------------------

#ALL the directories are defined here
#BASEDIR=/mnt/dos/f/sri/cs/java
BASEDIR=j:/
OUTDIR=$(BASEDIR)/classes
SRCDIR=$(BASEDIR)/src

# Arguments for Javac
JCC=jikes
CLASS_PATH = $(CLASSPATH):$(OUTDIR)
JFLAGS= -g -d $(OUTDIR) -extdirs /mnt/dos/c/Program\ Files/jdk/jre/lib/ext -classpath $(CLASS_PATH)
DFLAGS = -noindex -public -author -version -package

# How to make .class files from .java files
%.class: %.java
	$(JCC) $(JFLAGS) $<

CORE_GUI_SRC =   \
	com/sri/gui/core/*.java \
	com/sri/gui/core/button/*.java \
	com/sri/gui/core/containers/*.java \
	com/sri/gui/core/containers/tabs/*.java \
	com/sri/gui/core/layouts/*.java \
	com/sri/gui/core/tooltips/*.java

EXT_GUI_SRC     =    \
	com/sri/gui/ext/*.java \
	com/sri/gui/ext/backgrounds/*.java \
	com/sri/gui/ext/borders/*.java \
	com/sri/gui/ext/dialogs/*.java \
	com/sri/gui/ext/graph/*.java \
	com/sri/gui/ext/graph/graphable/*.java \
	com/sri/gui/ext/graph/renderer/*.java \
	com/sri/gui/ext/drawing/*.java \
	com/sri/gui/ext/drawing/selectors/*.java \
	com/sri/gui/ext/drawing/stencils/*.java

NETSIM_SRC = com/sri/apps/netsim/*.java

UTILS_SRC   =      \
	com/sri/utils/*.java    \
	com/sri/utils/adt/*.java    \
	com/sri/utils/net/*.java    \
	com/sri/utils/net/proxy/*.java  \
	com/sri/utils/net/proxy/*.java  \

GAMES_SRC   =   com/sri/games/*.java \
	com/sri/games/chess/*.java \
	com/sri/games/mine/*.java \
	com/sri/games/snake/*.java \
	com/sri/games/scrambler/*.java

MML_SRC     =   com/sri/apps/mml/*.java

TEST_SRC     =   com/sri/test/*.java

all: apps
	$(JCC) $(JFLAGS) $(SRCFILES)

apps: netsim mml games test

netsim: gui utils
	printf Building Network Simulator classes...
	$(JCC) $(JFLAGS) $(NETSIM_SRC)
	printf Done. \n

gui:
	printf Building Core GUI Classes...
	$(JCC) $(JFLAGS) $(CORE_GUI_SRC)
	printf Done. \n
	printf Building Core Extension Classes...
	$(JCC) $(JFLAGS) $(EXT_GUI_SRC)
	printf Done. \n

test:
	printf Building Test classes...
	$(JCC) $(JFLAGS) $(TEST_SRC)
	printf Done. \n

mml:
	printf Building MML classes...
	$(JCC) $(JFLAGS) $(MML_SRC)
	printf Done.\n

utils:
	printf Building utility classes...
	$(JCC) $(JFLAGS) $(UTILS_SRC)
	printf Done.\n

clean:
	echo Cleaning
	rm -R $(OUTDIR)/*

jar:
	(cd $(OUTDIR); jar -cvf sri.jar sri; chmod a+rx sri.jar)

docs:
	javadoc -d $(DOC_DIR) $(DFLAGS) -classpath $(CLASS_PATH) $(PACKAGES) $(SRCFILES)
	(cd $(DOC_DIR); chmod a+rx * -R)

lc:
	wc $(SRCFILES)

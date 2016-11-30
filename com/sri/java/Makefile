# ------------ Variables defined here ------------------

#ALL the directories are defined here
BASEDIR=/home/stude/s/spany/cs/java/compiler
#BASEDIR=/root/compiler

CLASSDIR=$(BASEDIR)/classes

SRC_DIR=$(BASEDIR)/src

# Arguments for Javac
JC=jikes
CLASS_PATH = $(CLASSPATH):$(CLASSDIR)
JFLAGS= -d $(CLASSDIR) -classpath $(CLASS_PATH)
DFLAGS = -noindex -public -author -version -package

# How to make .class files from .java files
%.class: %.java
	$(JC) $(JFLAGS) $<

EXPR_DIR=$(SRC_DIR)/expression
VALEXPR_DIR=$(EXPR_DIR)/valued
VOIDEXPR_DIR=$(EXPR_DIR)/void
TYPES_DIR=$(SRC_DIR)/types
ATTR_DIR=$(SRC_DIR)/attribute
BLOCK_DIR=$(SRC_DIR)/blocks
BASIC_DIR=$(SRC_DIR)/basic

SRC_FILES=\
	$(SRC_DIR)/*.java \
	$(EXPR_DIR)/*.java\
	$(VALEXPR_DIR)/*.java\
	$(VOIDEXPR_DIR)/*.java\
	$(TYPES_DIR)/*.java\
	$(ATTR_DIR)/*.java\
	$(BLOCK_DIR)/*.java\
	$(BASIC_DIR)/*.java

comp:
	echo "Compiling Decompiler..."
	#cd $(BASEDIR) ; mkdir classes
	$(JC) $(JFLAGS) $(SRC_FILES)

clean:
	rm -R $(CLASSDIR)/*

lc:
	wc $(SRC_FILES)

# All the dependencies.

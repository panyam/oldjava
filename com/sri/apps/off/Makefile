# ------------ Variables defined here ------------------

#ALL the directories are defined here


# Arguments for CC
EXE=proj
CC=g++
R_FLAGS=-Wall -O2 -O3 -fexceptions
D_FLAGS=-Wall -g -fexceptions 
CFLAGS=$(R_FLAGS)

proj: 
	jikes *.java

clean:
	rm -f *.class

deploy: proj
	jar -cvf 3d.jar *.class
	tar -cvf models.tar ktests;gzip models.tar

lc:
	wc *.java

tar:
	rm -f 380.tar.*
	tar -cvf 380.tar *.java Makefile ; gzip 380.tar

submit:
	echo Makefile $(SRC) $(HDR) README > .sub
	submit 380 proj.late < .sub

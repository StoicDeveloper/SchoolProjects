all: schedSim simRuns

schedSim: schedSim.c mlfq.c sjf.c debug.c debug.h
	clang schedSim.c mlfq.c sjf.c debug.c -g -pthread -Wall -Wpedantic -Werror -Wextra -o schedSim

simRuns: simRuns.c schedSim
	clang simRuns.c -g -lm -Wall -Wpedantic -Werror -Wextra -o simRuns

clean:
	rm -f schedSim simRuns

run2:
	./schedSim 2 sjf

run1:
	./schedSim 1 sjf

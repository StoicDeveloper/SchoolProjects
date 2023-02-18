//--------------------
// NAME: Xian Mardiros
// PURPOSE: The manual running of schedSim many times on different input would have been tedious
// 					so I wrote this to do it for me
//--------------------
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <math.h>
#define RUNS 5
#define TYPES 4
#define MLFQ "mlfq"
#define SJF "sjf"
#define MAX_CPUS 8
#define MAX_RESULT_LINES 100
#define NUMS_IN_RESULT_LINE 4
#define SIM_CMD "./schedSim"

// function declarations
void runAllSimulations(int results[MAX_RESULT_LINES][TYPES][NUMS_IN_RESULT_LINE]);
void runSimulationsScheds(int results[TYPES][4], int);
void runSimulationSeries(int results[TYPES][2], int, char*);
void runSimulation(int results[TYPES][2], int, char*);
int parseline(char*);
void printResults(int results[MAX_RESULT_LINES][TYPES][NUMS_IN_RESULT_LINE]);

// number of different numbers of cpus to run the simulations on
int cpu_tests;

// main
// run the simulations, print the results
int main()
{
	int results[MAX_RESULT_LINES][TYPES][NUMS_IN_RESULT_LINE];
	runAllSimulations(results);
	printResults(results);
	printf("End of Processing\n");
}

// run all the simulations
// for each number of cpus, doubling each time, run the simulations for that number of cpus
// and place the results in the results array
void runAllSimulations(int results[MAX_RESULT_LINES][TYPES][NUMS_IN_RESULT_LINE])
{
	int count = 0;
	int cpus = 1;
	while(cpus <= MAX_CPUS)
	{
		runSimulationsScheds(results[count], cpus);
		cpus *= 2;
		count++;
	}

	cpu_tests = count;
}

// run the simulations for a given number of cpus on each scheduler
// place the results into the results array
void runSimulationsScheds(int results[TYPES][4], int cpus)
{

	int mlfqResults[TYPES][2];
	int sjfResults[TYPES][2];

	runSimulationSeries(mlfqResults, cpus, MLFQ);
	runSimulationSeries(sjfResults, cpus, SJF);

	for(int i = 0; i < TYPES; i++)
	{
		results[i][0] = mlfqResults[i][0];
		results[i][1] = mlfqResults[i][1];
		results[i][2] = sjfResults[i][0];
		results[i][3] = sjfResults[i][1];
	}
}
	
// run RUNS simulations for the given number of cpus and the given scheduler
// place the results in resultAvgs 
void runSimulationSeries(int resultAvgs[TYPES][2], int cpus, char *sched)
{
	for(int i = 0; i < RUNS; i++)
	{
		int results[TYPES][2];
		runSimulation(results, cpus, sched);
		for(int j = 0; j < TYPES; j++)
		{
			resultAvgs[j][0] = 0;
			resultAvgs[j][1] = 0;
			resultAvgs[j][0] += results[j][0]/RUNS;
			resultAvgs[j][1] += results[j][1]/RUNS;
		}
	}
}

// run a single simulation, for the specified number of cpus and scheduler
// fork into schedSim with the required arguments, and parsing the minimal output
// records the simulation statistics for each type in results array
void runSimulation(int results[TYPES][2], int cpus, char *sched)
{
	int rc;
	int rc_wait;
	int err;
	int pipefd[2];
	pipe(pipefd);
	char str[5];
	sprintf(str, "%d", cpus);
	char *cmds[4] = {SIM_CMD, str, sched, "-m"};

	printf("forking to run %s on %s cpus with scheduler %s\n", cmds[0], cmds[1], cmds[2]);
	rc = fork();
	if(rc < 0)
	{
		printf("fork failed\n");
		exit(1);
	}else if(rc == 0)
	{
		close(pipefd[0]);
		dup2(pipefd[1], STDOUT_FILENO);
		err = execvp(cmds[0], cmds);
		printf("Exec failed with error %d\n", err);
		exit(err);
	}else
	{
		close(pipefd[1]);
		rc_wait = wait(NULL);
	}
	//printf("fork complete\n");

	FILE *file = fdopen(pipefd[0], "r");
	//printf("file opened\n");
	//fflush(NULL);
	char *line;
	size_t len = 0;
	int currType = 0;
	int count = 0;
	int isResponse = 0;
	while(count < 8 && getline(&line, &len, file) != -1)
	{
		//printf("\ngetting line %s", line);
		results[currType++][isResponse] = parseline(line);
		if(currType == TYPES)
		{
			currType = 0;
			isResponse = 1;
		}
		count++;
	}
	close(pipefd[0]);
}

// parse the line of sim output data
// only important is second number, which is either response or turnaround time
int parseline(char *line) 
{
	//printf("parsing line\n");
	//fflush(NULL);
	char *type;
	char *data;
	type = strtok(line, " \n");
	data = strtok(NULL, " \n");
	//printf("parsed line %s\n", data);
	//fflush(NULL);
	return atoi(data);
}

// make the table of simulation statistics
// the siulation stats are formatting such the vim-table-mode will automatically format it if pasted into a 
// text file while :TableModeToggle is on
void printResults(int results[MAX_RESULT_LINES][TYPES][NUMS_IN_RESULT_LINE])
{
	printf("| CPUs | Type | MLFQ Turnaround Avg | MLFQ Response Avg | SJF Turnaround Avg | SJF Response Avg |\n||\n");

	for(int i = 0; i < cpu_tests; i++)
	{
		for(int j = 0; j < TYPES; j++)
		{
			printf("| %d | %d | %d | %d | %d | %d |\n", (int)pow(2, i), j, results[i][j][0], results[i][j][1], results[i][j][2], results[i][j][3]);
		}
	}
}


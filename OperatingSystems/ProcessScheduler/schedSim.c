//-----------------------
// NAME: Xian Mardiros
// STUDENT NUMBER: 7862786
// COURSE: COMP3430
// INSTRUCTOR: Franklin Bristow
// ASSIGNMENT: 2
//
// REMARKS: overly complex attempt at exploring how conditions variabls and locking can be used
// 					to ensure tasks are safely executed in parallel. This was more work than it needed to 
// 					, by implementing different data structures for the policies, and for automating the 
// 					testing for the report. Still, it was worth it.

#include "debug.h"
#include <time.h>
#include "sjf.h"
#include "mlfq.h"
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <string.h>
#define NANOS_PER_USEC 1000
#define USEC_PER_SEC   1000000
#define POLICY_SJF "sjf"
#define POLICY_MLFQ "mlfq"
#define DEFAULT_WORKLOAD "tasks.txt"
#define SLICE 50
#define RESET_PERIOD 5000
#define MAX_PROB 101
#define RESET_INTERVAL 5000
#define NUM_TASK_TYPES 4
#define MAX_TASK_NAME_LEN 25


// task struct
typedef struct TASK
{
	char name[MAX_TASK_NAME_LEN];
	int type;
	int length;
	int prob_io;
	int progress; // the progress done last time this task was worked on
	int response;
	int turnaround;
} Task;

// function declarations
void parseTaskLine(Task **, char *);
void initCpus(pthread_t *cpus);
int initSched(Mlfq *, Sjf *, char *);
void runSim(char *);
void runSched(Mlfq *, Sjf *);
void runMlfq(Mlfq *);
void runSjf(Sjf *);
void waitForCpu(int);
void processNextTask(int *);
void checkWorkFinished();
static void microsleep(unsigned int);
int taskRuntime(Task *, int);
void* runCPU();
int micros(struct timespec);
void printStats(int);
void printTask(Task *);


// global scheduler state
int useMlfq;
int cpusWaiting = 0;
Task *schedBuffer = NULL;
int bufferIndex = 0;
int schedulerEmpty = 0;
Task **taskBuffer;
Task **completed;
int completedCount = 0;
int isNextTaskNeeded;
pthread_mutex_t schedLock = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cpuWaiting = PTHREAD_COND_INITIALIZER;
pthread_cond_t taskWaiting = PTHREAD_COND_INITIALIZER;
int workDone = 0;
int cpuNum;
struct timespec arrivalTime;

// whether to give output in parseable or human-readable mode
int minimal = 0;
char msg[50]; // for debugging

// functions

int main(int argc, char** argv)
{
	char *policy;
	char *filename;

	if(argc < 3 || argc > 6)
	{
		printf("Provide a CPU number and policy, plus an optional workload file name\n");
		exit(1);
	}else
	{
		cpuNum = atoi(argv[1]);
		policy = argv[2];

		// determine policy type
		if(!strcmp(policy, POLICY_MLFQ))
		{
			useMlfq = 1;
		}else if(!strcmp(policy, POLICY_SJF))
		{
			useMlfq = 0;
		}else
		{
			printf("Scheduler policy not supported\n");
			exit(1);
		}

		// determine file to parse and output format
		filename = DEFAULT_WORKLOAD;
		for(int i = 3; i < argc; i++)
		{
			if(!strcmp(argv[i], "-f"))
			{
				filename = argv[i+1];
			}else if(!strcmp(argv[i], "-m"))
			{
				minimal = 1;
			}
		}

		runSim(filename);
		if(!minimal)
		{
			printf("\nEnd of Processing\n");
		}

	}
}

//-----------------
// runSim
//
// Input:		- the name of the file to parse into tasks for the scheduler
// 					- the global cpuNum
// Output:	- the initialized schedulers, cpus
// 					- runs the simulation by starting the scheduler and cpus
// 					- an array of completed tasks
// 					- a printout of the completed tasks' data
// 					- destroys the schedulers
//-----------------
void runSim(char *filename)
{
	login("running simulation");

	Sjf *sjf = createSjf();
	Mlfq *mlfq = createMlfq();
	pthread_t cpus[cpuNum];
	initCpus(cpus);
	Task *buffer[cpuNum];
	taskBuffer = buffer;
	int taskNum;

	taskNum = initSched(mlfq, sjf, filename);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &arrivalTime);
	Task *arr[taskNum];
	completed = arr;
	runSched(mlfq, sjf);
	printStats(taskNum);

	destroy(sjf);
	destroy_mlfq(mlfq);

	logout("done simulation");
}

//-----------------
// initSched
//
// Input:		- both initialized schedulers and the filename of the task list
// 					- the global flag useMlfq
// Output:	- the correct scheduler as determined by useMlfq filled with tasks
// 					- a count of the number of tasks parsed
//-----------------

int initSched(Mlfq *mlfq, Sjf* sjf, char *filename)
{
	login("initializing scheduler");
	FILE *file = fopen(filename, "r");
	char *line;
	size_t len = 0;
	int count = 0;
	Task *task = NULL;
	while(getline(&line, &len, file) != -1)
	{
		parseTaskLine(&task, line);
		if(useMlfq)
		{
			enqueue(mlfq, task);
		}else
		{
			insert(sjf, task->length, task);
		}

		count++;
		//printf("%d\n", count);
	}
	logout("done initializing scheduler");
	return count;
}

//-----------------
// runSched
//
// Input:		- the initialized schedulers, one of which is filled with tasks
// 					- the global flag useMlfq
// Output:	- start the correct scheduler
//-----------------
void runSched(Mlfq *mlfq, Sjf* sjf)
{
	if(useMlfq)
	{
		runMlfq(mlfq);
	}else
	{
		runSjf(sjf);
	}
}

//-----------------
// initCpus
//
// Input:		- an array of uninitialized cpus
// 					- the global variable cpuNum
// Output:	- the cpus running on function runCPU, with no arguments or return values
//-----------------
void initCpus(pthread_t *cpus)
{
	login("initializing cpus");
	for(int i = 0; i < cpuNum; i++)
	{
		debugLog("CPU initialized");
		pthread_create(&cpus[i], NULL, runCPU, NULL);
	}
	logout("done initializing cpus");
}

//-----------------
// runCPU
//
// Input:		- the taskWaiting signal that indicates a task is waiting in schedBuffer
// 					- the tasks received from the scheduler, waiting in schedBuffer
// 					- the global flag workDone, which will cause the CPU to exit
// Output:	- the cpuWaiting signal that indicates a cpu is doing nothing
// 					- the global variable cpusWaiting that indicates the number of currently waiting cpus
// 					- bufferIndex to track the number of finished tasks waiting for processing
// 					- taskBuffer which contains all the finished tasks
// 					- schedBuffer, set to NULL once a task has been taken
// Remarks:	this one is more straight forward then the schedulers
//-----------------
void *runCPU()
{
	login("CPU: running");

	Task *task;
	int runtime;
	
	pthread_mutex_lock(&schedLock);
	cpusWaiting++;
	pthread_mutex_unlock(&schedLock);

	while(!workDone)
	{
		debugLog("CPU: iteration start");
		pthread_mutex_lock(&schedLock);
		debugLog("CPU: schedLock held");
		pthread_cond_signal(&cpuWaiting);

		while(schedBuffer == NULL && !workDone)
		{
			debugLog("CPU: waiting for task");
			pthread_cond_wait(&taskWaiting, &schedLock);
		}
		cpusWaiting--;
		if(workDone){
			// breaks are bad, but I don't see a way around this, with multiple threads there is no guarantee
			// that the state of workDone is the same here as at the start of the loop,
			break; 
		}

		// get next tast
		task = schedBuffer;
		schedBuffer = NULL;
		sprintf(msg, "CPU: task received %p\n", (void *)task);
		debugLog(msg);

		// finished waiting, don't need lock
		pthread_mutex_unlock(&schedLock);

		// do work
		runtime = taskRuntime(task, useMlfq);
		microsleep(runtime);
		task->length -= runtime;
		task->progress = runtime;

		// atomically report on work done
		pthread_mutex_lock(&schedLock);
		taskBuffer[bufferIndex++] = task;
		cpusWaiting++;
		pthread_mutex_unlock(&schedLock);
		debugLog("CPU: iteration end");
	}
	logout("CPU: done");
	return NULL;
}

//-----------------
// taskRuntime
//
// Input:		a task, and use slice, which is more descriptive, yet identical, to useMlfq
// Output:	the length of time the task should run for, based on the policy and the task's data
//-----------------
int taskRuntime(Task *task, int useSlice)
{
	login("calculating runtime");
	int io = rand() % MAX_PROB - task->prob_io;
	int ioTime;
	int runtime;
	if(io >= 0)
	{
		ioTime = rand() % (SLICE + 1);
		runtime = task->length > ioTime ? ioTime : task->length;
	}else
	{
		runtime = task->length > SLICE && useSlice ? SLICE : task->length;
	}
	logout("done calculating runtime");
	return runtime;
}



//-----------------
// parseTaskLine
//
// Input:		the taskptr where the task should be placed, and the line to parse
// Output:	an allocated and initialized task in taskptr
//-----------------
void parseTaskLine(Task **taskptr, char *line)
{
	sprintf(msg, "parsing task line: %s", line);
	debugLog(msg);

	Task *task = malloc(sizeof(Task));
	strcpy(task->name,strtok(line, " \n"));
	task->type = atoi(strtok(NULL, " \n"));
	task->length = atoi(strtok(NULL, " \n"));
	task->prob_io = atoi(strtok(NULL, " \n"));
	task->response = 0;
	*taskptr = task;
	//printTask(task);
}

//-----------------
// waitForCpu
//
// Input:		the global scheduler variables, which determine whether the scheduler needs to wait, 
// 					and the cpuWaiting trigger
// Output:	nothing
// Remarks:	needed to be careful to reset isNextTaskNeeded is reset every loop, otherwise the
// 					condition won't change even when the underlying variable do
//-----------------
void waitForCpu(int schedulerEmpty)
{
	int isTaskReturned = bufferIndex > 0;
	int isNextTaskNeeded = cpusWaiting && !schedulerEmpty && schedBuffer == NULL;
	while(!(isTaskReturned || isNextTaskNeeded))
	{
		debugLog("SCHED: waiting for cpu");
		pthread_cond_wait(&cpuWaiting, &schedLock);
		isTaskReturned = bufferIndex > 0;
		isNextTaskNeeded = cpusWaiting && !schedulerEmpty && schedBuffer == NULL;
	}
}


//-----------------
// runMlfq
//
// Input:		all scheduler global variables that determine this functions behaviour in each moment
// Output:	resets mlfq based on time, retrieves tasks for cpus, and handles tasks given back from
// 					cpus.
// Remarks:	also incredibly hard, but more instructive, as this scheduler being slower gave it more
// 					chance to reveal problems that only happened some of the time, unlike SJF which worked
// 					fine even when it wasn't entirely correct
//-----------------
void runMlfq(Mlfq *mlfq)
{
	login("running mlfq");
	Task *task;
	int requeued;
	int isNextTaskNeeded;
	int completedCount = 0;
	struct timespec resetTimerStart;
	struct timespec resetTimerCurr;
	struct timespec now;
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &resetTimerStart);
	while(!workDone)
	{
		debugLog("SCHED: iteration start");

		clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &resetTimerCurr);
		if(micros(resetTimerStart) - micros(resetTimerCurr) >= RESET_INTERVAL)
		{
			reset(mlfq);
			clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &resetTimerStart);
		}
			
		pthread_mutex_lock(&schedLock);

		waitForCpu(schedulerEmpty);

		isNextTaskNeeded = cpusWaiting && !schedulerEmpty && schedBuffer == NULL;
		if(isNextTaskNeeded)
		{
			// give tasks to the cpus
			schedBuffer = (Task *) dequeue(mlfq);
			processNextTask(&schedulerEmpty);
		}

		while(bufferIndex > 0)
		{
			debugLog("SCHED: receiving task from cpu");
			task = taskBuffer[--bufferIndex];
			requeued = requeue(mlfq, task, task->length, task->progress);
			if(!requeued)
			{
				// task was complete
				completed[completedCount++] = task;
				clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &now);
				task->turnaround = micros(now) - micros(arrivalTime);
				checkWorkFinished();
			}else
			{
				// task was not complete
				schedulerEmpty = 0;
			}
		}
		pthread_mutex_unlock(&schedLock);
		debugLog("SCHED: iteration end");
	}
	logout("done running mlfq");
}

//-----------------
// runSjf
//
// Input:		- the sjf to run
// 					- the global schedulerEmpty, schedBuffer, cpusWaiting, and bufferIndex variables, 
// 						which all determine what the sjf does at certain points
// Output:	- the schedBuffer may have a task written to it
// 					- global workDone will be set if all possible work has been completed
// Remarks:	this was extremely hard to write. Its necessary to be careful about what variable can 
// 					change when as a result of other threads, and precisely what conditions need to be met
// 					at each stage so that data is not lost
//-----------------
void runSjf(Sjf *sjf)
{
	login("running sjf");
	Task *task;
	struct timespec now; 
	while(!workDone)
	{
		debugLog("SCHED: iteration start");

		pthread_mutex_lock(&schedLock);

		waitForCpu(schedulerEmpty);

		isNextTaskNeeded = cpusWaiting && !schedulerEmpty && schedBuffer == NULL;
		if(isNextTaskNeeded)
		{
			// give tasks to the cpus
			schedBuffer = (Task *) next(sjf);
			processNextTask(&schedulerEmpty);
		}

		while(bufferIndex > 0)
		{
			debugLog("SCHED: receiving task from cpu");
			task = taskBuffer[--bufferIndex];
			if(task->length > 0)
			{
				// task not finished
				insert(sjf, task->length, task);
				schedulerEmpty = 0;
			}else
			{
				// task was finished
				completed[completedCount++] = task;
				clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &now);
				task->turnaround = micros(now) - micros(arrivalTime);
				checkWorkFinished();
			}
		}
		pthread_mutex_unlock(&schedLock);
		debugLog("SCHED: iteration complete");
	}
	logout("done running sjf");
}

//-----------------
//	checkWorkFinished
//
// Input:		- the global variable schedulerEmpty, schedBuffer, bufferIndex, and cpusWaiting
// 					- collectively, these represent all location that unfinished tasks could be at any time
// Output:	- if all conditions are met, deceptively broadcasts taskWaiting to wake up all cpus,
// 						and sets the workDone flag, which will cause CPUs to end execution.
//-----------------
void checkWorkFinished()
{
	if(schedulerEmpty && schedBuffer == NULL && bufferIndex == 0 && cpusWaiting == cpuNum)
	{
		debugLog("SCHED: work complete");
		// the scheduler is empty and all cpus are doing nothing, there are also no tasks
		// waiting for either a cpu or the scheduler 
		workDone = 1;
		pthread_cond_broadcast(&taskWaiting);
	}
}


//-----------------
// processNextTask
//
// Input:		the global scheduler buffer which may or may not contain a task to be given to a cpu
// Output:	-the schedulerEmpty flag set to the appropriate value
// 					-the taskWaiting signal to wake up cpus, if schedBuffer wasn't empty
// 					-if the task in schedBuffer hadn't been scheduled till now, set its response time
//-----------------
void processNextTask(int *schedulerEmpty)
{
	struct timespec now;
	if(schedBuffer != NULL)
	{
		debugLog("SCHED: next task retrieved");
		if(!schedBuffer->response)
		{
			clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &now);
			schedBuffer->response = micros(now) - micros(arrivalTime);
		}
		*schedulerEmpty = 0;
		pthread_cond_signal(&taskWaiting);
	}else
	{
		*schedulerEmpty = 1;
	}
}

//-----------------
// microsleep
//
// Input:		number of microseconds to sleep for
// Output:	nothing
// Remarks:	not my original code, use is allowed by assignment specification
//-----------------
static void microsleep(unsigned int usecs)
{
	login("sleeping");
	long seconds = usecs / USEC_PER_SEC;
	long nanos = (usecs % USEC_PER_SEC) * NANOS_PER_USEC;
	struct timespec t = { .tv_sec = seconds, .tv_nsec = nanos };
	int ret;
	do
	{
		ret = nanosleep( &t, &t );
 	}while(ret == -1 && (t.tv_sec || t.tv_nsec));
	logout("done sleeping");
}

//-----------------
// micros
//
// Input:		the time to convert to microseconds
// Output:	the time converted to microseconds
//-----------------
int micros(struct timespec time)
{
	return time.tv_sec * USEC_PER_SEC + time.tv_nsec / NANOS_PER_USEC;
}

//-----------------
// printStats
//
// Input:		the length of the complete tasks array
// Output:	the summary of the task data, in either minimal or normal form
//-----------------
void printStats(int len)
{
	int counters[NUM_TASK_TYPES];
	int turnaroundTotals[NUM_TASK_TYPES];
	int responseTotals[NUM_TASK_TYPES];
	int type;
	Task *curr;

	// initialize arrays
	for(int i = 0; i < NUM_TASK_TYPES; i++)
	{
		counters[i] = 0;
		turnaroundTotals[i] = 0;
		responseTotals[i] = 0;
	}

	// add the totals
	for(int j = 0; j < len; j++)
	{
		curr = completed[j];
		type = curr->type;
		counters[type]++;
		turnaroundTotals[type] += curr->turnaround;
		responseTotals[type] += curr->response;
	}

	// print data
	if(!minimal)
	{
		printf("Average turnaround time per type:\n\n");
	}
	for(int k = 0; k < NUM_TASK_TYPES; k++)
	{
		if(counters[k] == 0)
		{
			counters[k]++;
		}
		if(!minimal)
		{
			printf("\tType %d: %d usec\n", k, turnaroundTotals[k] / counters[k]);
		}else
		{
			printf("%d %d\n", k, turnaroundTotals[k] / counters[k]);
		}
	}

	if(!minimal)
	{
		printf("\nAverage response time per type:\n\n");
	}
	for(int m = 0; m < NUM_TASK_TYPES; m++)
	{
		if(counters[m] == 0)
		{
			counters[m]++;
		}
		if(!minimal)
		{
			printf("\tType %d: %d usec\n", m, responseTotals[m] / counters[m]);
		}else
		{
			printf("%d %d\n", m, responseTotals[m] / counters[m]);
		}
	}
}

//-----------------
//	printTask
//
// Input:		the task to print
// Output:	all info the task contains, and its location
//-----------------
void printTask(Task *task)
{
	if(task != NULL)
	{
		printf("TASK name %s, type %d, len %d, prob_io %d, progress %d, response %d, turnaround %d, ptr %p\n", task->name, task->type, task->length, task->prob_io, task->progress, task->response, task->turnaround, (void *)task);
	}else
	{
		printf("TASK NULL\n");
	}
}

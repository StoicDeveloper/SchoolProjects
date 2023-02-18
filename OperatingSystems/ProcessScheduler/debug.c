// Debug tools
// NAME: Xian Mardiros
// PURPOSE: Implementation of the debug tools

#include "debug.h"
#include <stdio.h>
#include <pthread.h>

// when multiple threads are using these functions, the logging can become a race condition
pthread_mutex_t debugLock = PTHREAD_MUTEX_INITIALIZER;
int debugNesting = 0;

void debugLog(char *string)
{
	if(DEBUG_FLAG)
	{
		for(int i = 0; i < debugNesting; i++)
		{
			printf("  ");
		}
		printf("%s", string);
		printf("\n");
		fflush(NULL);
	}
}

void login(char *string)
{
	if(DEBUG_FLAG)
	{
		pthread_mutex_lock(&debugLock);
		debugLog(string);
		debugNesting++;
		pthread_mutex_unlock(&debugLock);
	}
}

void logout(char *string)
{
	if(DEBUG_FLAG)
	{
		pthread_mutex_lock(&debugLock);
		debugNesting--;
		debugLog(string);
		pthread_mutex_unlock(&debugLock);
	}
}

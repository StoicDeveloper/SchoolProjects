// The bad shell
// 

#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

int debug = 1;

void exelinecmds(char *line);
int cmdtok(char *token);
int getcmd(char ***cmd, char *line, char **IOdirector);
int getinputfd(int *pipeinput, char **IOdirector);
int getoutputfd(int *pipefd, char **IOdirector);
void execcmd(char **cmd, int *inputfd, int *outputfd);

int main(int argc, char *argv[])
{
	if(argc != 2)
	{
		//printf("Provide exactly one shell script\n");
	}else
	{
		printf("Beginning processing\n");
		FILE *file = fopen(argv[1], "r");
		char *line;
		size_t len = 0;
		//int linenum = 0;
		
		while(getline(&line, &len, file) != -1)
		{
			//printf("\nline %d: %s", ++linenum, line);
			exelinecmds(line);
		}
		fclose(file);
	}
	printf("End of processing\n");
}

void exelinecmds(char *line)
{
	char **cmd;
	char *IOdirector;
	int pipefd[2] = {0, 1}; // these happen to be the default value for STDIN_FILENO, but here 0 is used as a flag
	int inputfd;
	int outputfd;

	while(getcmd(&cmd, line, &IOdirector))
	{
		line = NULL; // so that strtok won't start over at beginning of line
		inputfd = getinputfd(pipefd, &IOdirector);
		outputfd = getoutputfd(pipefd, &IOdirector);
		execcmd(cmd, &inputfd, &outputfd);
		free(cmd);
	}
}

int getcmd(char ***cmd, char *line, char **IOdirector)
{
	char *tok = strtok(line, " \n");
	printf("fetching command %s\n", tok);
	*cmd = malloc(sizeof(char *));
	size_t cmdlen = 0;
	if(tok)
	{
		while(cmdtok(tok))
		{
			printf("Placing token %s\n", tok);
			(*cmd)[cmdlen] = tok;
			tok = strtok(NULL, " \n");
			cmdlen++;
			*cmd = realloc(*cmd, (cmdlen + 1) * sizeof(char *));
		}
		(*cmd)[cmdlen] = NULL;
		*IOdirector = tok ? tok : "\0";
		//printf("Finished fetching command %s\n", (*cmd)[0]);
	}
	return cmdlen;
}

int cmdtok(char *token)
{
	return token && strcmp(token, "|") && strcmp(token, "<") && strcmp(token, ">");
}

int getinputfd(int *pipeinput, char **IOdirector)
{
	//printf("Getting input\n");
	int inputfd;
	char *inputfilename;
	// find correct input fd
	if(*pipeinput){
		// means previous command opened a pipe
		printf("pipe input\n");
		inputfd = *pipeinput;
		*pipeinput = 0;
	}else if(!strcmp("<", *IOdirector))
	{
		// redirect input from file, and get the output director
		inputfilename = strtok(NULL, " \n");
		printf("file input: %s\n", inputfilename);
		inputfd = open(inputfilename, O_RDONLY);
		*IOdirector = strtok(NULL, " \n");
	}else
	{
		printf("standard input\n");
		inputfd = STDIN_FILENO;
	}
	return inputfd;
}

int getoutputfd(int *pipefd, char **IOdirector)
{
	//printf("Getting output\n");
	int outputfd;
	char *outputfilename;
	// find correct output fd
	if(!strcmp(">", *IOdirector))
	{
		// redirect output from file
		outputfilename = strtok(NULL, " \n");
		printf("file output %s\n", outputfilename);
		outputfd = open(outputfilename, O_WRONLY | O_CREAT, S_IRWXU);
	}else if(!strcmp("|", *IOdirector))
	{
		// writing to pipe
		printf("pipe output\n");
		pipe(pipefd);
		outputfd = pipefd[1];
	}else
	{
		printf("standard output\n");
		outputfd = STDOUT_FILENO;
	}
	return outputfd;
}


void execcmd(char **cmd, int *inputfd, int *outputfd)
{
	//printf("executing command %s\n", cmd[0]);
	int rc;
	int rc_wait;
	// execute command
	rc = fork();
	printf("New process pid %d\n", rc);
	if(rc < 0)
	{
		printf("fork failed\n");
		exit(1);
	}else if(rc == 0)
	{
		printf("forking\n");

		if(debug)
		{
			sleep(20);
		}
		printf("changing input stream %d\n", *inputfd);
		dup2(*inputfd, STDIN_FILENO);
		printf("changing output stream %d\n", *outputfd);
		dup2(*outputfd, STDOUT_FILENO);
		printf("executing command\n");
		int err = execvp(cmd[0], cmd);
		printf("Exec failed with error %d\n", err);
		exit(err);
	}else
	{
		printf("waiting for fork resolution\n");
		rc_wait = wait(NULL);
		printf("fork finished\n");
		if(*inputfd != STDIN_FILENO)
		{
			close(*inputfd);
		}
		if(*outputfd != STDOUT_FILENO)
		{
			close(*outputfd);
		}	
	}
}


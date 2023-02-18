#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <assert.h>
#include <regex.h>

#define PORT 50002

typedef struct MEMO
{
	int id;
	char *name;
	char *note;
	int cookie;
} Memo;

void addMemo(Memo *);
int responseJsonContainsMatchingMemo(char *, Memo *);
char* getMemos(int);
void deleteMemo(Memo *);

int main(int argc, char *argv[])
{
	if( argc != 4 )
	{
		printf("Please provide cookie (int), memo name (quoted string), and note (quoted string) arguments.");
		exit(1);
	}

	Memo *memo = malloc(sizeof(Memo));
	memo->name = argv[2];
	memo->note = argv[3];
	memo->cookie = atoi(argv[1]);
	addMemo(memo);
	char *memosRes = getMemos(memo->cookie);
	assert(responseJsonContainsMatchingMemo(memosRes, memo));
	puts("assertion passed");
	free(memosRes);

	deleteMemo(memo);
	// this will fail if the list previously contained multiple matching memos 
	memosRes = getMemos(memo->cookie);
	assert(! responseJsonContainsMatchingMemo(memosRes, memo));
	puts("assertion passed");
	free(memosRes);
	free(memo);
}

int getConnectedSocket()
{
	int server = socket(AF_INET, SOCK_STREAM, 0);
	if(server == -1)
	{
		puts("bad socket");
	}
	struct sockaddr_in serveraddress;
	serveraddress.sin_addr.s_addr = inet_addr("127.0.0.1");
	serveraddress.sin_family = AF_INET;
	serveraddress.sin_port = htons(PORT);
	if(connect(server, (struct sockaddr *)&serveraddress, sizeof(serveraddress)) < 0)
	{
		printf("connection bad\n");
		return -1;
	}
	return server;
}

void addMemo(Memo *memo)
{
	puts("adding memo");
	int server = getConnectedSocket();
	char message[500];
	char *response = malloc(sizeof(char) * 2000);

	sprintf(message, "POST /api/memo HTTP/1.0\n\rCookie: session=%d\r\n\r\n{\"name\": \"%s\", \"note\": \"%s\"}", memo->cookie, memo->name, memo->note);
	if(send(server, message, strlen(message), 0) < 0)
	{
		printf("send bad");
	}
	recv(server, response, 2000, 0);

	const char brace[2] = "{";
	const char space[2] = " ";
	const char comma[2] = ",";
	char *token;
	token = strtok(response, brace);
	token = strtok(NULL, space);
	token = strtok(NULL, comma);
	int id = atoi(token);
	memo->id = id;
	free(response);


}



int responseJsonContainsMatchingMemo(char *response, Memo *memo)
{
	char re[200];
	regex_t regex;
	sprintf(re, "{[^}]*\"name\": \"%s\", \"note\": \"%s\", \"modified\": \"%d\"}", memo->name, memo->note, memo->cookie);

	int compiled = regcomp(&regex, re, 0);
	if(compiled)
	{
		puts("could not compile regex");
	}
	return ! regexec(&regex, response, 0, NULL, 0);

	return memo->cookie;
}

char* getMemos(int cookie)
{
	int server = getConnectedSocket();
	char message[500];
	char *response = malloc(sizeof(char) * 2000);

	sprintf(message, "GET /api/memo HTTP/1.0\n\rCookie: session=%d\r\n\r\n", cookie);
	if(send(server, message, strlen(message), 0) < 0)
	{
		printf("send bad");
	}
	recv(server, response, 2000, 0);
	return response;
}

void deleteMemo(Memo *memo)
{
	puts("deleting memo");
	int server = getConnectedSocket();
	char message[500];
	char *response = malloc(sizeof(char) * 2000);

	sprintf(message, "DELETE /api/memo/%d HTTP/1.0\n\rCookie: session=%d\r\n\r\n", memo->id, memo->cookie);
	if(send(server, message, strlen(message), 0) < 0)
	{
		printf("send bad");
	}
	recv(server, response, 2000, 0);
	// I don't understand why, but including this free() will break the program
	//free(response);
}


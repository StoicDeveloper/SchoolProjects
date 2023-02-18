// Debug header file
// NAME: Xian Mardiros
// PURPOSE: proveds interface for the use of my debugging tools
// 					it was difficult to visualize the output of my logging statements in the various
// 					nested functions, which the debug tools help with, especially in the multithreaded 
// 					environment of assignment 3

#ifndef DEBUG_HEADER
#define DEBUG_HEADER

// change to 0 to disable debug logging
#define DEBUG_FLAG 0

void debugLog(char *);
void login(char *);
void logout(char *);
#endif

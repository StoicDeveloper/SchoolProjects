// NAME: Xian Mardiros
// PURPOSE: interface for SJF policy
#ifndef SJF_HEADER
#define SJF_HEADER

typedef struct TREE Tree;
typedef struct NODE Node;
typedef struct SJF Sjf;

Sjf* createSjf();
void insert(Sjf *, int, void *);
void* next(Sjf *);
void destroy(Sjf *);

#endif

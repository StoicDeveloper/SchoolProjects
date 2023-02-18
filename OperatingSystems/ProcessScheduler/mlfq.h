// NAME: Xian Mardiros
// PURPOSE: Interface of the MLFQ

#ifndef MFLQ_HEADER
#define MFLQ_HEADER

typedef struct NODE Node;
typedef struct QUEUE Queue;
typedef struct MLFQ Mlfq;

Mlfq *createMlfq();
void enqueue(Mlfq *, void *);
void* dequeue(Mlfq *);
// requeue won't work, another task might dequeue before the previously dequeued task is finished, and ready to be requeued
// try implementing requeue queue, to list all of the dequeued tasks that might need requeuing of freeing in the future
int requeue(Mlfq *, void *, int length, int progress);
void reset(Mlfq *);
void destroy_mlfq(Mlfq *);
void printMlfq(Mlfq *);

#endif


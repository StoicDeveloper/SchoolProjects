//-----------------------
// NAME: Xian Mardiros
// PURPOSE: implementation of the MLFQ
// REMARKS: stores items in a series of queues, where only the first element of the highest-priority
// 					non-empty queue can be dequeued. Dequeued items may need to be requeued shortly after
// 					they are dequeued, and typically in the same order they were dequeued, and so these
// 					items' nodes are stored in an addition requeue queue, until they can be requeued or 
// 					their node can be destroyed, both being accomplished by the requeue function as appropriate
//-----------------------
#include "debug.h"
#include "mlfq.h"
#include <stddef.h>
#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
#define SLICE 50
#define ALLOTMENT 200
#define QUEUE_NUM 3
#define VALIDATE 0

char msg[50];

Mlfq *createMlfq();
void enqueue(Mlfq *, void *);
void* dequeue(Mlfq *);
int requeue(Mlfq *, void *, int length, int progress);
Node *delete_requeue(Queue *, void *);
void enqueue_queue(Queue *, Node *);
Node* dequeue_queue(Queue *);
void reset(Mlfq *);
void concat(Queue *, Queue *);
void destroy_mlfq(Mlfq *);
void print_queue(Queue *);
void validate_mlfq(Mlfq *);
void validate_queue(Queue *);
void validate_node(Node *);
void printMlfq(Mlfq *);

// MLFQ Structs
struct QUEUE
{
	Node *first;
	Node *last;
};

struct MLFQ
{
	int num;
	int allotment;
	Queue queues[QUEUE_NUM];
	Queue *requeue;
};

struct NODE
{
	int allotment_used;
	struct NODE *next;
	void *item;
	int dequeuedFrom; // only used for the requeue; requeue could just be a LL, but we already have the queue struct
};

// create MLFQ
// purpose: perform necessary allocations to create a functional mlfq struct
Mlfq *createMlfq()
{
	login("MLFQ: creating mlfq");
	Mlfq *mlfq = malloc(sizeof(Mlfq));
	mlfq->num = QUEUE_NUM;
	for(int i = 0; i < mlfq->num; i++)
	{
		mlfq->queues[i].first = NULL;
		mlfq->queues[i].last = NULL;
	}
	mlfq->requeue = malloc(sizeof(Queue));
	mlfq->requeue->first = NULL;
	mlfq->requeue->last = NULL;
	validate_mlfq(mlfq);
	logout("MLFQ: created mlfq");
	return mlfq;
}

// enqueue to MLFQ
// purpose: place item argument into last place of highest queue of mlfq argument
void enqueue(Mlfq *mlfq, void *item)
{
	login("MLFQ: enqueuing item");
	validate_mlfq(mlfq);
	assert(item != NULL);
	Node *node = malloc(sizeof(Node));
	node->allotment_used = 0;
	node->next = NULL;
	node->item = item;
	enqueue_queue(&mlfq->queues[0], node);
	validate_mlfq(mlfq);
	logout("MLFQ: enqueued item");
}

// dequeue from MLFQ
// purpose: return the item in the first place of highest non-empty queue in mlfq argument, and
// 					place that item's node into the mlfq's requeue
void* dequeue(Mlfq *mlfq)
{
	login("MLFQ: dequeuing");
	validate_mlfq(mlfq);
	//print_queue(mlfq->requeue);
	int curr = 0;
	Node *node = NULL;
	void *item = NULL;
	while(curr < mlfq->num && node == NULL)
	{
		node = dequeue_queue(&mlfq->queues[curr]);
		curr++;
	}

	if(node != NULL)
	{
		sprintf(msg, "requeue storing item %p\n", node->item);
		debugLog(msg);

		enqueue_queue(mlfq->requeue, node);
		node->dequeuedFrom = curr - 1;
		item = node->item;
	}

	//print_queue(mlfq->requeue);

	validate_mlfq(mlfq);
	logout("MLFQ: dequeued");
	return item;
}

// requeue to MLFQ
// purpose: dequeue the item from the requeue, and, if the length argument is positive, place it
// 					back into the appropriate queue, as depending on the progress argument
int requeue(Mlfq *mlfq, void *item, int length, int progress)
{
	login("MLFQ: requeuing");
	validate_mlfq(mlfq);
	assert(item != NULL);

	Node *node = delete_requeue(mlfq->requeue, item);
	int index;
	int requeued;

	assert(node != NULL);

	if(length > 0)
	{
		node->allotment_used += progress;
		if(node->allotment_used >= ALLOTMENT)
		{
			if(node->dequeuedFrom + 1 == mlfq->num)
			{
				index = node->dequeuedFrom;
			}else
			{
				index = node->dequeuedFrom + 1;
			}
			node->allotment_used = 0;
		}else
		{
			index = node->dequeuedFrom;
		}
		enqueue_queue(&mlfq->queues[index], node);
		node->dequeuedFrom = -1;
		requeued = 1;
	}else
	{
		free(node);
		requeued = 0;
	}
	//print_queue(mlfq->requeue);
	validate_mlfq(mlfq);
	logout("MLFQ: requeued");
	return requeued;
}

// delete from requeue
// purpose: remove and return the node containing the item argument from the requeue argument
// remarks: requeue could be any queue, but for now, this search functionality is only required
// 					for the requeue
Node *delete_requeue(Queue *requeue, void *item)
{
	login("MLFQ: deleting from requeue");
	validate_queue(requeue);
	assert(item != NULL);
	//print_queue(requeue);
	
	Node *curr = requeue->first;
	Node *target = NULL;

	sprintf(msg, "looking for item %p", item);
	debugLog(msg);

	if(curr)
	{
		if(curr->item == item)
		{
			debugLog("target is first item in requeue");
			target = dequeue_queue(requeue);
		}else
		{
			while(curr->next != NULL && !target)
			{
				if(curr->next->item == item)
				{
					target = curr->next;
					curr->next = curr->next->next;
					if(target == requeue->last)
					{
						debugLog("target found at end of list");
						requeue->last = curr;
					}else
					{
						debugLog("target found in middle of list");
					}
				}else
				{
					curr = curr->next;
				}
			}
		}
	}else
	{
		debugLog("requeue is empty, cannot search\n");
	}
	//print_queue(requeue);

	validate_queue(requeue);
	logout("MLFQ: deleted from requeue");
	return target;
}

// enqueue to queue
// purpose: place the node argument in the last place of the queue argument, pretty standard
void enqueue_queue(Queue *queue, Node *node)
{
	login("MLFQ: enqueuing to queue");
	validate_node(node);
	validate_queue(queue);
	debugLog("MLFQ: enqueuing to queue");

	node->next = NULL;
	if(queue->last)
	{
		queue->last->next = node;
	}else
	{
		queue->first = node;
	}
	queue->last = node;

	validate_node(node);
	validate_queue(queue);
	logout("MLFQ: enqueued to queue");
}

// dequeue from queue
// purpose: remove and return the first node from the queue argument
Node* dequeue_queue(Queue *queue)
{
	login("MLFQ: dequeuing from queue");
	validate_queue(queue);
	Node *node = NULL;
	if(queue->first)
	{
		node = queue->first;
		queue->first = queue->first->next;
		if(queue->first == NULL)
		{
			queue->last = NULL;
		}
	}
	validate_queue(queue);
	logout("MLFQ: dequeued from queue");
	return node;
}

// reset the mlfq
// purpose: concatenate all of the mlfq's queues (but not the requeue) to the highest queue
// 					so that in effect all items are moved to the highest queue
void reset(Mlfq *mlfq)
{
	login("MLFQ: resetting");

	for(int i = 0; i < mlfq->num; i++)
	{
		validate_mlfq(mlfq);
		concat(&mlfq->queues[0], &mlfq->queues[i]);
	}

	validate_mlfq(mlfq);
	logout("MLFQ: reset complete");
}

// concatenate two queues
// purpose: move the items in queue2 to the end of queue1
void concat(Queue *queue1, Queue *queue2)
{
	login("MLFQ: concatenating queues");
	validate_queue(queue1);
	validate_queue(queue2);

	if(queue2->first)
	{
		if(queue1->first)
		{
			queue1->last->next = queue2->first;
		}else
		{
			queue1->first = queue2->first;
		}
		queue1->last = queue2->last;
		queue2->first = NULL;
		queue2->last = NULL;
	}

	validate_queue(queue1);
	validate_queue(queue2);
	logout("MLFQ: concatenation complete");
}

// destroy the mlfq
// purpose: perform all necessary deallocations
void destroy_mlfq(Mlfq *mlfq)
{
	debugLog("MLFQ: destroying mlfq");
	free(mlfq->requeue);
	free(mlfq);
}

// print the queue
// purpose: detail the locations of the items of the queue
void print_queue(Queue *queue)
{
	login("printing queue");
	
	Node *curr = queue->first;
	int count = 1;

	if(curr == NULL)
	{
		sprintf(msg, "queue is empty");
		//debugLog(msg);
		printf("%s\n", msg);
	}else
	{
		while(curr)
		{
			sprintf(msg, "item %d %p,", count, curr->item);
			debugLog(msg);
		printf("%s\n", msg);
			curr = curr->next;
			count++;
		}
	}
	logout("print complete");
}

// ensure validity of the mlfq
void validate_mlfq(Mlfq *mlfq)
{
	if(VALIDATE)
	{
		assert(mlfq != NULL);
		for(int i = 0; i < mlfq->num; i++)
		{
			validate_queue(&(mlfq->queues[i]));
		}
		validate_queue(mlfq->requeue);
	}
}

// ensure validity of the queue
void validate_queue(Queue *queue)
{
	if(VALIDATE)
	{
		Node *curr;
		if(queue->first == NULL){
			assert(queue->last == NULL);
		}else
		{
			assert(queue->last != NULL);
			curr = queue->first;
			while(curr != NULL)
			{
				validate_node(curr);
				curr = curr->next;
			}
			assert(queue->last->next == NULL);
		}
	}
}

// ensure validity of the node
void validate_node(Node *node)
{
	if(VALIDATE)
	{
		assert(node != NULL);
		assert(node->item != NULL);
	}
}

// print the mlfq
// purpose: detail the contents of every queue (including requeue) in the mlfq
void printMlfq(Mlfq *mlfq)
{
	printf("MLFQ contents:\n");
	for(int i = 0; i < mlfq->num; i++)
	{
		printf("\tQueue %d:\n", i);
		print_queue(&(mlfq->queues[i]));
	}
	printf("\tRequeue:\n");
	print_queue(mlfq->requeue);
}

// CLASS: PriorityQueue
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Interface for PriorityQueue, which holds and orders events
#pragma once
#include "Queue.h"

class OrderedItem;
class Event;

class PriorityQueue{
private:
	Node *front;
public:
	PriorityQueue();
    bool isEmpty();
	ListItem *getFront();
	ListItem *dequeue();
    int checkFront();
	void insert( Event *item );
};

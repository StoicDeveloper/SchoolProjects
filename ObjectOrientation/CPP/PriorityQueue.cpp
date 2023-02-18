// CLASS: PriorityQueue
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: The data structure for holding and organizing events
#include "PriorityQueue.h"
#include "Node.h"
#include "Part.h"
#include <iostream>

using namespace std;

class Event;

PriorityQueue::PriorityQueue(){
	front = nullptr;
}

bool PriorityQueue::isEmpty(){return front == nullptr;}

ListItem *PriorityQueue::getFront(){return front->getItem();}

/*
 * dequeue
 * PURPOSE: take the item from the front
 * RETURNS: the front item
 */
ListItem *PriorityQueue::dequeue(){
        ListItem *theItem = nullptr;
        Node *theNode = front;
        if(front != nullptr){
                theItem = front->getItem();
                // special case: removing last item
                if(front->getNext() == nullptr){
                        front = nullptr;
                } else {
                        front = front->getNext();
                }
                delete(theNode);
        }
        return theItem;
}// dequeue

int PriorityQueue::checkFront(){
    return dynamic_cast<Event *>( front->getItem() )->getTime();
}

/*
 * insert
 * PURPOSE: add events to the list, mainting order by event time
 * PARAMETERS: the event to be added
 */
void PriorityQueue::insert( Event* event ){
	Node *curr = front;
	bool inserted = false;
	/* cases:
	 * case 1: empty list
	 * case 2: element goes at front
	 * case 3: element goes in middle
     * case 4: element goes at the back
	 */
    //cout << front->toString() << endl;
	if( curr == nullptr ){
        // case 1

        //cout << "null node";
		//empty list or goes at end
		front = new Node(event);

        //cout << curr->toString() << endl;
	}else if(  (dynamic_cast<Event *> (curr->getItem()))->compareTo( event ) >= 0 ){
        // case 2

        //cout << "front of the line" << endl;
		// element goes at front
		front = new Node( event, curr );
	}else{
        //cout << "find your place" << endl;
		while( curr != nullptr && !inserted ){
			// element goes at middle
			// should never reach end of list due to checking for
			// case 2
            if( curr->getNext() == nullptr ){
                // case 4
                curr->setNext( new Node( event ) );
                inserted = true;
            }else if( (dynamic_cast<Event *> (curr->getNext()->getItem()))->compareTo( event )
                    >= 0 || curr->getNext() == nullptr ){
                //case 3
				curr->setNext( new Node( event, curr->getNext() ) );
				inserted = true;
			}else{
				curr = curr->getNext();
			}
		}
	}
}


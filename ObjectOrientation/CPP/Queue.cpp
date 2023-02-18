// CLASS: Queue
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Implementation for data structure in which data need
//          only be added to the back and removed from the front
//          comments unchanged from those provided
#include "Queue.h"
#include "Node.h"
#include <string>

Queue::Queue() :front(nullptr), back(nullptr), size(0) {}
int Queue::getSize(){ return size; }
bool Queue::isEmpty(){ return size == 0; }
std::string Queue::toString(){ return front->toString(); }


// add item to front of queue.
void Queue::enqueue(ListItem *item){
        // special case: adding to empty queue
        if(front == nullptr){
                front = new Node(item, nullptr);
                back = front;
        } else {
                back->setNext(new Node(item, nullptr));
                back = back->getNext();
        }
        size++;
}// enqueue

// remove front item from Queue.
ListItem *Queue::dequeue(){
        ListItem *theItem = nullptr;
        Node *theNode = front;
        if(front != nullptr){
                theItem = front->getItem();
                // special case: removing last item
                if(front == back){
                        front = back = nullptr;
                } else {
                        front = front->getNext();
                }
                size--;
                delete(theNode);
        }
        return theItem;
}// dequeue


// look at first item in queue without removing.
ListItem *Queue::getFront(){
        ListItem *theItem = nullptr;
        if(front != nullptr){
                theItem = front->getItem();
        }
        return theItem;
}// getFront

// destructor
Queue::~Queue() {}

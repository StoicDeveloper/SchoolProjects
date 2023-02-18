// CLASS: Event
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Implementation of the superclass for the various events to be simulated
#include "Event.h"

Event::Event(int theTime, Simulation *theSim) :eventTime(theTime), sim (theSim) {}
Event::~Event() {}
int Event::getTime(){ return eventTime; }

int Event::compareTo(Event *other){
	return this->eventTime - other->eventTime;
}// compareTo

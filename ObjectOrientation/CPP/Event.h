// CLASS: Event
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for the Event superclass

#pragma once
#include "OrderedItem.h"
#include <string>

class Simulation;

/**** Event abstract interface */
class Event: public OrderedItem {
private:
        int eventTime; // time of the event.
protected:
        Simulation *sim; // simulation event is a part of.
public:
        Event(int theTime, Simulation* sim);
        virtual void processEvent() = 0; // polymorphic method for events.
        virtual ~Event();
        int getTime();
        int compareTo(Event *other); // you must implement this.
        virtual std::string toString() = 0;
};// class Event

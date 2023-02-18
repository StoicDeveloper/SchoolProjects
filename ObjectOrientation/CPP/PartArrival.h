// CLASS: PartArrival
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for PartArrival
#pragma once
#include "Event.h"
#include "Simulation.h"
#include <string>

class PartArrival: public Event {
private:
    int partNum;
public:
	PartArrival(int time, int num, Simulation *sim);
	void processEvent();
    std::string toString();
	~PartArrival();
};

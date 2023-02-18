// CLASS: End Assembly
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for event that handles end of assembly
#pragma once
#include "Event.h"
#include "Simulation.h"
#include <string>

class EndAssembly : public Event {
private:
	int first;
	bool atMain;
public:
	EndAssembly( int time, bool atMain, int first, Simulation *sim );
	void processEvent();
    std::string toString();
	~EndAssembly();
};

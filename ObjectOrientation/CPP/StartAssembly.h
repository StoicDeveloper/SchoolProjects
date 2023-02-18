// CLASS: StartAssembly
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: provide interface for StartAssembly
//-------------------------------
#pragma once
#include "Event.h"
#include <string>

class StartAssembly: public Event {
private:
	bool atMain;
public:
	bool isMain();
	StartAssembly( bool isMain, Simulation *sim );
	void processEvent();
    std::string toString();
	~StartAssembly();
};

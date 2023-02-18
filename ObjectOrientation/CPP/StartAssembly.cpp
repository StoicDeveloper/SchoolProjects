// CLASS: StartAssembly
//
// Author: Xian Mardiros, 7862786
//
// Remarks: Implements needed behaviour to start product assembly at one of the assembling stations

#include "StartAssembly.h"
#include "Simulation.h"
#include "EndAssembly.h"
#include <iostream>
#include <string>

// Which assembly station are we at?
bool StartAssembly::isMain(){ return atMain;};

// constructor
StartAssembly::StartAssembly( bool main, Simulation *sim ) : Event( sim->getSimulationTime(), sim ), atMain( main ) {}

/*
 * processEvent
 *
 * PURPOSE: dequeue part from the appropriate part or product
 * 					queues, schedule EndAssembly event at a time
 * 					equal to the current time plus the assembly
 * 					time of the current station, send the earlier
 * 					arrival time of the dequeued part with the
 * 					EndAssembly
 */
void StartAssembly::processEvent(){
	int minTime;
	Part* part1;
	Part* part2;
	int assemblyTime;

    cout << "Time: " << sim->getSimulationTime() << ", assembly starts at ";

	if( atMain ){
        cout << "main station\n";
		part1 = sim->nextPart(0);
		part2 = sim->nextPart(1);
		assemblyTime = sim->getMainTime();
        sim->setMainStatus(true);

	}else{
        cout << "finishing station\n";
		part1 = sim->nextPart(2);
		part2 = sim->nextProduct();
		assemblyTime = sim->getFinishingTime();
        sim->setFinishingStatus(true);
	}

    //cout << part2 << endl;

	minTime = part1->getArrivalTime();
	if( minTime > part2->getArrivalTime() ){
		minTime = part2->getArrivalTime();
	}

    int endTime = sim->getSimulationTime() + assemblyTime;
    //cout << "finish time " << endTime << endl;

	sim->addEvent( new EndAssembly( sim->getSimulationTime() + assemblyTime, atMain, minTime, sim ) );
}

std::string StartAssembly::toString(){
    return "StartAssembly";
}

StartAssembly::~StartAssembly(){}


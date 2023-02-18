// CLASS: PartArrival
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: implements needed behaviour for parts arriving
// 					at an assembly station
//
//-------------------------------------------------------
#include "PartArrival.h"
#include "Part.h"
#include <iostream>
#include <string>

PartArrival::PartArrival(int time, int num, Simulation *sim):Event(time, sim), partNum(num){}

/*
 * processEvent
 *
 * PURPOSE: adds a new part to the specified part queue,
 * 					then schedules an immediate StartAssembly event
 * 					if the appropriate station is not busy and the
 * 					other needed part is also waiting
 */
void PartArrival::processEvent(){
    cout << "Time: " << sim->getSimulationTime() << ", ";
	sim->queuePart(new Part(getTime()), partNum);

    if( partNum == 0 ){
        cout << "part 0 arrives at the main station\n";
        if( !sim->partQueueIsEmpty(1) && !sim->isMainBusy() ){
            sim->addEvent( new StartAssembly( true, sim ) );
        }
    }else if(partNum == 1){
        cout << "part 1 arrives at the main station\n";
        if( !sim->partQueueIsEmpty(0) && !sim->isMainBusy() ){
            sim->addEvent( new StartAssembly( true, sim ) );
        }
    }else{
        cout << "part 2 arrives at the finishing station\n";
        if( !sim->isFinishingBusy() && !sim->productQueueIsEmpty() ){
            sim->addEvent( new StartAssembly( false, sim ) );
        }
	}
}

std::string PartArrival::toString(){
    return "PartArrival ";
}

PartArrival::~PartArrival(){}

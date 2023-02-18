// CLASS: EndAssembly
//
// Author: Xian Maridors, 7862786
//
// REMARKS: implements end of assembly behvaiour
//
// -------------------------------------------
#include "EndAssembly.h"
#include <iostream>
#include <string>

// constructor
EndAssembly::EndAssembly( int time, bool atMain, int first, Simulation *sim ) : Event( time, sim ), first( first ), atMain( atMain ) {}

/*
 * processEvent
 *
 * PURPOSE: send a partially assembled product to the
 * 					finishing queue and start assembling a new
 * 					product if possible, or record statistics
 * 					for the finished product
 */
void EndAssembly::processEvent(){
    cout << "Time: " << sim->getSimulationTime() << ", ";
	if( atMain ){
        cout << "assembly finished at main assembly station\n";
        sim->setMainStatus(false);

        //cout << "part time: " << first << endl; //debugging

        Part *partProduct = new Part(first);
        ProductArrival *newArrival = new ProductArrival( partProduct, sim );

        //cout << partProduct << endl;
        //cout << newArrival->toString() << endl;

		sim->addEvent( newArrival );

		if( !sim->partQueueIsEmpty(0) && !sim->partQueueIsEmpty(1) && !sim->isMainBusy() ){
            // the part queues are not empty and main is not busy
			sim->addEvent( new StartAssembly( atMain, sim ) );
		}

	}else{
        cout << "assembly finished at finishing station\n";
        sim->setFinishingStatus(false);
		sim->incrementFinishedProducts();
		sim->addAssemblyTime( sim->getSimulationTime() - first );
	}
}

std::string EndAssembly::toString(){
    return "EndAssembly";
}

EndAssembly::~EndAssembly(){}

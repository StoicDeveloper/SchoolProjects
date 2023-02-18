
// CLASS: ProductArrival
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Implementation of behaviour needed for handling arrival of simulated products
#include "ProductArrival.h"
#include "Simulation.h"
#include <iostream>
#include <string>

ProductArrival::ProductArrival( Part *product, Simulation *sim ) : Event( sim->getSimulationTime(), sim ), product( product ) {}

/*
 * processEvent
 * PURPOSE: add product to the product queue and schedule StartAssembly if appropriate
 */
void ProductArrival::processEvent() {
    cout << "Time: " << sim->getSimulationTime() << ", product arrives at finishing station\n";
    sim->queueProduct( product );
	if ( !sim->partQueueIsEmpty(2) &&
			!sim->isFinishingBusy() ){
		sim->addEvent( new StartAssembly( false, sim ) );
		// assemble product
    }
}

std::string ProductArrival::toString(){
    return "ProductArrival" + product->toString();
}

ProductArrival::~ProductArrival(){
    delete(product);
}

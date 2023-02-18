// CLASS: Simulation
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Implements the main behaviour of the part
// 					assembly simulation
//
// --------------------------------------------------

#include "Simulation.h"
#include "PriorityQueue.h"
#include "PartArrival.h"
#include <iostream>


// constructor
Simulation::Simulation() : simulationTime(0), eventList( new PriorityQueue() ), mainBusy(false), finishingBusy(false), finishedProducts(0), totalAssemblyTime(0) {
	partQueues = new Queue*[3];
	for(int i=0; i<3; i++){
		partQueues[i] = new Queue();
	}
    productQueue = new Queue();
}

//
// Manipulation of queues
//

Part* Simulation::nextPart(int num){
    //cout << "Part " << num << " queue: " << partQueues[num]->toString() << endl;
	return dynamic_cast<Part *>( partQueues[num]->dequeue() );
}

Part* Simulation::nextProduct(){
    //cout << "Product queue: " << productQueue->toString() << endl;
	return (Part *) productQueue->dequeue();
}

void Simulation::queuePart(Part *part, int num){
	partQueues[num]->enqueue(part);
}

void Simulation::queueProduct(Part *product){
	productQueue->enqueue(product);
}

bool Simulation::partQueueIsEmpty(int num){
	return partQueues[num]->isEmpty();
}

bool Simulation::productQueueIsEmpty(){
	return productQueue->isEmpty();
}


// --------------------------------------
// runSimulation
//
// PURPOSE: main driver of simulation
//---------------------------------------
void Simulation::runSimulation(char *fileName){
	ifile.open(fileName);
	ifile >> mainAssemblyTime;
	ifile >> finishingAssemblyTime;
    //cout << mainAssemblyTime << finishingAssemblyTime << endl;
	int time;
    int loop = 0; // for debugging
	while( !ifile.eof() ){
        //cout << "Loop " << loop <<"\n";
		getNextArrival();
        loop++;
	}

	ifile.close();

	while( !eventList->isEmpty() ){
        if( simulationTime == eventList->checkFront() ){
            dynamic_cast<Event *>( eventList->dequeue() )->processEvent();
        }else{
            simulationTime++;
        }
    }
}

void Simulation::addEvent( Event *event ){
	eventList->insert( event );
}

// get info for next event from the input file
void Simulation::getNextArrival(){
	int time;
	int partNum;
	ifile >> time;
	ifile >> partNum;
    PartArrival *newArrival = new PartArrival( time, partNum, this );
    //cout << newArrival->toString() << endl;
	addEvent( newArrival );
}

//
// getters and setters for simulation variables
//
int Simulation::getSimulationTime(){return simulationTime;}

void Simulation::setSimulationTime(int time){
	simulationTime = time;
}

int Simulation::getMainTime(){return mainAssemblyTime;}
int Simulation::getFinishingTime(){return finishingAssemblyTime;}

bool Simulation::isMainBusy(){return mainBusy;}

bool Simulation::isFinishingBusy(){return finishingBusy;}

void Simulation::setMainStatus(bool status){
	mainBusy = status;
}

void Simulation::setFinishingStatus(bool status){
	finishingBusy = status;
}

void Simulation::incrementFinishedProducts(){
	finishedProducts++;
}

void Simulation::addAssemblyTime(int time){
	totalAssemblyTime += time;
}

void Simulation::printStats(){
    cout << "Number of items assembled: " << to_string(finishedProducts) << endl;
    cout << "Average assembly time: " << to_string(totalAssemblyTime/(double)finishedProducts) << endl;
    cout << "Parts remaining in queues:\nPart 0: " << partQueues[0]->getSize() << endl;
    cout << "Part 1: " << partQueues[1]->getSize() << "\nPart 2: " << partQueues[2]->getSize() << endl;
    cout << "Products: " << productQueue->getSize() << endl;

}


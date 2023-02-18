// CLASS: Simulation
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for the simulation
#pragma once
#include <fstream>
#include "StartAssembly.h"
#include "ProductArrival.h"
using namespace std;

class PriorityQueue; // Priority Queue
class Queue; // Queue class - provided to you
class Event; // Event - given to you.
class Part;

class Simulation {
private:
        ifstream ifile; // input file to read.
        int simulationTime; // what is the current time of the simulation?
        PriorityQueue *eventList; // priority queue of Events.
        Queue* productQueue; // queue of partially assembled products (for finishing station).
        Queue** partQueues; // *array* of queues of parts for the stations.
        int  mainAssemblyTime; //  how long does the main station take?
        int  finishingAssemblyTime; //  how long does the main station take?
	bool mainBusy; // is the main station busy?
	bool finishingBusy; // is the finishing station busy?
	int finishedProducts;
	int totalAssemblyTime;

public:
        Simulation();

	// you need methods to manipulate product and part queues.
				Part *nextPart(int num);
				Part *nextProduct();
				void queuePart(Part *part, int num);
				void queueProduct(Part *product);
				bool partQueueIsEmpty(int num);
				bool productQueueIsEmpty();

        int getSimulationTime();
        void setSimulationTime(int time);

        // main method for driving the simulation
        void runSimulation(char *fileName);

	// add an event to event queue.
	void addEvent (Event*);

	// read next arrival from file and add it to the event queue.
        void getNextArrival();
	// getters for station assembly times
	int getMainTime();
	int getFinishingTime();

	// getters and setters for station statuses.
	bool isMainBusy();
	bool isFinishingBusy();
	void setMainStatus(bool);
	void setFinishingStatus(bool);

	// simulation statistics
	void incrementFinishedProducts();
	void addAssemblyTime( int time );
    void printStats();

};// class Simulation

#define CATCH_CONFIG_MAIN
#include "catch.hpp"
#include "Event.h"
#include "PartArrival.h"
#include "PriorityQueue.h"

TEST_CASE( "(1) Test creation of PriorityQueue" ){
    PriorityQueue *list = new PriorityQueue();
    REQUIRE( list->isEmpty() );
}

TEST_CASE( "Test (2)insertion and (3) dequeue" ){
    Simulation *sim = new Simulation();
    PriorityQueue *list = new PriorityQueue();
    list->insert(new PartArrival( 50, 0, sim ));
    REQUIRE( list->checkFront() == 50 );
    //PartArrival *event = list->dequeue();
    //REQUIRE( event->getTime() == 50 );
    REQUIRE( dynamic_cast<Event *>(list->dequeue())->getTime() == 50 );
}

TEST_CASE( "Test (4) insertion ordering and (5) getFront" ){
    Simulation *sim = new Simulation();
    PriorityQueue *list = new PriorityQueue();
    PartArrival *event1 = new PartArrival( 50, 0, sim );
    PartArrival *event2 = new PartArrival( 40, 0, sim );
    PartArrival *event3 = new PartArrival( 30, 0, sim );

    list->insert( event2 );
    list->insert( event3 );
    list->insert( event1 );

    REQUIRE( list->checkFront() == 30 );
    REQUIRE( event3 == list->getFront() );
    list->dequeue();
    REQUIRE( event2 == list->getFront() );
    list->dequeue();
    REQUIRE( event1 == list->getFront() );
}

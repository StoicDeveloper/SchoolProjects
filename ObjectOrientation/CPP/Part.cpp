// CLASS: Part
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: implementation of the simulated parts for assembly
#include "Part.h"
#include <iostream>
#include <string>

using namespace std;

Part::Part( int time ) : arrivalTime( time ) {}
int Part::getArrivalTime(){
    //cout << "Part details: " << this << endl;
    //cout << arrivalTime;
    return arrivalTime; }
string Part::toString(){
    // there has got to be a better way
    string toReturn = "Part";
    toReturn += to_string(arrivalTime);
    toReturn += " ";
    return toReturn;}
Part::~Part() {};

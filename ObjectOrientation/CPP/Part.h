// CLASS: Part
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for Part
#pragma once
#include "ListItem.h"
#include <string>

class Part: public ListItem {
private:
	int arrivalTime;
public:
	Part( int time );
	int getArrivalTime();
    std::string toString();
	~Part();
};

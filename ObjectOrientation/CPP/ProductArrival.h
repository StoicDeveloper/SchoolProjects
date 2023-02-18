// CLASS: ProductArrival
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: interface for ProductArrival
#pragma once
#include "Event.h"
#include "Part.h"
#include <string>

class ProductArrival: public Event {
private:
	Part* product;
public:
	ProductArrival( Part *product, Simulation *sim);
	void processEvent();
    std::string toString();
	~ProductArrival();
};

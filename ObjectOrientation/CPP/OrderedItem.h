// CLASS: OrderedItem
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: unchanged OrderedItem interface
#pragma once
#include "ListItem.h"

class Event;

class OrderedItem : public ListItem {
public:
        virtual int compareTo(Event *other) = 0;
	virtual ~OrderedItem();
}; // class ListItem

// CLASS: ListItem
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: ListItem interface, only changed to add virtual toString

#pragma once
#include <string>

class ListItem {
public:
        virtual ~ListItem();
        virtual std::string toString() = 0;
}; // class ListItem

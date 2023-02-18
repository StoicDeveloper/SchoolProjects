// CLASS: Node
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Interface for Node
#pragma once
#include "Event.h" //I don't really understand why I need this here, but it fixes a bug
#include <string>

class ListItem;

/**** Node interface */
class Node {
private:
        ListItem *item;
        Node *next;
public:
        Node();
        Node(ListItem *i, Node *n);
        Node(ListItem *i);
        Node *getNext();
        void setNext(Node *next);
        ListItem *getItem();
        void setItem(ListItem *i);
        std::string toString();
}; // class Node

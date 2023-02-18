// CLASS: Node
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Node implementation for queues, toString added

#include "Node.h"
#include <iostream>

using namespace std;

Node::Node() :item(nullptr), next(nullptr)  {}
Node::Node(ListItem *i, Node *n) :item(i), next(n) {}
Node::Node(ListItem *i) :item(i), next(nullptr) {}
Node *Node::getNext() {return next;}
void Node::setNext(Node *n) {next=n;}
ListItem *Node::getItem() {return item;}
void Node::setItem(ListItem* li) { item = li; }

/*
 * toString
 * PURPOSE: Give info about this and downstream nodes
 * RETURNS: a string with the desired info
 */
std::string Node::toString(){
    std::string retString = "";
    /* produces warning, uncomment for debugging
    //cout << this << endl;
    if( this == nullptr ){
        retString += "null";
    }else{
        //cout << item << endl;
        retString += item->toString() + " ";
        if( next != nullptr ){
            retString += next->toString();
        }else{
            retString += "\n";
        }
    }
    */
    return retString;
}

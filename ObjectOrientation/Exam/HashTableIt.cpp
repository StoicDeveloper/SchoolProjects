// COMP2150 take home exam
// Question 1A
// Class: HashTableIt
// Author: Xian Mardiros, 7862786
#include "HashTableIt.h"
#include "HashTable.h"
#include "Node.h"

/*
 * constructor
 * sets currIndex to the first non-empty index
 * and currNode to the first node in the list at that index,
 * or -1 and nullptr if the list is empty
 */
HashTableIt::HashTableIt( HashTable *hashTable ) 
  : currNode(nullptr), currIndex(0), table(hashTable){
  if( this->table->table[this->currIndex] == nullptr ){
    this->currIndex = this->getNextIndex(0);
  }
  if( currIndex != -1 ){
    this->currNode = this->table->table[this->currIndex];
  }
};

/*
 * hasNext 
 * checks if there is a next item in the iterable
 */
bool HashTableIt::hasNext(){
	/* Very, very bad code
  bool hasNext = true;
  if( this->currNode == nullptr ){
    hasNext = false;
  } 
  return hasNext;
	*/
	return this->currNode != nullptr;
}

/*
 * next
 * returns the item of currNode if it isn't null, then sets currNode
 * to the next node at that index or if there isn't one then to 
 * the first node at the next non non-empty index
 */
Hashable* HashTableIt::next(){
  Hashable* item = nullptr;
  if( this->currNode != nullptr ){
    item = this->currNode->getData();
    if( this->currNode->getNext() != nullptr ){
      this->currNode = this->currNode->getNext();
    }else{
      this->currIndex = this->getNextIndex( this->currIndex );
      if( this->currIndex != -1 ){
        this->currNode = this->table->table[this->currIndex];
      }else{
        this->currNode = nullptr;
      }
    }
  }
  return item;
}

/*
 * getNextIndex
 * finds the next index after the parameter index that has nodes
 */
int HashTableIt::getNextIndex( int index ){
  /* fails to edge case: input of last 
   * valid index creates out of bounds error
  while( index != -1 && this->table->table[++index] == nullptr ){
    if( index == this->table->size ){
      index = -1;
    }
  }
  */
  bool foundNext = false;
  while( index != -1 && !foundNext ){
    if( ++index >= this->table->size ){
      index = -1;
    }else{
      foundNext = this->table->table[index] != nullptr;
    }
  }
  return index;
}

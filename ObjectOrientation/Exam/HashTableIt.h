// COMP2150 take home exam
// Question 1A
// Class: HashTableIt (header)
// Author: Xian Mardiros, 7862786
#pragma once

class Hashable;
class HashTable;
class Node;

class HashTableIt{
private:
  Node *currNode;
  int currIndex;
  HashTable *table;
  HashTableIt( HashTable* );
  int getNextIndex( int );
public:
  bool hasNext();
  Hashable* next();
};

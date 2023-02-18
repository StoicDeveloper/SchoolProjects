class Hashable;
class Node {
private:
  Hashable* data;
  Node* next;
public:
  Node();Node (Hashable *i, Node* next);
  Node* getNext();
  Hashable* getData();
};

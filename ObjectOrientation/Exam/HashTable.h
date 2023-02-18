class Node;
class Hashable;
class HashTableIt;
class HashTable {
friend class HashTableIt;
private:
Node** table;
int size;
public:
HashTable();
void add(Hashable*);
Hashable* get(Hashable*);
bool contains(Hashable*);
HashTableIt* iterator() const;
bool operator== (const HashTable&);
};

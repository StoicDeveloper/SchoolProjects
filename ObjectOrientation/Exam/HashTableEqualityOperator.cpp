// COMP2150 take home exam
// Question 1B
// Method: Overidden == operator 
// Author: Xian Mardiros, 7862786

bool HashTable::operator== ( const HashTable &table ) const {
  bool equals = true;
  HashTableIt *it = this->iterator();
  while( it->hasNext() && equals ){
    if( !table.contains( it->next() ) ){
      equals = false;
    }
  }
  return equals
}

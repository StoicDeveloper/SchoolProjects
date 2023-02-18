// CLASS: HashTable
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: It's a hash table
//
//-----------------------------------------
"use strict"

let LinkedList = require("./LinkedList.js")
let Hashable = require("./Hashable.js")

class HashTable{
	#_table;
	#_items;

	/*
	constructor
	create a new hash table, consisting of an array, and 
	add the remaining arguments to the table as elements
	*/
	constructor( size ){
		this.#_table = new Array(size)
		this.#_items = 0

		if( arguments.length > 1 ){
			for( let i=1; i<arguments.length; i++ ){
				this.add( arguments[i] )
			}
		}
	}

	/*
	add
	add the hashable item parameter to the hashTable array in the new or 
	existing LinkedList at array index determined by the hashVal of the item
	*/
	add( item ){
		assertHashable( item )
		var index = item.hashVal() % this.#_table.length

		if( this.#_table[index] == null ){
			this.#_table[index] = new LinkedList( item )
		}else{
			this.#_table[index].add( item )
		}
		this.#_items += 1
	}

	/*
	get
	return the matching hashable item in the hashTable, throw an error
	if a matching item isn't found
	3 cases:
	case 1: empty element
	case 2: matching LL
	case 3: not matching LL
	*/
	get( item ){
		assertHashable( item )
		var retItem = null
		var element = this.#_table[item.hashVal() % this.#_table.length]

		if( element instanceof LinkedList ){
			// takes care of cases 2 and 3
			retItem = element.get( item )
		} // case 1 taken care of by default

		if( retItem == null ){
			throw "Error: Cannot get item\n"
		}

		return retItem
	}

	/*
	remove
	remove the matching item from the hashTable
	destroy the LinkedList at the correct index if its now empty
	if no matching item is found, do nothing
	*/
	remove( item ){
		assertHashable( item )
		var index = item.hashVal() % this.#_table.length
		var element = this.#_table[index]

		if( element instanceof LinkedList ){
			if( element.remove( item ) ){
				this.#_items--
				if( element.isEmpty() ){
					this.#_table[index] = null
				}
			}
		}
	}

	/*
	contains
	check if the hashTable contains a matching item
	executed by calling "get" and catching the error if nothing is found
	*/
	contains( item ){
		assertHashable( item )
		var found = true

		try{
			this.get(item)
		}catch(error){
			found = false
		}
		return found
	}

	isEmpty(){
		return this.#_items == 0		
	}

	get size(){
		return this.#_items
	}

	/*
	toString
	return a string detailing the contents of the hashTable
	*/
	toString(){
		var string = "Hash table contents:\n--------------\n"
		var element

		for( let i=0; i<this.#_table.length; i++ ){
			element = this.#_table[i]

			if( element != null ){
				string += i + ": " + element.toString() + "\n"
			}
		}
		return string
	}
}

function assertHashable( item ){
	if( ! item instanceof Hashable) {
		throw "Error: Not Hashable object\n"
	}
}

module.exports = HashTable

// CLASS: Hashable
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: provide an inteface for objects that 
//					need to be placed into a hashTable
//
//-----------------------------------------
"use strict"

class Hashable{
	#_item

	/*
	constructor
	ensure a Hashable object can't be created, then set the item field
	*/
	constructor( item ){
		if( this.constructor === Hashable ){
			throw "Error: Cannot instantiate Hashable"
		}else{
			this.#_item = item
		}
	}

	hashVal(){
		throw "Error: hashVal not implemented in this class"
	}

	equals( item ){
		throw "Error: equals not implemented in this class"
	}

	get item(){
		return this.#_item
	}

	toString(){
		throw "Error: toString not implemented in this class"
	}
}

module.exports = Hashable

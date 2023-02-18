
// CLASS: Node
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: just a standard node implementation
//
//-----------------------------------------
"use strict"

class Node{
	#_item
	#_next

	constructor( item, next ){
		if( item == null || (next != null && ! next instanceof Node) ){
			throw "incorrect Node constructor format"
		}
		this.#_item = item
		this.#_next = next
	}

	get item(){
		return this.#_item
	}

	get next(){
		return this.#_next
	}

	set next( node ){
		if( node instanceof Node ){
			this.#_next = item
		}else{
			throw "Error: Node.next can only be set to an instance of Node\n"
		}
	}

	/*
	toString()
	recursively create a string of the contents of this node and all down-list nodes
	*/
	toString(){
		var string = this.#_item.toString()
		if( this.#_next != null ){
			string += ", " + this.#_next.toString()
		}
		return string
	}
}

module.exports = Node

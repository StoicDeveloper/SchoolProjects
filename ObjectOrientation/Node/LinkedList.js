
// CLASS: LinkedList
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: just a standard LL implementation
//
//-----------------------------------------
"use strict"

let Node = require("./Node.js")

class LinkedList{
	#_top

	constructor(){
		this.#_top = null
		var arg
		for( arg of arguments ){
			this.add( arg )
		}
	}

	add( item ){
		this.#_top = new Node( item, this.#_top )
	}

	/*
	get
	return the matching item that's closest to the front of the list, 
	or null if there is no matching item
	*/
	get( item ){
		assertHasEquals( item )
		var retItem = null
		var curr = this.#_top

		while( curr != null && retItem == null ){
			if( curr.item.equals( item ) ){
				retItem = curr.item
			}
			curr = curr.next
		}
		return retItem
	}

	/*
	remove
	find the matching item and remove it from the list, or 
	do nothing if there is no match
	*/
	remove( item ){
		assertHasEquals( item )
		var curr = this.#_top
		var removed = false

		if( curr == null ){
			throw "Error: list empty, it should not exist\n"
		}else if( curr.item.equals( item ) ){
			this.#_top = this.#_top.next
			removed = true
		}else{
			while( curr.next != null && !removed ){
				if( curr.next.item.equals( item ) ){
					curr.next = curr.next.next
					removed = true
				}
				this.#_top = this.#_top.next
			}
		}
		return removed
	}

	toString(){
		return "[" + this.#_top.toString() + "]"
	}

	isEmpty(){
		return this.#_top == null
	}
}

function assertHasEquals( item ){
	if( ! ("equals" in item) ){
		throw "can't compare items!"
	}
}

module.exports = LinkedList

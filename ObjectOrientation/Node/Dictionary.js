// CLASS: Dictionary
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Implement a dictionary ADT
//
//-----------------------------------------
"use strict"

let HashTable = require("./HashTable.js")
let KeyValueHash = require("./KeyValueHash.js")
let StringHash = require("./StringHash.js")
let IntHash = require("./IntHash.js")

class Dictionary{
	#_size
	#_table
	static TABLE_SIZE = 1000

	constructor(){
		this.#_size = 0
		this.#_table = new HashTable( Dictionary.TABLE_SIZE )
	}
	
	/*
	put
	enter a {key: value} pair into the dictionary
	uses a try-catch block to either replace the value of the 
	existing key with the parameter key, or enter a new dict 
	entry if the get method throws an error
	*/
	put( key, value ){
		key = getHashable(key)

		try{
			this.#_table.get( new KeyValueHash( key ) ).value = value
		}catch(error){
			this.#_table.add( new KeyValueHash( key, value ) )
		}
	}

	/*
	get
	return the value of the table entry with the matching key
	*/
	get( key ){
		key = getHashable(key)
		return this.#_table.get( new KeyValueHash( key ) ).value
	}

	/*
	contains
	check if the table contains an entry with the matching key
	*/
	contains( key ){
		key = getHashable(key)
		return this.#_table.contains( new KeyValueHash( key ) ) 
	}

	isEmpty(){
		return this.#_table.isEmpty()
	}

	print(){
		console.log( this.#_table.toString() )
	}
}

/*
get hashable
return a hashable object which has the parameter key as a field
*/
function getHashable( key ){
	let hashable

	if( typeof key == "string" ){
		hashable = new StringHash( key )
	}else if( typeof key == "number" ){
		hashable = new IntHash( key )
	}else{
		throw "can't make " + typeof key + " into hashable"
	}
	return hashable
}

module.exports = Dictionary

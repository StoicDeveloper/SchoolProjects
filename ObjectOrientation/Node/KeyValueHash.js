// CLASS: KeyValueHash
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: A hashable data type that implements a {key: value} pair
//
//-----------------------------------------
"use strict"

let Hashable = require("./Hashable.js")
class KeyValueHash extends Hashable{
	#_value
	
	/*
	constructor
	create a KeyValueHash with the parameters as fields, or with just a key and
	empty string value if the value parameter is omitted (this is used for searching
	in which case the object's value doesn't matter)
	*/
	constructor( key , value ){
		super(key)
		
		if( ! key instanceof Hashable ){
			throw "Error: wrong parameter type"
		}else if( value != undefined ){
			this.#_value = value
		}else{
			this.#_value = ""
		}
	}

  /*
	hashVal
	get the hasVal of they key
	*/
	hashVal(){
		//console.log( typeof this.item )
		if( ! this.item instanceof Hashable ){
			throw "Cannot hash " + typeof this.item
		}
		//console.log( this.toString() + typeof this.value)
		return this.item.hashVal()
	}
	
	equals( keyValueHash ){
		return this instanceof KeyValueHash && this.item.equals( keyValueHash.item )
	}

	get value(){
		return this.#_value
	}

	toString(){
		return this.item.toString() + ": " + this.#_value
	}
}

module.exports = KeyValueHash


// CLASS: StringHash
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: A datatype that allows a string to be placed into a hashTable
//
//-----------------------------------------
"use strict"

let Hashable = require("./Hashable.js")

class StringHash extends Hashable{
	static PRIME = 97
	
	constructor( string ){
		super(string)
		if( typeof string != 'string' ){
			throw "Error: wrong parameter type"
		}
	}

	/*
	hashVal
	generate the hash value from the string contents
	*/
	hashVal(){
		var string = this.item
		var value = 0
		var len = string.length
		for( var i=0; i<len; i++ ){
			value += string.charCodeAt(i)*(Math.pow( StringHash.PRIME, len-i-1 ) )
		}
		return value
	}
	
	equals( stringHash ){
		return this.item == stringHash.item
	}

	toString(){
		return this.item
	}
}

module.exports = StringHash

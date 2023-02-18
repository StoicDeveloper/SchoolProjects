
// CLASS: IntHash
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: implements the Hashable inteface so that integers can
// 					be stored in a HashTable
//
//-----------------------------------------
"use strict"

let Hashable = require("./Hashable.js")
class IntHash extends Hashable{
	
	constructor( int ){
		super(int)
		if( typeof int != 'number' ){
			throw "Error: wrong parameter type"
		}
	}

	hashVal(){
		return this.item
	}
	
	equals( hashable ){
		return this.item == hashable.item
	}

	toString(){
		return "" + this.item
	}
}

module.exports = IntHash

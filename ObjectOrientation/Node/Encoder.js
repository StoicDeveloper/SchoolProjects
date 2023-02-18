// CLASS: Encoder
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: implement lzw compression
//
//-----------------------------------------
"use strict"

let Dictionary = require("./Dictionary.js")
let fs = require('fs')

class Encoder{
	#_file
	#_compDict
	#_values
	static OUTPUT = "output.lzw"

	constructor( filename ){
		this.#_file = filename
		this.#_compDict = new Dictionary()
		
		for( this.#_values = 0; this.#_values<95; this.#_values++ ){
			let char = String.fromCharCode(this.#_values+32)
			this.#_compDict.put( char, this.#_values )
		}
	}

	encode(){
		let text = fs.readFileSync( this.#_file, "utf8" )
		let len = text.length
		fs.writeFileSync( Encoder.OUTPUT, "" )

		let i = 0
		let curr = text[i]
		while( i < len ){
			while( this.#_compDict.contains( curr ) ){
				var last = curr
				curr += text[i++]
			}
			this.#_compDict.put( curr, this.#_values++ )
			fs.appendFileSync( Encoder.OUTPUT, this.#_compDict.get( last ) + " " )
			curr = curr[ curr.length-1 ]
		}
		fs.appendFileSync( Encoder.OUTPUT, "-1" )
	}
}

module.exports = Encoder

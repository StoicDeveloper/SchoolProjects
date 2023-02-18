// FILE: encode
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: provide executable code to compress a text file
//
//-----------------------------------------
"use strict"
let Encoder = require("./Encoder.js")

function main(){
	var encoder = new Encoder( process.argv[2] )
	encoder.encode()
}

main()

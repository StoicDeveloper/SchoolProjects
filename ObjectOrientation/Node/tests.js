
// FILE: Tests
//
// Author: Xian Mardiros, 7862786
//
// REMARKS: Tests the HashTable class
//
"use strict"

let assert = require("assert")
let IntHash = require("./IntHash.js")
let StringHash = require("./StringHash.js")
let KeyValueHash = require("./KeyValueHash.js")
let HashTable = require("./HashTable.js")


function main(){
	var hashTables = testConstructor()
	testGet( hashTables )
	testContains( hashTables )
	testAdd( hashTables )
	testRemove( hashTables )
}

function testConstructor(){
	console.log("Testing constructor")
	var table1 = new HashTable(11)
	var table2 = new HashTable( 13, new IntHash(3) ) 
	var table3 = new HashTable( 17, new IntHash(6), new IntHash(6), 
		new StringHash("Hello world!"), 
		new KeyValueHash( new IntHash(6), "Value" ), new StringHash("data") )

	console.log( table1.toString() )
	console.log( table2.toString() )
	console.log( table3.toString() )

	try{
		assert( table1.isEmpty() && table2.size == 1 && table3.size == 5 )
		console.log( "Constructor test successful." )
	}catch(err){
		console.log( "Constructor test failed." )
	}
	return [table1, table2, table3]
}

function testGet( tables ){
	console.log("\nTesting \"get\" method")
	var [table1, table2, table3] = tables
	var got = false

	try{
		var string = table1.get( new StringHash( "Blue Green Red" ) ).item
		got = true
	}catch(err){}

	var int = table2.get( new IntHash(3) ).item
	var string2 = table3.get( new KeyValueHash( new IntHash(6), "Value4" ) ).value

	try{
		assert( !got && int == 3 && string2 == "Value" )
		console.log( "\"Get\" test successful." )
	}catch(err){
		console.log( "\"Get\" test failed." )
	}
}

function testContains( tables ){
	console.log("\nTesting \"contains\" method")
	var [table1, table2, table3] = tables

	try{
		assert( !table1.contains( new IntHash(5) ) )
		assert( table2.contains( new IntHash(3) ) )
		assert( table3.contains( new StringHash("Hello world!") ) )
		console.log( "\"Contains\" test successful." )
	}catch(err){
		console.log( "\"Contains\" test failed." )
	}
}

function testAdd( tables ){
	console.log("\nTesting \"add\" method")
	var [table1, table2, table3] = tables

	try{
		table1.add( new StringHash( "Blue Green Red" ) )
		table2.add( new IntHash(1000) )
		table3.add( new KeyValueHash( new StringHash("string"), "Value2" ) )

		console.log( table1.toString() )
		console.log( table2.toString() )
		console.log( table3.toString() )

		assert( table1.contains( new StringHash( "Blue Green Red" ) ) && table2.contains( new IntHash(1000) ) && table3.contains( new KeyValueHash( new StringHash("string"), "Value3" ) ) ) 
		console.log( "\"Add\" test successful." )
	}catch(err){
		console.log( "\"Add\" test failed." )
	}
}


function testRemove( tables ){
	console.log("\nTesting \"remove\" method")
	var [table1, table2, table3] = tables

	try{
		table1.remove( new StringHash( "Blue Green Red" ) )
		table1.remove( new IntHash(0) )
		table2.remove( new IntHash(1000) )
		table3.remove( new KeyValueHash( new StringHash("string"), "Value5" ) )
		var string1 = table1.toString()

		console.log( table1.toString() )
		console.log( table2.toString() )
		console.log( table3.toString() )

		assert( table1.isEmpty() )
		assert( string1 == table1.toString() )
		assert( table2.size == 1 && !table2.contains( new IntHash(1000) ) )
		assert( table3.size == 5 )
		assert( table3.contains( new KeyValueHash( new IntHash(6), "anything" ) ) )
		assert( table3.get( new KeyValueHash( new IntHash(6), 
			"whatever" ) ).value == "Value" )
		console.log( "\"Remove\" test successful." )
	}catch(err){
		console.log( "\"Remove\" test failed." )
	}
}


main()

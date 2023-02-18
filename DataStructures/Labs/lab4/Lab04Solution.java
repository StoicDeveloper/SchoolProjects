import java.io.*;
import java.util.*;

/***************************************************
 *
 * COMP 2140 Lab 4; Week of October 28, 2019
 *
 * Comparing a hash table with linear probing
 * to a hash table with separate chaining.
 *
 ***************************************************/
public class Lab04Solution {

    private static final int MINIMUM_TABLE_SIZE = 235;
    
    /******************** MAIN METHOD ***************************/

    public static void main( String[] args ) {

	System.out.println( "\n\nCOMP 2140 Lab 04 "
			    + "(Week of October 28, 2019)\n" );

	compareCollisionResolutionTechniques();
	
	System.out.println( "\nLab 04 program ends\n\n" );
    } // end main

    /******************************************************************
     *
     * compareCollisionResolutionTechniques
     *
     * Compare a table that uses linear probing for collision resolution
     * with a table that uses separate chaining for collision resolution.
     *
     * First, insert the same items (Strings read in from file Lab04.txt)
     * into the two tables.
     *
     * Then print out some statistics to compare the tables.
     *
     *****************************************************************/

    private static void compareCollisionResolutionTechniques() {
	// To read in the input file
	BufferedReader inFile;
	String inputLine;
	int primeTableSize = closePrime( MINIMUM_TABLE_SIZE );
	
	// A hash table that uses linear probing
	TableWithLP tlp = new TableWithLP( primeTableSize );

	// A hash table that uses separate chaining 
       	TableWithSC tsc = new TableWithSC( primeTableSize );

	// Insert all the keys in the file Lab04Input.txt into both tables,
	// searching for each one after it is inserted.
	try {
	    inFile = new BufferedReader( new FileReader( "Lab04Input.txt" ) );
	    inputLine = inFile.readLine();

	    while (inputLine != null) {

		System.out.println( "== Processing '" + inputLine + "'" );

		// Insert the key into the table with linear probing

		tlp.insert( inputLine );
		if ( !tlp.search( inputLine ) )
		    System.out.println( "Error in table with linear "
					+ "probing: key not inserted: '"
					+ inputLine + "'" );

                // Insert the key into the table with separate chaining

		tsc.insert( inputLine );
		if ( !tsc.search( inputLine ) )
		    System.out.println( "Error in table with separate "
		        + "chaining: key not inserted: '"
					+ inputLine + "'" );

		inputLine = inFile.readLine();
	    } // end while 
	} catch ( IOException e ) {
	    System.out.println( "Error reading file: Lab04Input.txt" );
	} // end try-catch

	System.out.println();
	tlp.printStatistics();

	System.out.println();
	tsc.printStatistics();
    } // end compareCollisionResolutionTechniques

    /******************************************************************
     *
     * closePrime
     *
     * Returns the smallest, odd prime number >= n.
     *
     * A number n is prime if its only 1 and itself.
     * It simply checks odd numbers >= n until it finds a prime number.
     *
     *****************************************************************/
    private static int closePrime( int n ) {
	int value = n;

	// Start with an odd number, because an even number isn't prime
	// (except for 2, which is too small to be a reasonable table size).
	if ( value % 2 == 0 ) {
	    value++;
	} // end if

	// Try odd numbers >= n until you find one that is a prime number
	while ( ! isPrime( value ) ) {
	    value += 2;
	} // end while
	
	return value;
    } // end closePrime

    /******************************************************************
     *
     * isPrime
     *
     * Returns true if the number is prime; otherwise, returns false.
     *
     * A number n is prime if its only 1 and itself.
     * If n has any factors other than 1 and itself, it must have
     * a factor between (and including) 2 and sqrt(n).
     *
     *****************************************************************/
    private static boolean isPrime( int n ) {
	boolean noFactorFound = n % 2 != 0; // Make sure 2 isn't a factor

	// Try all odd numbers <= sqrt( n ) as factors
	for ( int i = 3; noFactorFound && i * i <= n; i += 2 ) {
	    noFactorFound = n % i != 0; // i is not a factor if n % i is not 0.
	} // end for
	    
	return noFactorFound;
    } // end isPrime

} // end class Lab04Solution

//=====================================================================
// Class TableWithLP:
//
// A hash table that uses linear probing to resolve conflicts.
//=====================================================================
class TableWithLP {

    private static final int A = 13; // For the hash function, a small prime

    private String[] hashArray; // The hash table array.
    int numberItems; // The number of items currently stored in the hash table.

    /******************************************************************
     *
     * TableWithLP constructor
     *
     * Assumption: tableSize is a prime number
     *
     *****************************************************************/
    public TableWithLP( int tableSize ) {
	hashArray = new String[ tableSize ];
	for ( int i = 0; i < hashArray.length; i++ ) {
	    hashArray[ i ] = null;
	} // end for
	numberItems = 0;
    } // end TableWithLP constructor
    
    /******************************************************************
     *
     * hash
     *
     * Hashes key and returns the resulting array index.
     *
     * The hash function uses the polynomial hash code
     * implemented using Horner's method.
     *
     *****************************************************************/
    private int hash( String key ) {
	int hashIndex = 0;

	for ( int i = 0; i < key.length(); i++ ) {
	    hashIndex = (hashIndex * A ) % hashArray.length + (int) key.charAt(i);
	    hashIndex = hashIndex % hashArray.length;
	} // end for

	return hashIndex;
    } // end hash

    /******************************************************************
     *
     * insert
     *
     * Insert key into the table (except if the table is full or
     * if key is already in the table, in which case it prints
     * an error message).
     * 
     * Also, this method increments numberItems if the key is inserted.
     *
     *****************************************************************/
    public void insert( String key ) {
	int hashIndex;

	if ( numberItems != hashArray.length ) {
	    // There is space for a new item.

	    // First, hash the key to find the starting index.
	    hashIndex = hash( key );
	    
	    // Find an empty spot.
	    // Stop and abort if you find key --- no duplicates are allowed!
	    while ( hashArray[hashIndex] != null
		    && !hashArray[hashIndex].equals( key ) ) {
		hashIndex = (hashIndex+1) % hashArray.length;
	    } // end while
	    if ( hashArray[hashIndex] == null ) {
		hashArray[hashIndex] = key;
		numberItems++;
	    } else {
		// We found a duplicate of key already in the table.
		// No duplicates are allowed, so abort the insert.
		System.out.println( "***ERROR: Attempting to insert "
				    + " a duplicate of \"" + key
				    + "\" into a TableWithLP" ); 
	    } // end if-else
	} else {
	    // The table is full.  You cannot insert anything!
	    System.out.println( "***ERROR: Attempting to insert \""
				+ key + "\" into a full TableWithLP." );
	} // end if-else
	
    } // end insert

    /******************************************************************
     *
     * search
     *
     * Searches the table for key, and returns true if key is found,
     * false if it isn't.
     *
     *****************************************************************/
    public boolean search( String key ) {
	int hashIndex = hash( key );
	
	while ( hashArray[hashIndex] != null
		&& !hashArray[hashIndex].equals( key ) ) {
	    hashIndex = (hashIndex+1) % hashArray.length;
	} // end while
	
	return ( hashArray[hashIndex] != null );
    } // end search

    /******************************************************************
     *
     * printStatistics
     *
     * First, this method computes the total number of probes needed
     * to find every item currently in the table.
     *
     * Then, this method prints:
     *  (1) The size of the table,
     *  (2) The number of items in the table,
     *  (3) The total number of probes needed to find every item,
     *  (4) The average number of probes needed to find an item
     *      (total number of probes / number of items)
     *  (5) The load factor
     *      (number of items in the table / table size).
     *
     *****************************************************************/
    public void printStatistics() {
	int totalProbes = 0;
	int index;
	String key;
	int probes;

	// Compute the total number of probes needed to find every item
	for ( int i = 0; i < hashArray.length; i++ ) {
	    if ( hashArray[i] != null ) {
		// Count how many probes are needed to find key = hashArray[i]
		index = hash( hashArray[ i ] );
		probes = 1;
		while ( !hashArray[ index ].equals( hashArray[i] ) ) {
		    index = (index+1) % hashArray.length;
		    probes++;
		} // end while
		totalProbes += probes;
	    } // end if
	} // end for

	// Print out statistics
	System.out.println( "\nHash table with linear probing:" );
	System.out.println( "\nThe table size is " + hashArray.length + "." );
	System.out.println( "The table contains " + numberItems
			    + " items." );
	System.out.println( "The total number of probes required to "
			    + "search for every item: "
			    + totalProbes + " probes" );
	System.out.println( "The average number of probes required to "
			    + "search for an item: "
			    + ((float)totalProbes) / numberItems + " probes" );
	System.out.println( "The load factor: "
			    + ((float) numberItems) / hashArray.length );
    } // end printStatistics

    /******************************************************************
     *
     * toString
     *
     * For debugging purposes only.
     * This method returns a String giving information about the
     * non-empty entries of the array.
     *
     *****************************************************************/
    public String toString() {
	String output = "The non-empty array entries:\n";
	int hashIndex;

	for ( int i = 0; i < hashArray.length; i++ ) {
	    if ( hashArray[i] != null ) {
		hashIndex = hash( hashArray[i] );
		output += "   Index = " + i + "  hash( key ) = " + hashIndex
		    + "  key = " + hashArray[i] + "\n";
	    }
	} // end for

	return output;
    } // end toString
    
} // end class TableWithLP



//=====================================================================
// Class TableWithSC:
//
// A hash table that uses separate chaining to resolve conflicts.
//=====================================================================
class TableWithSC {

    //=====================================================
    // Class Node: An ordinary linked-list node
    //=====================================================
    private class Node {
	public String item;
	public Node next;

	public Node( String newItem, Node newNext ) {
	    item = newItem;
	    next = newNext;
	} // end Node constructor
	
    } // end class Node
    //=====================================================

    private static final int A = 13; // For the hash function

    private Node[] hashArray; // The array of linked lists.
    int numberItems; // The number of items currently stored in the table.

    /******************************************************************
     *
     * TableWithSC constructor
     *
     * Assumption: tableSize is a prime number
     *
     *****************************************************************/
    public TableWithSC( int tableSize ) {
	hashArray = new Node[ tableSize ];
	for ( int i = 0; i < hashArray.length; i++ ) {
	    hashArray[ i ] = null;
	} // end for
	numberItems = 0;
    } // end TableWithSC constructor
    
    /******************************************************************
     *
     * hash
     *
     * Hashes key and returns the resulting array index.
     *
     * The hash function uses the polynomial hash code
     * implemented using Horner's method.
     *
     *****************************************************************/
    private int hash( String key ) {
	int hashIndex = 0;

	for ( int i = 0; i < key.length(); i++ ) {
	    hashIndex = (hashIndex * A ) % hashArray.length + (int) key.charAt(i);
	    hashIndex = hashIndex % hashArray.length;
	} // end for

	return hashIndex;
    } // end hash

    /******************************************************************
     *
     * insert
     *
     * Inserts key into the table (except if key is already in the table,
     * in which case, this method prints an error message).
     * 
     * Also, this method increments numberItems if the key is inserted.
     *
     *****************************************************************/
    public void insert( String key ) {
	int hashIndex = hash( key );
	if ( !inLinkedList( key, hashArray[ hashIndex ] ) ) {
	    hashArray[ hashIndex ] = new Node( key, hashArray[ hashIndex ] );
	    numberItems++;
	} else { 
	    // We found a duplicate of key already in the table.
	    // No duplicates are allowed, so abort the insert.
	    System.out.println( "***ERROR: Attempting to insert "
				+ " a duplicate of \"" + key
				+ "\" into a TableWithSC" );
	} // end if-else
    } // end insert

    /******************************************************************
     *
     * search
     *
     * Searches the table for key, and returns true if key is found,
     * false if it isn't.
     *
     *****************************************************************/
    public boolean search( String key ) {
	int hashIndex = hash( key );
	return inLinkedList( key, hashArray[ hashIndex ] );
    } // end search

    /******************************************************************
     *
     * inLinkedList
     *
     * Searches the linked list pointed at by top for a node containing
     * key, and returns true if key is found and false if it isn't.
     *
     *****************************************************************/
    private boolean inLinkedList( String key, Node top ) {
	Node curr = top;
	boolean foundKey = false;

	while ( curr != null && !foundKey ) {
	    foundKey = curr.item.equals( key );
	    curr = curr.next;
	} // end while

	return foundKey;
    } // end inLinkedList

    /******************************************************************
     *
     * printStatistics
     *
     * First, this method computes the total number of probes needed
     * to find every item currently in the table.
     *
     * Then, this method prints:
     *  (1) The size of the table,
     *  (2) The number of items in the table,
     *  (3) The total number of probes needed to find every item,
     *  (4) The average number of probes needed to find an item
     *      (total number of probes / number of items)
     *  (5) The load factor
     *      (number of items in the table / table size).
     *
     *****************************************************************/
    public void printStatistics() {
	int totalProbes = 0; // Total # of probes to find every item in the whole table.
	int totalItems = 0; // Total # of items in the whole table.
	Node curr; // For moving through one linked list
	int i; // For computing the # of items within one linked list,
	       // and also the # of probes needed to find
	       // every item in that one linked list.

	// First, compute the total number of probes needed to find
	// every item currently in the table.
	for ( int j = 0; j < hashArray.length; j++ ) {
	    if ( hashArray[j] != null ) {
		curr = hashArray[j];
		i = 0;
		while ( curr != null ) {
		    i++; // The i-th item in the linked list (starting at 1)
		    totalProbes += i; // Would need to access i nodes
		                      // to find the item in this node.
		    curr = curr.next;
		} // end while
		totalItems += i; // add in the number of items in this list to the total
	    } // end if
	} // end for
	
	System.out.println( "\nHash table with separate chaining:" );
	System.out.println( "\nThe table size is " + hashArray.length + "." );
	System.out.println( "The table contains " + totalItems
			    + " items." );
	System.out.println( "The total number of probes required to "
			    + "search for every item: "
			    + totalProbes + " probes" );
	System.out.println( "The average number of probes required to "
			    + "search for an item: "
			    + ((float)totalProbes) / totalItems + " probes" );
	System.out.println( "The load factor: "
			    + ((float) totalItems) / hashArray.length );
    } // end printStatistics

    /******************************************************************
     *
     * toString
     *
     * For debugging purposes only.
     * This method returns a String giving information about the
     * non-empty entries of the array.
     *
     *****************************************************************/
    public String toString() {
	String output = "The non-empty array entries:";
	int hashIndex;
	Node curr;

	for ( int i = 0; i < hashArray.length; i++ ) {
	    if ( hashArray[i] != null ) {
		hashIndex = hash( hashArray[i].item );
		output += "\n   Keys at index = " + i + ": ";
		curr = hashArray[i];
		while ( curr != null ) {
		    output += "\n      " + curr.item;
		    curr = curr.next;
		} // end while
	    } // end if
	} // end for

	return output;
    } // end toString

} // end class TableWithSC

import java.io.*;
import java.util.*;

/***************************************************
 *
 * COMP 2140 Lab 2; Week of September 30, 2019
 *
 * Simple linked list:
 *
 * Add up the value stored in the linked list both
 * recursively and non-recursively (iteratively).
 * Compare the running times of the two algorithms.
 *
 ***************************************************/

public class Lab02 {

    private static final int LIST_SIZE = 1000;

    /********* MAIN METHOD ***********/

    public static void main( String[] args ) {

	System.out.println( "\n\nCOMP 2140 Lab 02 (Week of September 30, 2019)\n" );

	testListSumming();

	System.out.println( "\nLab 02 program ends\n\n" );
    } // end main

    /******************************************
     *
     * testListSumming:
     *
     * Creates a list of size LIST_SIZE and then
     * runs the recursive and non-recursive summing
     * methods to compare their running times.
     *
     ******************************************/

    private static void testListSumming() {
	LinkedList list;
	long start, stop, elapsed;
	int sum;

	// Create a new test list.
	list = createTestList( LIST_SIZE );

	// Print the list out
	System.out.println( "The list:" );
	list.printList();

	// Sum the numbers iteratively
	start = System.nanoTime();
	sum = list.sumIterative( );
	stop = System.nanoTime();
	elapsed = stop - start;
	System.out.println("\nTime for the iterative sum method: "+ elapsed
			   + " nanoseconds.");
	System.out.println( "The iterative sum method computed the sum to be " + sum );

	// Sum the numbers recursively
	start = System.nanoTime();
	sum = list.sumRecursive( );
	stop = System.nanoTime();
	elapsed = stop - start;
	System.out.println("\nTime for the recursive sum method: "+ elapsed
			   + " nanoseconds.");
	System.out.println( "The recursive sum method computed the sum to be " + sum );

    } // end testListSumming


    /******************************************
     *
     * createTestList
     *
     * Return a new list filled with listSize
     * random numbers.  Each random number is
     * between 0 and listSize * 100.
     *
     ******************************************/
    public static LinkedList createTestList( int listSize ) {
	LinkedList list = new LinkedList();
	Random generator = new Random(System.nanoTime());

	for ( int i = 0; i < listSize; i++ ) {
	    list.insertAtFront( generator.nextInt( listSize * 100 ) );
	} // end for

	return list;
    } // end createTestList


} // end class Lab02SolutionFall2019

//=====================================================
// LinkedList class
//
// An ordinary linked list containing ints.
// The list consists of just a pointer (top)
// to the first node in the list.
//=====================================================
class LinkedList {

    //================================================
    // Node class
    //
    // A node in the linked list.
    // Note that it is inside the LinkedList class.
    //
    // The usual accessors and mutators (getters and
    // setters) are provided, though these methods
    // are unnecessary. You can directly access
    // the instance members "item" and "next" in any
    // node that you have a pointer to without using
    // the accessors or mutators anywhere in the
    // LinkedList class.  Why? Because item and next
    // are declared public in the private Node class
    // inside the LinkedList class.
    //================================================
    private class Node {
        public int item;
        public Node next;

        // Node constructor
        public Node( int newItem, Node newNext ) {
            item = newItem;
            next = newNext;
        }

        // The usual accessors and mutators
        // (in case you really want to use them).

        public int getItem() { return item; }

        public Node getNext() { return next; }

        public void setNext( Node newNext ) { next = newNext; }

    } // end class Node

    /********* Returning to class LinkedList ************/

    private Node top; // A pointer to the first node in
                      // the list (when it's not empty)

    /******************************************
     *
     * LinkedList constructor
     *
     * Creates an empty LinkedList.
     *
     ******************************************/
    public LinkedList( ) {
	top = null;
    }

    /******************************************
     *
     * insertAtFront
     *
     * It is passed a new value to add to the list.
     *
     * It inserts a new node containing newItem
     * at the front of the list.
     *
     * It does NOT check for duplicates ---
     * duplicate values are allowed.
     *
     ******************************************/
    public void insertAtFront( int newItem ) {
	top = new Node( newItem, top );
    }

    /******************************************
     *
     * printList
     *
     * Print out the list (10 numbers per line).
     *
     ******************************************/
    public void printList( ) {
	int count = 1;
	Node curr;

	if ( top == null ) {
	    System.out.println( "List is empty" );
	} else {
	    curr = top;
	    while ( curr != null ) {
		System.out.print( " " + curr.item );
		count++;
		if ( count % 10 == 0 ) {
		    System.out.println();
		}
		curr = curr.next;
	    } // end while
	    if ( count % 10 != 0 )
		System.out.println();
	} // end if-else
    } // end printList

    /******************************************
     *
     * sumRecursive
     *
     * This is the public driver method for the
     * algorithm that sums the list recursively.
     *
     * It returns 0 if the list is empty.
     * Otherwise, it calls the recursive helper
     * method that actually does the summing and
     * returns whatever the helper method returns.
     *
     ******************************************/
    public int sumRecursive( ) {
	int sum = 0;

	if ( top != null ) {
	    sum = sumRecursiveHelper( top );
	}

	return sum;
    } // end sumRecursive

    /******************************************
     *
     * sumRecursiveHelper
     *
     * Parameter curr points to a node in the
     * LinkedList that is pointed at by
     * implicit parameter this.
     *
     * Guarantee: curr is NOT null.
     *
     * This recursive method returns the sum of
     * the numbers stored in the nodes starting
     * at curr to the end of the list.
     *
     * Base case: There are no nodes after curr.
     *    In this case, it simply returns the
     *    value stored in curr.
     *
     ******************************************/
    private int sumRecursiveHelper( Node curr ) {
        int sum = 0;
        if(curr.getNext() == null){
            sum = curr.getItem();
        }else{
            sum = curr.getItem() + sumRecursiveHelper( curr.getNext() );
        }
	    return sum;

    } // end sumRecursive

    /******************************************
     *
     * sumIterative
     *
     * Returns the sum of the numbers stored in
     * the LinkedList pointed at by implicit
     * parameter "this".
     *
     * This method uses a simple while-loop
     * to add up all the numbers.
     *
     ******************************************/
    public int sumIterative( ) {

	    int sum = 0;
        Node curr = top;
        while(curr != null){
            sum += curr.getItem();
            curr = curr.getNext();
        }
        return sum;

    } // end sumIterative

} // end class LinkedList

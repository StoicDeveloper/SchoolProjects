import java.io.*;
import java.util.*;

/***************************************************
 *
 * COMP 2140 Lab 3; Week of October 14, 2019
 *
 * Recursive delete in a linked list with
 * dummy nodes.
 *
 ***************************************************/

public class Lab03 {

    private static final int LIST_SIZE = 1000;

    /********* MAIN METHOD ***********/

    public static void main( String[] args ) {

	System.out.println( "\n\nCOMP 2140 Lab 03 (Week of October 14, 2019)\n" );

	testLists();

	System.out.println( "\nLab 03 program ends\n\n" );
    } // end main

    /******************************************
     *
     * testLists:
     *
     * Generates an array of random data, then
     * inserts the values into a linked list
     * with dummy nodes.
     * Then it deletes the values in the array
     * from the linked list, checking to see
     * that they have been deleted.
     *
     ******************************************/

    private static void testLists() {
	int[] testData = createTestList( LIST_SIZE );
	LinkedList list = new LinkedList();
	boolean deletionError = false;
	boolean sortedOrderError = false;

	// Test that the isEmpty method recognizes an empty list.
	if ( list.isEmpty() ) {
	    System.out.println( "\nYour isEmpty() method correctly "
				+ "recognized that the list is empty." );
	} else {
	    System.out.println( "\n*** ERROR *** Your isEmpty() method did not"
				+ "recognize that the list is empty." );
	}

	// Insert the random data in the array into the list
	for ( int i = 0; i < LIST_SIZE; i++ ) {
	    list.insertOrdered( testData[i] );
        } // end for

	// Print the list out
	System.out.println( "\nThe list after insertions:" );
	list.printList();

	// Test that the isEmpty method recognizes a non-empty list.
	if ( !list.isEmpty() ) {
	    System.out.println( "\nYour isEmpty() method correctly "
				+ "recognized that the list is NOT empty." );
	} else {
	    System.out.println( "\n*** ERROR *** Your isEmpty() method did not"
				+ "recognize that the list is NOT empty." );
	}

	// Recursively delete each data value from
	// the linked list with dummy nodes,
	// checking that each delete worked:
	// (a) Do a search after each delete to
	//     verify that the value has been
	//     deleted,
	// (b) Verify that the list is still sorted,
	//     and
	// (c) Verify that the list still contains
	//     the other values --- only the
	//     requested value should be deleted!
	for ( int i = 0; i < LIST_SIZE; i++ ) {
	    list.deleteOrdered( testData[i] );
	    if ( list.contains( testData[i] ) ) {
		deletionError = true;
		System.out.println( "***ERROR*** " + testData[i]
				    + " was deleted from the list,"
				    + " but it is still IN the list!" );
	    } // end if
	    if ( !noOtherValuesDeleted( list, testData, i ) ) {
		deletionError = true;
		System.out.println( "***ERROR*** When" + testData[i]
				    + " was deleted from the list,"
				    + " other values were also deleted!"
				    + "\n            You deleted nodes"
				    + " that you shouldn't have deleted." );
	    } // end if
	    if ( !list.isSorted() ) {
		sortedOrderError = true;
		System.out.println( "***ERROR*** When " + testData[i]
				    + " was deleted from the list,"
				    + " the sorted ordering in the list was messed up!" );
	    } // end if
	} // end for

	if ( !deletionError ) {
	    System.out.println( "\nAll " + LIST_SIZE
				+ " items were correctly deleted." );
	} // end if
	if ( !sortedOrderError ) {
	    System.out.println( "\nThe list was still sorted after"
				+ " every deletion." );
	} // end if

    } // end testLists


    /******************************************
     *
     * createTestList
     *
     * Return an array filled with listSize
     * random numbers.  Each random number is
     * between 0 and listSize * 100.
     *
     ******************************************/
    private static int[] createTestList( int listSize ) {
	int[] numberArray = new int[ listSize ];
	Random generator = new Random(System.nanoTime());

	for ( int i = 0; i < listSize; i++ ) {
	    numberArray[i] =  generator.nextInt( listSize * 100 );
	} // end for

	return numberArray;
    } // end createTestList

    /******************************************
     *
     * noOtherValuesDeleted
     *
     * We just deleted testData[i] from list.
     *
     * Return true if the values that haven't been
     * deleted yet are still in list.
     * Return false if some value that we have NOT
     * deleted yet is no longer in the list.
     *
     * Values that haven't been deleted yet are
     * the values in positions i+1 to the end of
     * array testData that do NOT also appear
     * anywhere in positions 0 to i of array testData.
     * (Because the data is randomly generated,
     * a value in position i+1 to the end of the array
     * could be a duplicate of a value in position
     * 0 to i, and so it could already be deleted.)
     *
     ******************************************/
    private static boolean noOtherValuesDeleted( LinkedList list,
					       int[] testData,
					       int i ) {
	boolean otherValuesStillThere = true;
	for ( int j = i+1; j< testData.length && otherValuesStillThere; j++ ) {
	    if ( !alreadyDeletedValue( testData, i, testData[j] ) ) {
		otherValuesStillThere = list.contains( testData[j] );
	    } // end if
	} // end for
	return otherValuesStillThere;
    } // end noOtherValuesStillThere

    /******************************************
     *
     * alreadyDeletedValue
     *
     * We have already deleted from a LinkedList
     * the values in positions 0 to i of array
     * testData.
     *
     * We want to know if key is one of those
     * deleted values or not.
     *
     * Return true if key is one of the values
     * in positions 0 to i of array testData,
     * that is, if key has already been deleted.
     * Return false if key is NOT one of the
     * values in positions 0 to i of testData.
     *
     ******************************************/
    private static boolean alreadyDeletedValue( int[]testData,
						int i,
						int key ) {
	boolean alreadyDeletedKey = false;

	for ( int j = 0; j <= i && !alreadyDeletedKey; j++ ) {
	    alreadyDeletedKey = testData[j] == key;
	} // end for

	return alreadyDeletedKey;
    } // end alreadyDeletedValue

} // end class Lab03



//=====================================================
// LinkedList class
//
// An ordinary linked list containing ints.
// The list consists of just a pointer (top).
//
// The list contains BOTH a dummy header and a
// dummy trailer node, so top always points
// at the dummy header. The dummy trailer is
// always the last node in the list.
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

    private Node top; // A pointer to the dummy header

    /******************************************
     *
     * LinkedList constructor
     *
     * Creates an empty LinkedList (with dummy nodes)
     *
     ******************************************/
    public LinkedList( ) {
	top = new Node( Integer.MIN_VALUE,
			new Node( Integer.MAX_VALUE, null)
		       );
    } // end LinkedList constructor


    /******************************************
     *
     * isEmpty
     *
     * Returns true if the list is empty;
     * otherwise, it returns false.
     *
     ******************************************/
    public boolean isEmpty() {

        return top.next.item == Integer.MAX_VALUE;

    } // end isEmpty

    /******************************************
     *
     * insertOrdered
     *
     * It is passed a new value to add to the list.
     *
     * It inserts a new node containing newItem
     * in the appropriate place so that the list
     * is in sorted order by item.
     *
     * It does NOT check for duplicates ---
     * duplicate values are allowed.
     *
     ******************************************/
    public void insertOrdered( int newItem ) {
	Node prev = top;
	Node curr = top.next;

	while ( curr.item < newItem ) {
	    prev = curr;
	    curr = curr.next;
	}

	prev.next = new Node( newItem, curr );
    } // end insertOrdered


    /******************************************
     *
     * contains
     *
     * Returns true if the list contains key;
     * otherwise, returns false.
     *
     * Assumption: the list is ordered
     *
     ******************************************/
    public boolean contains( int key ) {
	boolean keyFound = false;
	Node curr = top.next; // First "real" node (after dummy header)
	                      // if there are any "real" nodes.

	while ( curr.item < key ) {
	    // Note: list is ordered and trailer's item is > key,
	    // so the condition in the while-loop is correct.
            curr = curr.next;
	} // end while

	keyFound = curr.item == key;
	return keyFound;
    } // end contains


    /******************************************
     *
     * isSorted
     *
     * Returns true if the list is ordered;
     * otherwise, returns false.
     *
     * Assumes: the dummy trailer contains Integer.MAX_VALUE.
     *
     ******************************************/
    public boolean isSorted() {
	boolean sorted = true;
	Node curr = top.next; // First "real" node (after dummy header)
	                      // if there are any "real" nodes.

	// Loop until we reach the dummy trailer or we find two
	// items in consecutive nodes that are not in sorted order.
	while ( curr.next != null && sorted ) {
	    // curr is not the dummy trailer, so there is a node after curr.
	    sorted = curr.item <= curr.next.item;
            curr = curr.next;
	} // end while

	return sorted;
    } // end isSorted

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

	if ( isEmpty() ) {
	    System.out.println( "List is empty" );
	} else {
	    curr = top.next; // First non-dummy node (if any)
	    while ( curr.next != null ) { // not at the trailer yet
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
     * deleteOrdered
     *
     * This is the public driver method for the
     * algorithm that deletes all occurrences
     * of deleteValue from the list.  Since there can
     * be duplicates in the list, we may have
     * to delete more than one node containing
     * deleteValue.
     *
     * This public driver method directly handles
     * the case when the list is empty --- it
     * simply returns.
     * Otherwise, it calls the recursive helper
     * method, which deletes deleteValue everywhere it
     * occurs and returns the node that the dummy header
     * should point at after the deletion is over.
     *
     ******************************************/
    public void deleteOrdered( int deleteValue ) {

	if ( !isEmpty() ) {
	    top.next = deleteOrderedHelper( deleteValue, top.next );
	}
	// If the list is empty, do nothing

    } // end deleteOrdered

    /******************************************
     *
     * deleteOrderedHelper
     *
     * Parameter curr points to a node in the
     * LinkedList that is pointed at by
     * implicit parameter this.
     * Parameter deleteValue contains the value to
     * be deleted.
     *
     * Guarantee: curr is not the dummy trailer
     *
     * This method only works on the sublist
     * that starts at (and includes) curr
     * and goes all the way to the end of
     * the list.
     * This method is supposed to delete all
     * nodes that contain deleteValue in that sublist.
     * This method returns a pointer
     * to the first node in that sublist after
     * the deletions have occurred.
     *
     * Base case: curr contains a value > deleteValue.
     *    This entire sublist does NOT contain
     *    deleteValue since the list is ordered.
     *    In this case, no nodes should be deleted,
     *    so we only have to make sure we return a
     *    pointer to curr.
     *
     * Recursive case: curr contains a value <= deleteValue
     *    A recursive call takes care of deletions
     *    in the list after curr and gives us a pointer
     *    to the first node after curr that doesn't
     *    contain deleteValue.  The returned pointer
     *    is placed into curr's next pointer.
     *
     *    Now we have to decide what to do with curr itself.
     *
     *    If curr does NOT contain deleteValue, then this
     *    method wants to leave curr at the front of the list
     *    --- so it should return a pointer to curr.
     *
     *    If curr DOES contain deleteValue, then this method
     *    wants to delete curr --- so it should return a pointer
     *    to the node after curr.
     *
     ******************************************/
    private Node deleteOrderedHelper( int deleteValue, Node curr ) {

        Node returnNode;

        if(curr.item > deleteValue){
            returnNode = curr;
        }else{
            curr.next = deleteOrderedHelper( deleteValue, curr.next );
            if(curr.item == deleteValue){
                returnNode = curr.next;
            }else{
                returnNode = curr;
            }
        }

        return returnNode;

    } // end deleteOrderedHelper


} // end class LinkedList

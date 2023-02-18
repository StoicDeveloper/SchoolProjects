/**********************************************************
 *
 * COMP 2140 Lab 6; Week of December 2, 2019
 *
 * ER patients waiting in a priority queue implemented as a heap.
 *
 *********************************************************/

import java.io.*;
import java.util.*;

public class Lab06 {

    private static final int NUM_EVENTS = 1000;
    private static final int NUM_EVENT_TYPES = 4;
    private static final int ARRIVAL = 0, SEE_DOCTOR = 1,
	                     EXIT = 2, CONDITION_CHANGE = 3;
    private static final int MAX_PRIORITY = 100;
    private static final int MAX_HEAP_SIZE = 400;


    /*****************************************************************
     *
     * main
     *
     *****************************************************************/
    public static void main( String[] args ) {

	System.out.println( "\nCOMP 2140 Lab 6: ER Patients "
			    + "in a priority queue\n" );

	simulateER();

	System.out.println( "\n\nLab 06 program ended normally.\n" );

    } // end main

    /*****************************************************************
     *
     * simulateER
     *
     * PURPOSE:
     *    Randomly generate events in a simulated ER waiting room:
     *     - a new patient arrives (random ID and priority)
     *     - the highest priority patient waiting gets to see a doctor
     *       (and is therefore no longer waiting)
     *     - a patient leaves the waiting room without seeing a doctor
     *     - a waiting patient's condition changes (change to another
     *       random priority)
     *
     *    Print each event and print out statistics after all events
     *    are done.
     *
     *****************************************************************/
    private static void simulateER() {
	TriagePriorityQueue waitingRoom = new TriagePriorityQueue( MAX_HEAP_SIZE );
	Random generator = new Random();
	int eventType, priority, newPriority, id;
	Patient p;
	boolean[] whoIsIn = new boolean[ NUM_EVENTS+1 ]; // Which patients are still in the waiting room?

	// statistics
	int totalPatients = 0;
	int totalSeenByDoctor = 0;
	int totalExited = 0;
	int totalChanged = 0;

	// Initialize the whoIsIn array
	for ( int i = 0; i <= NUM_EVENTS; i++ ) {
	    whoIsIn[i] = false; // No patient with ID i is in the waiting room.
	} // end for

	for ( int i = 1; i <= NUM_EVENTS; i++ ) {

	    eventType = generator.nextInt( NUM_EVENT_TYPES );

	    if ( eventType == ARRIVAL ) {
		// New patients are assessed by a triage nurse and
		// assigned an ID (totalPatients) and a priority
		// (random) and then wait in the waiting room
		// (the priority queue).
		if ( ! waitingRoom.isFull() ) {
		    totalPatients++;
		    priority = generator.nextInt( MAX_PRIORITY );
		    whoIsIn[totalPatients] = true;
		    waitingRoom.insert( new Patient( totalPatients, priority ) );
		    System.out.println( "New patient with ID " + totalPatients + " and priority " + priority
					+ " arrives." );
		} // end if
	    } else if ( eventType == SEE_DOCTOR ) {
		// Whenever a space for a patient in the ER becomes
		// available, the waiting patient with the highest
		// priority is taken into the ER.
		if ( ! waitingRoom.isEmpty() ) {
		    p = waitingRoom.deleteMax();
		    whoIsIn[ p.getPatientID() ] = false;
		    totalSeenByDoctor++;
		    System.out.println( "Patient with ID " + p.getPatientID() + " and priority "
					+ p.getPriority() + " gets to see a doctor." );
		} // end if
	    } else if ( eventType == EXIT ) {
		// Sometimes patients leave before seeing a doctor,
		// and must be removed from the priority queue.
		if ( ! waitingRoom.isEmpty() ) {
		    id = findAnExistingPatient( generator.nextInt( totalPatients ), totalPatients, whoIsIn );
		    if ( id >= 0 ) {
			p = waitingRoom.patientExits( id );
			whoIsIn[ id ] = false;
			totalExited++;
			System.out.println( "Patient with ID " + id + " and priority "
					    + p.getPriority() + " leaves without seeing a doctor." );
		    }
		}
	    } else {
		// Sometimes a waiting patient's condition changes,
		// and the triage nurse has to increase or decrease
		// the patient's priority.
		if ( ! waitingRoom.isEmpty() ) {
		    id = findAnExistingPatient( generator.nextInt(totalPatients), totalPatients, whoIsIn );
		    if ( id >= 0 ) {
			priority = waitingRoom.findPatientPriority( id );
			newPriority = generator.nextInt( MAX_PRIORITY );
			totalChanged++;
			System.out.println( "Patient with ID " + id + " and priority "
					    + priority + " gets new priority " + newPriority );
			waitingRoom.changePriority( id, newPriority );
		    }
		}
	    } // end if-else-if-else-if-else

	    // Sanity check the heap --- make sure it is still heap ordered
	    if ( ! waitingRoom.isHeapOrdered() ) {
		System.out.println( "\n*** ERROR: something messed up the heap order!\n" );
	    } // end if

	} // end for

	// Print out statistics
	System.out.println( "\nStatisitcs when simulation ended:" );
	System.out.println( "    Number of patients: " + totalPatients );
	System.out.println( "    Number of patients who saw doctors: " + totalSeenByDoctor );
	System.out.println( "    Number of patients who left without seeing a doctor: " + totalExited );
	System.out.println( "    Number of patients whose condition changed: " + totalChanged );
	System.out.println( "    Number of patients still waiting: "
			    + (totalPatients - (totalSeenByDoctor + totalExited)) );

    } // end simulateER


    /************************************************************
     * findAnExistingPatient
     *
     * PURPOSE:
     *    Returns the ID of a patient whose ID is closest to
     *    startID and who is still in the waiting room.
     *
     * PARAMETERS:
     *    whoIsIn: a bit array: whoIsIn[i] is true only if the
     *             patient with ID i is still in the waiting room.
     *    maxID: the largest possible ID, currently.
     *           (it's the total number of patients, so far)
     *    startID: a randomly generated ID so we get a random
     *             patient from among the patients currently
     *             in the waiting room.
     *
     ************************************************************/
    private static int findAnExistingPatient( int startID, int maxID, boolean[] whoIsIn ) {

	int result = -1;
	int i = startID;
	int diff = 1;

	if ( whoIsIn[i] )
	    result = i;
	else {
	    while ( diff < ((maxID+1) / 2) && result == -1 ) {
		if ( whoIsIn[ (i+diff) % maxID ] )
		    result = (i+diff) % maxID;
		else if ( whoIsIn[ (i-diff+maxID) % maxID ] )
		    result = (i-diff+maxID) % maxID;
		diff++;
	    } // end while
	} // end else

	return result;

    } // end findAnExistingPatient

} // end class Lab06


//======================================================================
// class Patient
//======================================================================
// A patient waiting to see a doctor in an ER waiting room.
// The patient is identified by the patient ID (on a paper bracelet)
// which is assigned by the triage nurse.
// The patient is also assigned a priority by the triage nurse, which
// is based on the nurse's assessment of the severity of the patient's
// complaint.  The more fatal the complaint, the higher the priority and
// the sooner the patient should be seen by the ER doctors.
// A patient will be reassessed by the triage nurse periodically, which
// may result in a change to the patient's priority.
class Patient {

    private int patientID;
    private int priority;

    public Patient( int id, int p ) {

	patientID = id;
	priority = p;

    } // end Patient constructor

    public int getPatientID() { return patientID; }
    public int getPriority() { return priority; }

    public void changePriority( int p ) { priority = p; }

} // end class Patient

//======================================================================
// class TriagePriorityQueue
//======================================================================
// A priority queue of patients waiting in an ER waiting room.
// The priority queue is implemented as a max heap.
// A patient's priority is an int, and the larger the int, the higher
// the priority.
// There are some non-standard operations, because patient's priorities
// can be changed if their conditions change while they are waiting.
// Also, sometimes patients leave without waiting until their turn to
// see a doctor.
class TriagePriorityQueue {

    private Patient[] heap;
    private int heapSize; // number of items currently in the heap

    public TriagePriorityQueue( int maxSize ) {

	heap = new Patient[ maxSize ];
	heapSize = 0;

    } // end TriagePriorityQueue constructor


    /************************************************************
     * isEmpty
     *
     * PURPOSE:
     *    Returns true if there are no items in the heap;
     *    returns false otherwise.
     *
     ************************************************************/
    public boolean isEmpty() {

	return heapSize == 0;

    } // end isEmpty


    /************************************************************
     * isFull
     *
     * PURPOSE:
     *    Returns true if there is no room in the heap to
     *    insert another item; returns false otherwise.
     *
     ************************************************************/
    public boolean isFull() {

	return heapSize >= heap.length;

    } // end isFull


    /************************************************************
     * parent
     *
     * PURPOSE:
     *    Returns the index of the parent of the item at index i
     *    (doesn't check if there is a parent).
     *
     ************************************************************/
    public static int parent( int i ) {

	return (i-1)/2;

    } // end parent


    /************************************************************
     * rightChild
     *
     * PURPOSE:
     *    Returns the index of the right child of the
     *    item at index i (doesn't check if there is a
     *    right child).
     ************************************************************/
    public static int rightChild( int i ) {

	return 2 * i + 2;

    } // end rightChild


    /************************************************************
     * leftChild
     *
     * PURPOSE:
     *    Returns the index of the left child of the
     *    item at index i (doesn't check if there is a
     *    left child).
     ************************************************************/
    public static int leftChild( int i ) {

	return 2 * i + 1;

    } // end leftChild

    /*****************************************************************
     *
     * lastParent
     *
     * PURPOSE:
     *    Return the index of the last (rightmost) parent in the heap.
     *
     *****************************************************************/
    public int lastParent() {

	return (heapSize / 2) - 1;

    } // end lastParent

    /*****************************************************************
     *
     * highestPriorityChild
     *
     * PURPOSE:
     *    Return the index of the child of heap[index] that has
     *    the highest priority if heap[index] has two children.
     *
     *    Return the index of the only child if heap[index] has
     *    only one child.
     *
     *    Return -1 if heap[index] has no children (is a leaf).
     *
     *****************************************************************/
    public int highestPriorityChild( int index ) {

	int highest = -1;
	int left = leftChild( index );
	int right = left+1;

	if ( left < heapSize ) {
	    // heap[index] has at least one child
	    highest = left;
	    if ( right < heapSize &&
		 heap[left].getPriority() < heap[right].getPriority()
	       ) {
		// heap[index] has TWO children and the right child
		// has higher priority than the left child
		highest = right;
	    } // end if
	} // end if

	return highest;

    } // end highestPriorityChild


    /*************************************************************
     *
     * insert
     *
     * PURPOSE:
     *    Add a new patient.
     *
     ******************************************************************/
    public void insert( Patient p ) {

	if ( ! isFull() ) {
	    heap[ heapSize ] = p;
	    heapSize++;
	    siftUp( heapSize-1 );
	} // end if

    } // end insert


    /*************************************************************
     *
     * deleteMax
     *
     * PURPOSE:
     *    Delete and return a highest priority item.
     *
     ******************************************************************/
    public Patient deleteMax() {

	Patient result = null;

	if ( ! isEmpty() ) {
	    result = heap[0];
	    heapSize--;
	    heap[0] = heap[heapSize];
	    siftDown( 0 );
	} // end if

	return result;

    } // end deleteMax



    /*************************************************************
     *
     * siftUp
     *
     * PURPOSE:
     *    The item at heap[index] may have higher priority
     *    than its parent, but no other problems exist in the heap.
     *    Restore heap order by repeatedly moving its parent down
     *    until either:
     *    (1) the root was moved down, or
     *    (2) its parent's priority is >= its priority.
     *    Put the item into the "hole" created by the item last
     *    moved down.
     *
     ******************************************************************/
    private void siftUp( int index ) {
	Patient toSift = heap[index]; // make a "hole" in the heap
	int i = index;
	int iParent = parent( index );

	while ( i > 0 && heap[iParent].getPriority() < toSift.getPriority() ) {
	    heap[i] = heap[iParent]; // move the "hole" up to the parent
	    i = iParent;
	    iParent = parent( i );
	} // end while
	heap[i] = toSift; // put the sifted item into the correct position

    } // end siftUp


    /*************************************************************
     *
     * siftDown
     *
     * PURPOSE:
     *    The item at heap[index] may have lower priority
     *    than one or both of its children (if it has any), but no
     *    other problems exist in the heap.
     *    Restore heap order by repeatedly moving up its
     *    highest-priority child until either:
     *    (1) the item in a leaf is moved up, or
     *    (2) its priority is no smaller than either of its children's
     *        priorities.
     *    Put the item into the "hole" created by the last moved item.
     *
     ******************************************************************/
    private void siftDown( int index ) {
	Patient toSift = heap[index]; // make a "hole" in the heap
	int i = index;
	int iChild = highestPriorityChild( index );

	while ( i <= lastParent() && toSift.getPriority() < heap[iChild].getPriority() ) {
	    heap[i] = heap[iChild]; // move the "hole" down to the child
	    i = iChild;
	    iChild = highestPriorityChild( i );
	} // end while
	heap[i] = toSift; // put the sifted item into the correct position

    } // end siftUp


    //********* Non-standard priority queue methods *************


    /************************************************************
     * isHeapOrdered
     *
     * PURPOSE:
     *    Returns true if the heap array is in heap
     *    order; returns false otherwise.
     *
     *    Because this is a max heap, it is heap ordered
     *    if every parent's priority is no smaller than
     *    the priorities of its child or children.
     *
     ************************************************************/
    public boolean isHeapOrdered() {
        boolean ordered = true;
        for( int i = 0; i < lastParent() && ordered; i++ ){
            if( heap[ leftChild(i) ].getPriority() > heap[ i ].getPriority() ||
                    ( heap[ rightChild(i) ] == null ||
                      heap[ rightChild(i) ].getPriority() > heap[ i ].getPriority() ) )
            {
                ordered = false;
            }
        }
        return ordered;

    } // end isHeapOrdered


    /*************************************************************
     *
     * changePriority
     *
     * PURPOSE:
     *    Change the priority of the patient with id patientID
     *    to newPriority, if such a patient exists.
     *
     *    After changing the priority, the heap may no longer be
     *    in heap order, so restore heap order.
     *
     *    Do nothing if the patient doesn't exist.
     *
     ******************************************************************/
    public void changePriority( int patientID, int newPriority ) {

        int pos;
        if( ( pos = findPatientPosition( patientID ) ) > -1 ){
            heap[ pos ].changePriority( newPriority );
            siftUp( pos );
            siftDown( pos );
        }

    } // end changePriority


    /*************************************************************
     *
     * patientExits
     *
     * PURPOSE:
     *    A patient leaves the ER without seeing a doctor.
     *    Remove them from the priority queue and return the
     *    Patient object.
     *
     ******************************************************************/
    public Patient patientExits( int patientID ) {

	Patient result = null;
        int foundIndex = findPatientPosition( patientID );

	if ( foundIndex >= 0 ) {
	    // Found the patient.  Now remove the patient.
	    result = heap[ foundIndex ];
	    if ( foundIndex == heapSize - 1 ) {
		// The patient at the end of the heap is easy to remove.
		heapSize--;
	    } else {
		// If the patient isn't the one at the end of the heap,
		// then we do something similar to deleteMax: replace
		// the exiting patient with the last patient and then
		// restore heap order by sifting down.
		heapSize--;
		heap[ foundIndex ] = heap[heapSize];
		siftDown( foundIndex );
	    } // end if-else
	} // end if

	return result;

    } // end patientExits


    /*************************************************************
     *
     * findPatientPriority
     *
     * PURPOSE:
     *    Return the priority of the patient with id patientID;
     *    return -1, if the patient isn't in the heap.
     *
     ******************************************************************/
    public int findPatientPriority( int patientID ) {
	int result = -1;
	int index = findPatientPosition( patientID );

	if ( index >= 0 )
	    result = heap[index].getPriority();

	return result;
    } // end findPatientPriority


    /*************************************************************
     *
     * findPatientPosition
     *
     * PURPOSE:
     *    Return the index in heap of the patient with id
     *    patientID; return -1 if the patient isn't in the heap.
     *
     ******************************************************************/
    private int findPatientPosition( int patientID ) {

	int i = 0;
        int foundIndex = -1;

	while ( i < heapSize && foundIndex < 0 ) {
	    if ( heap[i].getPatientID() ==  patientID ) {
		foundIndex = i;
	    } else {
		i++;
	    } // end if-else
	} // end while

	return foundIndex;

    } // end findPatientPosition

} // end class TriagePriorityQueue

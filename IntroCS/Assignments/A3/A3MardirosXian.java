/*
A2MardirosXian
COMP 2140 Section A1
Instructor  Cameron
Assignment  Assignment 3
@author     Xian Mardiros 7862786
@version    Oct 29, 2019

Purpose: use queue and stack ADTs to model an elevator
*/

import java.util.*;
import java.io.*;

public class A3MardirosXian{
    private static Elevator elevator;

    public static void main( String[] args ){

        System.out.println( "Enter the input file name (.txt files onlty):" );

        try{
            processElevatorFile();
            runSimulation();
            elevator.printStats();

            System.out.println("Processing ends normally");

        }catch(IOException error){
            System.out.println( "File not found" );
            System.out.println( error.toString() );
        }

    }

    // processes the input file and creates the elevator instance from the resulting data
    public static void processElevatorFile() throws IOException{
        Scanner file = new Scanner( new File( ( new Scanner( System.in ) ).nextLine() ) );
        int capacity = file.nextInt();
        int floors = file.nextInt();
        Queue arrivalQueue = new Queue();

        // parse each employee from file into arrivalQueue,
        // retreiving each of arrivalTime, ID, dest, and origin in order
        while( file.hasNextInt() ){
            arrivalQueue.enter( new Employee( file.nextInt(), file.nextInt(), file.nextInt(), file.nextInt() ) );
        }

        //System.out.println( arrivalQueue.toString() );

        elevator = new Elevator( capacity, floors, arrivalQueue );
    }

    public static void runSimulation(){
        while( !elevator.noMoreArrivals() ){
            elevator.checkArrivals();
            elevator.nextAction();
        }
        elevator.finishSim();
    }
}

// Store all data related to a given employee using the elevator
class Employee{
    int ID;
    int arrivalTime;
    int origin;
    int dest;

    // --- CONSTURCTOR ---

    public Employee(int arrivalTime, int ID, int origin, int dest){
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.dest = dest;
    }

    // --- ACCESSOR METHODS ---

    public int getArrival(){
        return arrivalTime;
    }

    public int getID(){
        return ID;
    }

    public int getOrigin(){
        return origin;
    }

    public int getDest(){
        return dest;
    }

    // --- TOSTRING METHOD ---

    public String toString(){
        return String.format( "Employee %d, arrival floor %d, arrival time %d, desired floor %d", ID, origin, arrivalTime, dest );
    }

}

// Provides most of the functionality and data storage related to the elevator simulation
// Contains many variables to track elevator state, a few constants for clarity,
// some output variables for tracking elevator progress, and various methods to control
// elevator behaviour
class Elevator{

    // --- INSTANCE VARIABLES ---

    private int capacity;
    private int floors;
    private int time;               // current sim time
    private int floor;              // current location
    private int direction;          // two possible values: GOING_UP and GOING_DOWN
    private int directionExtremum;  // two possible values: floors-1 and 0
    private int otherDirection;     // will always be the opposite of 'direction'
    private int otherExtremum;      // will always be the opposite of 'directionExtremum'
    private Queue arrivalQueue;     // the list of people who have yet to arrive
    private Queue[][] waitingQueues;// the lists of people waiting to go up or down
    private Stack passengers;       // current elevator contents
    private int[] buttons;          // the floor which the elevator currently intends to stop at
    private int occupants;          // length of passengers Stack
    private int waiting;            // number of people waiting to be picked up

    // --- ELEVATOR CONSTANTS ---

    private int QUEUES_PER_FLOOR = 2;
    private int GOING_DOWN = 0;
    private int GOING_UP = 1;

    // --- OUTPUT VARIABLES ---

    private int trips = 0;
    private int tripTimeTotal = 0;
    private int minTime = Integer.MAX_VALUE;
    private Employee minEmployee;
    private int maxTime = 0;
    private Employee maxEmployee;

    // ---CONSTRUCTOR ---

    public Elevator(int capacity, int floors, Queue arrivalQueue){
        this.capacity = capacity;
        this.floors = floors;
        direction = GOING_UP;
        otherDirection = GOING_DOWN;
        directionExtremum = floors-1;
        this.arrivalQueue = arrivalQueue;
        waitingQueues = new Queue[ floors ][ QUEUES_PER_FLOOR ];
        passengers = new Stack();
        buttons = new int[floors];

        for( int i = 0; i < floors; i++ ){
            for( int j = 0; j < QUEUES_PER_FLOOR; j++ ){
                waitingQueues[i][j] = new Queue();
            }
        }
    }

    // check if more passengers will arrive
    public boolean noMoreArrivals(){
        return arrivalQueue.isEmpty();
    }

    // add all of the people currently arriving to the appropriate destination floor queue
    public void checkArrivals(){
        while( !arrivalQueue.isEmpty() && arrivalQueue.front().getArrival() == time ){
            Employee arrivedEmployee = arrivalQueue.leave();
            employeeArrives( arrivedEmployee );
            waiting++;
        }
    }

    // model elevator behaviour during one time unit
    public void nextAction(){

        // change direction if it is either at the end,
        // or wouldn't help anyone by continuing, but would by changing direction
        if( cantGoFurther() || ( passengers.isEmpty() &&
                    !passengersWaitingAhead( direction ) &&
                    passengersWaitingAhead( otherDirection ) ) ){
            changeDirection();
        }

        boolean mustExit = buttons[ floor ] > 0;
        boolean notFull = occupants < capacity;
        boolean mustEnter = !waitingQueues[ floor ][ direction ].isEmpty();

        // check to see if elevator should open doors or keep moving
        if( mustExit || ( notFull && mustEnter ) ){
            openDoors( mustExit, notFull, mustEnter );
        }else if( occupants > 0 || passengersWaitingAhead( direction ) ){
            floor += ( direction == GOING_DOWN ) ? -1 : 1;
            System.out.printf(
                    "Time %d: Elevator moves %s to floor %d\n", time,
                    ( direction == GOING_DOWN ) ? "down" : "up", floor );
        }else{
            System.out.printf(
                    "Time %d: Elevator waits on foor %d\n", time, floor );
        }

        //printWaitQueues();

        time++;
    }

    // add the parameter employee to the destination floor's appropriate queue
    public void employeeArrives(Employee newEmployee){
        int employeeDirection;
        int origin = newEmployee.getOrigin();
        int dest = newEmployee.getDest();

        employeeDirection = ( origin > dest ) ? GOING_DOWN : GOING_UP;

        //System.out.println( newEmployee.getID() );

        waitingQueues[ origin ][ employeeDirection ].enter( newEmployee );

        System.out.printf( "Time %d: A person begings waiting to go %s: %s\n", time,
                ( direction == GOING_DOWN ) ? "down" : "up", newEmployee.toString() );
    }

    // check if the elevator is at the top or bottom floor
    private boolean cantGoFurther(){
        return floor == directionExtremum;
    }

    // checks for waiting employees in both queues on all of the floors in the current
    // direction, stopping when an employee is found
    private boolean passengersWaitingAhead( int checkingDirection ){
        boolean foundPassenger = false;
        int checkingExtremum =
            ( checkingDirection == direction ) ? directionExtremum : otherExtremum;

        int increment = ( checkingDirection == GOING_UP ) ? 1 : -1;
        int checkingFloor = floor + increment;
        int stopAt = checkingExtremum + increment;

        while( !foundPassenger && ( checkingFloor != stopAt ) ) {
            //Sysem.out.println( "Floor " + checkingFloor + " queues:" );
            //System.out.println( waitingQueues[ checkingFloor ][ GOING_UP ].toString() );
            //System.out.println( waitingQueues[ checkingFloor ][ GOING_DOWN ].toString() );
            // inner brackets in conditional included for clarity
            foundPassenger = !waitingQueues[ checkingFloor ][ GOING_DOWN ].isEmpty() ||
                !waitingQueues[ checkingFloor ][ GOING_UP ].isEmpty();
            checkingFloor += increment;
        }

        return foundPassenger;
    }

    // changes the direction from up to down or vice-versa depending on current direction
    // also changes the directionExtremum, which keeps track of the last floor in the
    // current direction
    private void changeDirection(){
        directionExtremum += otherExtremum - ( otherExtremum = directionExtremum );
        direction += otherDirection - ( otherDirection = direction );
        System.out.printf( "Time %d: Elevator changed direction: Now going %s.\n", time, ( direction == GOING_DOWN ) ? "down" : "up" );
    }

    // switch the values of two int variables
    // left in for proof my my foolishness, this function only swaps the contents of
    // the parameter variables, not the argument variables. Further, java is pass by
    // reference, and no function can accomplish this task in this way
    //private void intSwitch( int int1, int int2 ){
    //    int1 += ( int2 - ( int2 = int1 ) );
    //}

    // allow people to leave or enter the elevator
    // people will only enter if there is room, so notFull is made true if people leave
    private void openDoors( boolean mustExit, boolean notFull, boolean mustEnter ){
        if( mustExit ){
            exitElevator();
            notFull = true;
        }
        if( notFull && mustEnter ){
            enterElevator();
        }
    }

    // allow employees to leave the elevator, but checking how many need to leave, then popping
    // non-exiting passengers into a temporary stack until the correct number of exiting
    // passengers have been removed (updating instance variables and statistics each time),
    // then popping the employees in the temporary stack back into the elevator
    private void exitElevator(){
        Stack temp = new Stack();

        while( buttons[ floor ] > 0 ){
            Employee top = passengers.top();

            // passenger either gets off or makes way (into temp stack) for those behind
            if( top.getDest() == floor ){
                // update state of elevator
                passengers.pop();
                occupants--;
                buttons[ floor ]--;

                // update elevator stats
                trips += 1;
                int tripTime = time - top.getArrival();
                tripTimeTotal += tripTime;
                if( tripTime > maxTime ){
                    maxTime = tripTime;
                    maxEmployee = top;
                }
                if( tripTime < minTime ){
                    minTime = tripTime;
                    minEmployee = top;
                }

                // print message
                System.out.printf( "Time %d: Got off the elevator: %s\n",
                        time, top.toString() );
            }else{
                temp.push( passengers.pop() );
            }
        }

        // those who made way file back into the elevator
        while( !temp.isEmpty() ){
            passengers.push( temp.pop() );
        }
    }

    private void enterElevator(){
        Queue entryQueue = waitingQueues[ floor ][ direction ];
        while( !entryQueue.isEmpty() && occupants < capacity ){
            Employee toEnter = entryQueue.leave();
            passengers.push( toEnter );
            buttons[ toEnter.getDest() ]++;
            occupants++;
            waiting--;

            System.out.printf( "Time %d: Got on the elevator: %s\n", time, toEnter.toString() );
        }
    }

    public void finishSim(){
        while( waiting > 0 || occupants > 0){
            nextAction();
        }
    }

    public void printStats(){
        System.out.printf( (
                "\nElevator simulation statistics:\n" +
                "\tTotal number of trips: %d\n" +
                "\tTotal passenger time: %d\n" +
                "\tAverage trip time: %.2f\n" +
                "\tMinimum trip time: %d\n" +
                "\tMinimum trip details: %s\n" +
                "\tMaximum trip time: %d\n" +
                "\tMaximum trip details: %s\n\n" ),
                trips, tripTimeTotal, tripTimeTotal/(double)trips,
                minTime, minEmployee.toString(), maxTime, maxEmployee.toString() );
    }

    public void printWaitQueues(){
        int floor = 0;
        for( int i = 0; i < floors; i++ ){
            System.out.printf( "Floor %d queues:\n", i );
            Queue upQueue = waitingQueues[ i ][ GOING_UP ];
            Queue downQueue = waitingQueues[ i ][ GOING_DOWN ];
            if( !upQueue.isEmpty() ){
                System.out.printf( "UP - %s\n", waitingQueues[ i ][ GOING_UP ] );
            }
            if( !downQueue.isEmpty() ){
                System.out.printf( "DOWN - %s\n", waitingQueues[ i ][ GOING_DOWN ] );
            }
        }
    }
}

class Stack{
    private static final int MAX_SIZE = 10;

    private int top;
    private Employee[] stackArray;

    public Stack(){
        stackArray = new Employee[MAX_SIZE];
        top = -1;

    }

    public void push(Employee newEmployee){
        top++;
        stackArray[top] = newEmployee;
    }

    public Employee pop(){
        Employee toReturn = stackArray[top];
        top--;
        return toReturn;
    }

    public Employee top(){
        return stackArray[top];
    }

    public boolean isEmpty(){
        return top == -1;
    }

    public String toString(){
        int curr = top;
        String toReturn = "Stack contains, from top: ";

        if( isEmpty() ){
            toReturn += "stack is empty";
        }else{
            while( curr >= 0 ){
                toReturn += String.format("ID %d, ", stackArray[curr].getID());
                curr--;
            }
        }

        return toReturn;
    }
}

class Queue{
    private Node end;

    private class Node{
        public Employee item;
        public Node next;

        public Node(Employee item, Node next){
            this.item = item;
            this.next = next;
        }
    }

    // add the parameter employee at the back of the queue
    public void enter(Employee newEmployee){
        Node newNode;
        if( isEmpty() ){
            newNode = new Node(newEmployee, null);
            newNode.next = newNode;
        }else{
            // System.out.println( newEmployee.getID() );

            newNode = new Node(newEmployee, end.next);
            end.next = newNode;
        }
        end = newNode;
    }

    // return the employee that has been in the queue the longest
    public Employee leave(){
        Employee toReturn = end.next.item;

        if(end == end.next){
            end = null;
        }else{
            // takes care of last item edge case
            end.next = end.next.next;
        }

        return toReturn;
    }

    // return the employee who will next exit the queue, without removing them
    public Employee front(){
        return end.next.item;
    }

    // determine whether the queue has any employees in it
    public boolean isEmpty(){
        return end == null;
    }

    public String toString(){
        String toReturn = "Queue contains, from front: ";

        if( isEmpty() ){
            toReturn += "queue is empty.";
        }else{
            Node curr = end.next;
            do{
                toReturn += String.format("ID %d, ", curr.item.getID());
                curr = curr.next;
            }while( curr != end.next );
        }

        return toReturn;
    }
}

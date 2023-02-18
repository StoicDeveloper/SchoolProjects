/*
A2MardirosXian
COMP 2140 Section A1
Instructor  Cameron
Assignment  Assignment 2
@author     Xian Mardiros 7862786
@version    Oct 14, 2019

Purpose: model an airport baggage conveyer belt using doubly linked lists
*/

import java.io.*;
import java.util.Scanner;

public class A2MardirosXian{

    private static ConveyerBelt belt = new ConveyerBelt();

    public static void main(String[] args){

        // Allow user to choose file with keyboard input.
        System.out.println( "\nEnter the input file name (.txt files only): " );

        try{
            parseCommandFile();
        }catch(IOException error){
            System.out.println( error.toString() );
        }

        System.out.println( "\nEnd of Processing." );
    }

    // take the user's input file name, iterate over its contents, execute each command
    public static void parseCommandFile() throws IOException{
        Scanner file;
        String command;
        String line;

        // take the user input, and make a string of the file's contents
        file = new Scanner(new File((new Scanner( System.in )).nextLine()));
        System.out.println( "Processing file...." );

        // iterate to end of file
        while(file.hasNextLine()){
            // store command name line, print the command,
            // then break it into tokens for retreiving command name and any arguments
            line = file.nextLine();
            System.out.printf( "=====Processing command:  %s =====\n", line );

            Scanner lineToks = new Scanner(line);
            command = lineToks.next();

            // CHECKBAGS by iterating over file lines the specified number of times,
            // collecting baggage data, and passing it to checkBaggage method, then print output
            // PRINTSUMMARY and PRINTDETAIL just call the appropriate methods
            // LOADFLIGHT and REMOVEOVERSIZE take the int or double argument tokens
            // and calls the method, passing the arguments
            if(command.equals("CHECKBAGS")){
                int bags = lineToks.nextInt();
                int vipBags = 0;

                for(int i = 0; i < bags; i++){
                    int flight = file.nextInt();
                    float weight = file.nextFloat();
                    float size = file.nextFloat();
                    boolean isVIP = file.nextBoolean();

                    if(isVIP){
                        vipBags++;
                    }
                    file.nextLine(); // skip remaining line break character

                    belt.checkBaggage(flight, weight, size, isVIP);
                }

                System.out.printf( "%d VIP and %d regular bags checked in.\n", vipBags, bags - vipBags );
            }else if(command.equals("PRINTSUMMARY")){
                belt.printSummary();
            }else if(command.equals("PRINTDETAIL")){
                belt.printDetail();
            }else if(command.equals("LOADFLIGHT")){
                int flight = lineToks.nextInt();

                belt.loadFlight(flight);
            }else if(command.equals("REMOVEOVERSIZE")){
                double maxSize = lineToks.nextDouble();

                belt.removeOversize(maxSize);
            }
        }
        file.close();
    }
}

// store info related to one bag, provide accessor methods to retrieve that info
// setter methods not needed, since bag info won't be changed after creation
class Baggage{

    private int flight;
    private float weight;
    private float size;
    private boolean isVIP;

    public Baggage(int flight, float weight, float size, boolean isVIP){
        this.flight = flight;
        this.weight = weight;
        this.size = size;
        this.isVIP = isVIP;
    }

    public int getFlight(){
        return flight;
    }

    public float getWeight(){
        return weight;
    }

    public float getSize(){
        return size;
    }

    public boolean getVIP(){
        return isVIP;
    }

}

// store info related to contents of a baggage conveyer belt, provide methods for adding
// and removing bags, and printing details of contents
class ConveyerBelt{

    private Node first;
    private Node last;
    private Node lastVIP;
    private int total = 0;
    private int totalVIP = 0;
    private float totalWeight = 0;

    // Nodes only accessible within ConveyerBelt, provide method for removing and insterting
    // single node, and to print a single node's info
    private class Node{

        public Baggage bag;
        public Node prev;
        public Node next;

        // constructer not specifying linked nodes
        public Node(Baggage bag){
            this.bag = bag;
        }

        // remove Node from belt, change adjacent nodes' links and edit belt variables
        public void remove(){
            if(prev != null){
                prev.next = next;
            }//else{
            //    first = next;
            //}
            if(next != null){
                next.prev = prev;
            }

            total--;
            if(this.bag.getVIP()){
                totalVIP--;
            }
            totalWeight -= this.bag.getWeight();

            // had to add as edge case, otherwise the list wouldn't be empty even once all bags
            // were "removed"
            //if(total == 0){
            //    first = null;
            //}

        }

        // insert Node by changing links before, and ife necessary, after insert position
        public void insertAfter(Node currNode){
            if(currNode.next != null){
                Node formerNext = currNode.next;
                formerNext.prev = this;
                this.next = formerNext;
            }
            currNode.next = this;
            this.prev = currNode;
        }

        public void printDetail(){
            System.out.printf( "Flight number:  %d, Weight:  %.1f kg, Size:  %.1f cm, VIP: %b\n",
                    bag.getFlight(), bag.getWeight(), bag.getSize(), bag.getVIP());
        }
    }

    // creates Baggage instance according to specified parameters, then inserts
    // the Baggage at the correct location, either at the end of belt
    // or between the last VIP Baggage and the first non-VIP baggage
    public void checkBaggage(int flight, float weight, float size, boolean isVIP){
        Baggage newBaggage = new Baggage(flight, weight, size, isVIP);
        Node newNode = new Node(newBaggage);

        // three cases: 1) total and totalVIP == 0, 2) totalVIP == 0, 3) and neither == 0
        // each case must accomodate a VIP and nonVIP Baggage
        // first case: empty belt
        if(total == 0){
            first = newNode;
            last = newNode;
            if(isVIP){
                lastVIP = newNode;
            }
        }else{
            if(isVIP){
                // second case: only VIP == 0
                if(totalVIP == 0){
                    newNode.next = first;
                    first = newNode;
                    lastVIP = newNode;
                }else{
                    newNode.insertAfter(lastVIP);
                    lastVIP = newNode;
                }
            }else{
                // third case: neither are 0
                newNode.insertAfter(last);
                last = newNode;
            }
        }

        // housekeeping: increment belt variables
        if(isVIP){
            totalVIP++;
        }
        totalWeight += weight;
        total++;
    }

    // print info from belt that is stored as instance variables
    public void printSummary(){
        System.out.printf( "Total number of bags:  %d, Number of VIP bags:  %d, Total weight of bags:  %.1f\n", total, totalVIP, totalWeight );
    }

    // as above, but also iterate over belt and print details of each Baggage
    public void printDetail(){
        int count = 0;
        Node currNode = first;

        printSummary();
        System.out.println( "The bags on the conveyer belt are:" );

        while(currNode != null){
            count++;

            System.out.printf( "%d) ", count );

            currNode.printDetail();
            currNode = currNode.next;
        }
    }

    // iterate over belt and remove all bags associated with given flight number
    public void loadFlight(int flight){
        int count = 0;
        Node currNode = first;
        while(currNode != null){
            if(currNode.bag.getFlight() == flight){
                currNode.remove();
                count++;
            }
            currNode = currNode.next;
        }
        System.out.println( count + " bags loaded onto flight " + flight + ".");
    }

    // iterate over belt and remove all bags over a certain size
    public void removeOversize(double max){
        int count = 0;
        Node currNode = first;
        while(currNode != null){
            if(currNode.bag.getSize() > max){
                currNode.remove();
                count++;
            }
            currNode = currNode.next;
        }
        System.out.println( count + " removed as oversized." );

    }
}



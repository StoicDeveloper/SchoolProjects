/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author xian
 */
import java.util.*;
import java.io.*;

public class A4MardirosXian {


// handles input and splitting of input files into words
// creates a table, and a BST

    private static int TABLE_SIZE = 20000;
    private static int totalWords = 0;

    public static void main(String[] args){

        System.out.println( "Input file name:" );

        try{
            WordTable table = new WordTable( TABLE_SIZE );
            Scanner file = new Scanner( new File( (new Scanner( System.in ) ).nextLine() ) );

            while( file.hasNextLine() ){
                String[] words = file.nextLine().toLowerCase().split( "[^a-z]+" );
                for( String word: words ){
                    if( word.length() > 0 ){
                        WordCount foundKey = table.search( word );
                        if( foundKey == null ){
                            table.insert( word );
                        }else{
                            foundKey.increment();
                        }
                        totalWords++;
                    }
                }
            }

            System.out.println( "Total number of words in the file: " + totalWords );
            System.out.println( "Number of unique words: " + table.getNumItems() );

            BinarySearchTree tree = new BinarySearchTree();
            table.initTraversal();
            while( table.objsToRetrieve() ){
                tree.insert( table.nextWordCount() );
            }

            System.out.println(
                    "\nEnter the number of items to print (positive integers only):" );
            int toPrint = ( new Scanner( System.in ) ).nextInt();

            System.out.println( "\nThe first 5 and last 5 words in alphabetical order:" );

            tree.printTree( toPrint );

            System.out.print( "\nProcessing ends normally" );
        }catch( IOException error ){
            System.out.println( error.toString() );
        }
    }
}

// stores a single word and its count
class WordCount{

    private String word;
    private int count;

    public WordCount( String word ){
        this.word = word;
        count = 1;
    }

    public void increment(){
        count++;
    }

    public String getWord(){
        return word;
    }

    public int getCount(){
        return count;
    }

    public String toString(){
        return "word = " + word + ", count = " + count;
    }
}

class WordTable{

    // Class Node: An ordinary linked-list node
    private class Node {
        public WordCount item;
        public Node next;

        public Node( String word, Node newNext ) {
            item = new WordCount( word );
            next = newNext;
        }
    }

    private static final int A = 13; // For the hash function

    private Node[] hashArray; // The array of linked lists.
    private int numberItems; // The number of items currently stored in the table.
    private int position;
    private Node curr;

    public int getNumItems(){
        return numberItems;
    }

    // TableWithSC constructor
    // Assumption: tableSize is a prime number
    public WordTable( int tableSize ) {
        hashArray = new Node[ closePrime( tableSize ) ];

        for ( int i = 0; i < hashArray.length; i++ ) {
            hashArray[ i ] = null;
        }

        numberItems = 0;
    }

    // Hashes key and returns the resulting array index.
    // The hash function uses the polynomial hash code
    // implemented using Horner's method.
    private int hash( String key ) {
        int hashIndex = 0;

        for( int i = 0; i < key.length(); i++ ){
            hashIndex = (hashIndex * A ) % hashArray.length + (int) key.charAt(i);
            hashIndex = hashIndex % hashArray.length;
        }

        return hashIndex;
    }

    // Inserts key into the table (except if key is already in the table,
    // in which case, this method prints an error message).
    // Also, this method increments numberItems if the key is inserted.
    public void insert( String key ){
        int hashIndex = hash( key );

        if( inLinkedList( key, hashArray[ hashIndex ] ) != null ){
            hashArray[ hashIndex ] = new Node( key, hashArray[ hashIndex ] );
            numberItems++;
        }else {
            // We found a duplicate of key already in the table.
            // No duplicates are allowed, so abort the insert.
            System.out.println( "***ERROR: Attempting to insert "
                    + " a duplicate of \"" + key
                    + "\" into a TableWithSC" );
        }
    }

    // Searches the table for key, and returns true if key is found,
    // false if it isn't.
    public WordCount search( String key ) {
        int hashIndex = hash( key );
        return inLinkedList( key, hashArray[ hashIndex ] );
    }

    // Searches the linked list pointed at by top for a node containing
    // key, and returns true if key is found and false if it isn't.
    private WordCount inLinkedList( String key, Node top ) {
        Node curr = top;
        WordCount foundKey = null;

        while ( curr != null && foundKey == null ) {
            if( curr.item.getWord().equals( key ) ){
                foundKey = curr.item;
            }
            curr = curr.next;
        }

        return foundKey;
    }


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
                hashIndex = hash( hashArray[i].item.getWord() );
                output += "\n   Keys at index = " + i + ": ";
                curr = hashArray[i];
                while ( curr != null ) {
                    output += "\n      " + curr.item;
                    curr = curr.next;
                } // end while
            } // end if
        } // end for

        return output;
    }


    // Returns the smallest, odd prime number >= n.
    // It simply checks odd numbers >= n until it finds a prime number.
    private static int closePrime( int n ){
        int value = n;

        // Start with an odd number, because an even number isn't prime
        // (except for 2, which is too small to be a reasonable table size).
        if ( value % 2 == 0 ) {
            value++;
        }

        // Try odd numbers >= n until you find one that is a prime number
        while( ! isPrime( value ) ){
            value += 2;
        }

        return value;
    }

    // Returns true if the number is prime; otherwise, returns false.
    // If n has any factors other than 1 and itself, it must have
    // a factor between (and including) 2 and sqrt(n).
    private static boolean isPrime( int n ){
        boolean noFactorFound = n % 2 != 0; // Make sure 2 isn't a factor

        // Try all odd numbers <= sqrt( n ) as factors
        for( int i = 3; noFactorFound && i * i <= n; i += 2 ){
            noFactorFound = n % i != 0; // i is not a factor if n % i is not 0.
        }

        return noFactorFound;
    }

    // set up the hash array traversal, set position to index of first non-empty array
    // element, set curr to first node of that element
    // if empty array, curr remains null and position is the array length
    public void initTraversal(){
        position = 0;
        curr = null;
        while( position < hashArray.length && curr == null ){
            if( hashArray[ position ] != null ){
                curr = hashArray[ position ];
            }else{
                position++;
            }
        }
    }

    public boolean objsToRetrieve(){
        boolean toRetrieve = false;
        if( position < hashArray.length && curr != null ){
            toRetrieve = true;
        }

        return toRetrieve;
    }

    public WordCount nextWordCount(){
        WordCount toReturn = curr.item;
        if( curr.next != null ){
            curr = curr.next;
        }else{
            curr = null;
            position++;
            while( position < hashArray.length && curr == null ){
                if( hashArray[ position ] != null ){
                    curr = hashArray[ position ];
                }else{
                    position++;
                }
            }
        }

        return toReturn;
    }


}

class BinarySearchTree{
    BSTNode root;
    int numberItems;

    private class BSTNode{
        public WordCount item;
        public BSTNode left;
        public BSTNode right;

        public BSTNode( WordCount item ){
            this.item = item;
        }

    }

    public BinarySearchTree(){
        root = null;
        numberItems = 0;
    }

    public void insert( WordCount newWordCount ){
        if( root == null ){
            root = new BSTNode( newWordCount );
        }else{
            insertHelper( newWordCount, root );
        }
    }

    private void insertHelper( WordCount newWordCount, BSTNode currNode ){
        int diff = newWordCount.getWord().compareTo( currNode.item.getWord() );
        if( diff > 0 ){
            if( currNode.right != null ){
                insertHelper( newWordCount, currNode.right );
            }else{
                currNode.right = new BSTNode( newWordCount );
            }
        }else{
            if( currNode.left != null ){
                insertHelper( newWordCount, currNode.left );
            }else{
                currNode.left = new BSTNode( newWordCount );
            }
        }
    }

    public void printTree( int numToPrint ){
        if( numToPrint*2 < numberItems ){
            printTree( root, numToPrint, numberItems - 2*numToPrint, numToPrint );
        }else{
            printTree( root, 0, 0, numberItems );
        }
    }

    private int printTree( BSTNode curr, int startPrint, int middleSkip, int endPrint ){
        int leftTraversed = 0;
        int rightTraversed = 0;
        if( curr.left != null ){
            leftTraversed = printTree( curr.left, startPrint, middleSkip, endPrint );
            startPrint -= leftTraversed;
            if( startPrint < 0 ){
                middleSkip += startPrint;
                startPrint = 0;
            }
        }
        if( startPrint > 0 ){
            System.out.println( curr.item.toString() );
            startPrint--;
            if( startPrint == 0 ){
                System.out.println( "... (skipping middle elements)" );
            }
        }else if( middleSkip > 0 ){
            middleSkip--;
        }else{
            System.out.println( curr.item.toString() );
            endPrint--;
        }
        if( curr.right != null ){
            rightTraversed = printTree( curr.right, startPrint, middleSkip, endPrint );
        }

        return 1 + leftTraversed + rightTraversed;
    }
}
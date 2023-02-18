//==============================================================
// A5Q1.java
//
// COMP 2140 FALL 2019
// ASSIGNMENT 5 Question 1
// Updated by: <your name goes here>
//
// PURPOSE: Store books (author-and-title pairs) in a BST
//          ordered by author.
//          Handle the following commands: insert book, delete book,
//          search by author (return a string containing ALL books
//          by the author) and search by title (return a string
//          containing all books with that title).
//
//==============================================================

import java.util.Scanner;
import java.io.*;


//==========================================================
// A5Q1 class (application class)
//==========================================================

public class A5Q1 {

    /*******************************************************************
     * main
     *
     * PURPOSE: Print out "bookend" messages and call the method that
     *          does all the work.
     *
     ******************************************************************/
    public static void main( String[] args ) {

	System.out.println( "COMP 2140 Fall 2019 Assignment 5 "
			    + "Question 1" );

	processCommands();

	System.out.println( "\nProgram ends normally." );

    } // end main

    /*******************************************************************
     * processCommands
     *
     * PURPOSE: Prompt the user for the input file.
     *          Then read in and process all commands until
     *          end of file.
     *
     ******************************************************************/
    private static void processCommands() {

	// For reading in the file name (using keyboard input)
	Scanner keyboard;
	String fileName;
	BufferedReader inputFile;
	String inputLine;

	BookTree myBooks = new BookTree();

	// Allow user to choose file with keyboard input.
	keyboard = new Scanner( System.in );
	System.out.println( "\nEnter the input file name "
			    + "(.txt files only): " );
	fileName = keyboard.nextLine();

	System.out.println( "Processing file " + fileName + "...." );

	try{
	    inputFile = new BufferedReader( new FileReader(fileName) );
	    inputLine = inputFile.readLine();

	    while ( inputLine != null ) {
		System.out.println( "===== Processing command line \""
				    + inputLine + "\" =====" );
		processOneCommand( myBooks, inputLine, inputFile );
		inputLine = inputFile.readLine();
	    } // end while

	} catch ( IOException e ) {
	    System.out.println( "*** ERROR: Problems reading file: "
				+ fileName );
	} // end try-catch

    } // end processCommands

    /*******************************************************************
     * processOneCommand
     *
     * PURPOSE: Given a command line, figure out which command it is,
     *          read in any other information the command needs,
     *          and then execute the command.
     *
     * PARAMETERS:
     *    myBooks: The table containing all the books.
     *    commandLine: The input line containing the command (a single letter).
     *    inputFile: The input file that we just read commandLine from.
     *
     ******************************************************************/
    private static void processOneCommand( BookTree myBooks,
					   String commandLine,
					   BufferedReader inputFile
					   ) {

	String secondLine, thirdLine; // Commands use 1 or 2 more input lines.

	// Read in the first parameter for the command (author or title,
	// depending on the command).
	secondLine = null;
	try{
	    secondLine = inputFile.readLine();
	} catch ( IOException e ) {
	    System.out.println( "*** ERROR: Problems reading input file" );
	} // end try-catch

	// Figure out which command it is.
	if ( commandLine.equals( "I" ) || commandLine.equals( "D" ) ) {
	    // Insert a new book or delete an existing book.
	    // Both commands use two more lines of input.
	    // secondLine contains the author.
	    // read in a third line of input (the title).
	    thirdLine = null;
	    try{
		thirdLine = inputFile.readLine();
	    } catch ( IOException e ) {
		System.out.println( "*** ERROR: Problems reading "
				    + "the book title in an "
				    + "insert or delete command." );
	    } // end try-catch
	    System.out.println( "  Author: " + secondLine );
	    System.out.println( "  Title: " + thirdLine );
	    if ( commandLine.equals( "I" ) ) {
		// Insert a new book
		myBooks.insert( new Book( secondLine, thirdLine ) );
	    } else {
		// Delete an existing book
		myBooks.delete( secondLine, thirdLine );
	    } // end if-else
	} else if ( commandLine.equals( "A" ) ) {
	    // Search for all books by a particular author.
	    // secondLine contains the author.
	    System.out.println( "  Author: " + secondLine );
	    System.out.println( "Books by " + secondLine + ":\n"
			       + myBooks.searchByAuthor( secondLine ) );
	} else if ( commandLine.equals( "T" ) ) {
	    // Search for all books with a particular title.
	    // secondLine contains the title.
	    System.out.println( "  Title: " + secondLine );
	    System.out.println( "Books with title \"" + secondLine
				+ "\":\n"
				+ myBooks.searchByTitle( secondLine ) );
	} else {
	    // Invalid command
	    System.out.println( "*** ERROR: Invalid command \""
				+ commandLine + "\" on input line \""
				+ commandLine + "\"" );
	} // end if-else-if-else-if-else-else

    } // end processOneCommand

} // end class A5Q1


//==========================================================
// Book class
//==========================================================

class Book {
    private String author;
    private String title;

    public Book( String a, String t ) {
	author = a;
	title = t;
    } // end Book constructor

    public String getAuthor() {
	return author;
    } // end getAuthor

    public String getTitle() {
	return title;
    } // end getTitle

    public String toString() {
	return "\"" + title + "\" by " + author;
    } // end toString

} // end class Book


//==============================================================
// BookTree class
//
// A BST of books, ordered by author.
//==============================================================
class BookTree {

    //==========================================================
    // BSTNode class
    //==========================================================

    private class BSTNode {
	Book item;
	BSTNode left;
	BSTNode right;

	// Create a leaf.
	public BSTNode ( Book newItem ) {
	    item = newItem;
	    left = null;
	    right = null;
	} // end BSTNode constructor

    } // end class BSTNode

    //============== BookTree class continued ================

    private BSTNode root; // Pointer to the root of the tree

    // Create an empty binary search tree.
    public BookTree() {
	root = null;
    } // end BookTree constructor

    /*******************************************************************
     * isEmpty
     *
     * PURPOSE: Return true if the calling BookTree is empty.
     *          Otherwise, if the tree is NOT empty, return false.
     *
     ******************************************************************/
    public boolean isEmpty() {
	boolean result = false;
	if ( root == null )
	    result = true;
	return result;
    } // end isEmpty

    /*******************************************************************
     * insert
     *
     * PURPOSE: Recursively insert a new book in the calling BookTree.
     *          If the book is a duplicate of another book already
     *          in the tree, print an error message and don't insert.
     *
     * This is the public driver method for a recursive insert.
     * Its job is to handle an insert into an empty tree, or,
     * if the tree is not empty, to pass the work to the recursive
     * helper method (insertSomewhereUnderneath).
     *
     ******************************************************************/
    public void insert( ) {
	if ( root == null ) {
	    root = new BSTNode( newItem );
	} else {
	    insertSomewhereUnderneath( root, newItem );
	} // end if-else
    } // end insert

    /*******************************************************************
     * insertSomewhereUnderneath
     *
     * PURPOSE: To insert a new book beneath node fred somewhere.
     *          If the book is a duplicate of another book already
     *          in the tree, print an error message and don't insert.
     *
     * IDEA: The new book belongs somewhere under fred (should be a
     *       descendant of fred).  The tree is ordered by author,
     *       so compare the authors of the new book and fred's book
     *       to decide if the new book belongs in the left or the
     *       right child subtree of fred.
     *       If fred doesn't have the appropriate child, insert the
     *       new book as the appropriate child of fred.
     *       If fred does have the appropriate child, pass the
     *       insertion to the appropriate child.
     *
     *       Note: If fred's author and the new book's author
     *       are the same, there are two cases:
     *       (1) If the titles are also the same, then new book
     *           is a duplicate.  Print an error message and
     *           don't insert.
     *       (2) If the titles are different, then new book is
     *           not a duplicate of fred's book. New book should
     *           be inserted somewhere in fred's right child,
     *           unless it is a duplicate of some book already
     *           in fred's right child.
     *
     ******************************************************************/
    public void insertSomewhereUnderneath( BSTNode fred, Book newItem ) {
	int authorComparison
	    = newItem.getAuthor().compareTo(fred.item.getAuthor());

	if ( authorComparison < 0 ) {
	    // newItem belongs somewhere in fred's left child subtree
	    if ( fred.left == null ) {
		// newItem should be fred's left child
		fred.left = new BSTNode( newItem );
	    } else {
		// fred already has a left child --- let it
		// deal with the insertion!
		insertSomewhereUnderneath( fred.left, newItem );
	    } // end if-else
	} else if ( authorComparison > 0 ||
		    ( authorComparison == 0
		      && ! newItem.getTitle().equals(fred.item.getTitle())
		    )
		  ) {
	    // newItem's author is AFTER fred's author OR
	    // the two authors are the same, but the titles are different.
	    // So newItem belongs somewhere in fred's right child subtree.
	    if ( fred.right != null ) {
		// newItem should be fred's right child
		fred.right = new BSTNode( newItem );
	    } else {
		// fred already has a right child --- let it
		// deal with the insertion!
		insertSomewhereUnderneath( fred.right, newItem );
	    } // end if-else
	} else { // newItem and fred's item are exactly the same!
	    System.out.println( "*** ERROR: Attempt to insert a book "
				+ "that is already in the BST: "
				+ newItem );
	} // end if-else-if-else

    } // end insertSomewhereUnderneath

    /*******************************************************************
     * searchByAuthor
     *
     * PURPOSE: Return a String containing all books by searchAuthor.
     *
     * Idea: Because there can be multiple books by the same author
     *       and the tree is ordered on the author,
     *       we have to continue searching even after we find a
     *       match because there might be more matches yet to be found.
     *
     * Algorithm:
     *       Do an ordinary BST search, but when we find a match,
     *       simply add the book's title to the output string and
     *       continue the search in the right child.
     *
     ******************************************************************/
    public void searchByAuthor( String searchAuthor, String matchingBooks ) {
	BSTNode curr = root;
	boolean firstItem = true;

	matchingBooks = "";
	while ( curr != null ) {
	    if ( searchAuthor == curr.item.getAuthor() ) {
		if ( !firstItem ) {
		    // every book after the first one on a new line
		    matchingBooks += "\n";
		} else {
		    firstItem = false;
		}
		matchingBooks += "" + curr.item;
		curr = curr.right;
	    } else if ( searchAuthor < curr.item.getAuthor() ) {
		curr = curr.left;
	    } else {
		curr = curr.right;
	    } // end if-else-if-else
	} // end while

    } // end searchByAuthor

    /*******************************************************************
     * searchByTitle --- the public driver method
     *
     * PURPOSE: Returns a String containing all books with
     *          title searchTitle in the whole tree.
     *
     * Idea: Because there can be multiple books with the same title
     *       and the tree is ordered on the author,
     *       you have to use a traversal to find all the books
     *       with the given title.
     *
     * Algorithm:
     *       An inorder traversal, so that the books with the same
     *       title are gathered in order by author.
     *
     ******************************************************************/
    public String searchByTitle( String searchTitle ) {
	String matchingBooks = "";
	if ( root != null ) {
	    searchByTitle( root, searchTitle );
	} // end if
	return matchingBooks;
    } // end searchTitle public driver method

    /*******************************************************************
     * searchByTitle --- the recursive helper method
     *
     * PURPOSE: Returns a String containing all books  with
     *          title searchTitle in the subtree rooted at
     *          node curr.
     *
     * Algorithm:
     *       Perform an inorder traversal, so that the books with
     *       the same title are gathered in order by author.
     *       When curr is "visited", check if its title is the
     *       desired title and add curr's book to the output string
     *       if it is the desired title.
     *
     ******************************************************************/
    public String searchByTitle( BSTNode curr, String searchTitle ) {
	String matchingBooks = "";
	String fromRight;

	matchingBooks = searchByTitle( curr.left, searchTitle );

	// Visit curr
	if ( curr.item.getTitle().equals( searchTitle ) ) {
	    if ( !matchingBooks.equals( "" ) ) {
		matchingBooks += "\n";
	    } // end if
	    matchingBooks += curr.item;
	} // end if

	fromRight = searchByTitle( curr.right, searchTitle );
	if ( !matchingBooks.equals( "" ) && !fromRight.equals( "" ) ) {
	    matchingBooks += "\n";
	} // end if
	matchingBooks += fromRight;

	return matchingBooks;
    } // end searchByTitle

    /*******************************************************************
     * delete --- the public driver method
     *
     * PURPOSE: Handle the deletion when the BST is empty (do nothing).
     *          Otherwise, if the tree is NOT empty, the work is given
     *          to the recursive helper method to do.
     *          The recursive method returns a pointer to the node
     *          that root should point at after the deletion is over.
     *
     ******************************************************************/
    public void delete( String delAuthor, String delTitle ) {
	BSTNode curr = root;
	if ( curr != null ) {
	    root = deleteReplace( curr, delAuthor, delTitle );
	} // end if
    } // end delete --- the public driver method

    /*******************************************************************
     * deleteReplace
     *
     * PURPOSE: Delete the book with author delAuthor and title
     *          delTitle from the subtree rooted at curr and
     *          return a pointer to the node that curr's parent
     *          should point at after the deletion is over.
     *
     * IDEA:
     *    Each successive recursive call is working at the next node
     *    on the search path from the root down to the node containing
     *    the book to be deleted.
     *    Once we find out which node actually needs to be deleted
     *    --- call it delNode ---, we can delete delNode by returning
     *    to its parent (in the previous call) whatever node should
     *    replace delNode.
     *    The previous call will point the parent's child pointer at the
     *    replacement instead of at delNode.
     *    Of course, for most calls, the node that the call is at is NOT
     *    the node to be deleted.  So the call returns the node it is at
     *    to the previous call, and the previous call will point the
     *    parent's child pointer at the node the parent was already
     *    pointing at --- no change is made.
     *
     ******************************************************************/
    private BSTNode deleteReplace( BSTNode curr, String delAuthor,
				   String delTitle ) {
	BSTNode currReplacement = curr; // Usually, curr is not the node
	                                // to delete so curr's parent
	                                // should still point at curr.
	BSTNode inorderSucc;

	//	if ( delAuthor.equals( curr.item.getAuthor() ) ) {
	    // curr contains a book by delAuthor.
	    // Does it have the right title?
	    if ( delTitle.equals( curr.item.getTitle() ) ) {
		// curr contains the book to be deleted
		if ( curr.left == null ) {
		    // curr is delNode, the node that actually needs
		    // to be deleted.  This is an easy case because
		  // curr has at most one child (the right child).
		    // Replace curr with its right child by simply

		      // returning to curr's parent the right child
		    // of curr as the replacement for curr.
		    currReplacement = curr.right;
		    //		} else if ( curr.right == null ) {
		  // curr is delNode, the node that actually needs
 // to be deleted.  This is an easy case because
// curr has exactly one child (the left child).
// Replace curr with its left child by simply
     // returning to curr's parent the left child
// of curr as the replacement for curr.
   currReplacement = curr.left;
} else {
		              // curr has two children: Use curr's inorder
		    // successor. The inorder successor will be delNode,
// the node that must be deleted.
		    // deleteSuccessor takes care of finding and
	  // deleting the inorder successor, and returns
	   // a pointer to it.  All that remains to be done
	 // is to overwrite curr's item (which we want to
 // delete) with the inorder successor's item.
 inorderSucc = deleteSuccessor( curr, curr.right );
      curr.item = inorderSucc.item;
      } // end if-else-if-else
      } else {
      // curr's book is by delAuthor, but the title is not
      // delTitle.
      // The book by delAuthor and with title delTitle can
      // only be in somewhere in the right child subtree.
      if ( curr.right != null ) {
      curr.right = deleteReplace( curr.right,
      delAuthor, delTitle );
      } // else this book isn't in the tree: do nothing!
      } // end if-else
      } else {
     // curr's book is NOT by delAuthor
      if ( delAuthor.compareTo( curr.item.getAuthor() ) < 0 ) {
      if ( curr.left != null ) {
      curr.left = deleteReplace( curr.left,
					       delAuthor, delTitle );
		} // else this book isn't in the tree: do nothing!
	    } else {
		if ( curr.right != null ) {
		    curr.right = deleteReplace( curr.right,
						delAuthor, delTitle );
		} // else this book isn't in the tree: do nothing!
	    } // end if-else
	} // end if-else

	return currReplacement;
    } // end deleteReplace --- the recursive helper method

    /*******************************************************************
     * deleteSuccessor
     *
     * PURPOSE: Delete and return a pointer to the inorder successor
     *          of the node (call it x) that deleteReplace found
     *          (x contains the book that the user wants to delete,
     *          but x has two children).
     *
     * IDEA:
     * Recursively move down to the inorder successor: move to the left
     * child until curr doesn't have a left child.  Then curr is
     * the inorder successor.
     * At that time, delete the inorder successor by pointing its parent
     * at the successor's right child and return a pointer to the
     * inorder successor.
     *
     ******************************************************************/
    private BSTNode deleteSuccessor( BSTNode currParent, BSTNode curr ) {
	BSTNode inorderSucc = null;

	// curr is the inorder successor; delete it and return it.
	if ( curr == currParent.left )
	    currParent.left = curr.right;
	else
	    currParent.right = curr.right;
	inorderSucc = curr;

	return inorderSucc;
    } // end deleteSuccessor

} // end class BookTree

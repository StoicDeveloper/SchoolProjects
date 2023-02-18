
/***************************************************
 *
 * COMP 2140 Lab 5; Week of November 18, 2019
 *
 * Rebalancing a binary search tree.
 *
 ***************************************************/

import java.io.*;
import java.util.*;
import javax.swing.*;

public class Lab05 {

    //  Construct and rebalance three different BSTs.
    public static void main( String args[] ) {
	BST t;
	Random generator = new Random();

	System.out.println( "\nCOMP 2140 Lab 5: Rebalancing binary search trees" );
	System.out.println();

	// First tree

	t = new BST();
	t.insert( 1 );
	t.insert( 2 );
	t.insert( 3 );
	t.insert( 4 );
	t.insert( 5 );

	testOneTree( t, "T1",
		     "BST T1 is a linked list (every internal node "
		     + "has only a right child)." );

	System.out.println();

	// Second tree
	t = new BST();
	t.insert( 10 );
	t.insert( 9 );
	t.insert( 8 );
	t.insert( 7 );
	t.insert( 6 );
	t.insert( 5 );
	t.insert( 4 );
	t.insert( 3 );
	t.insert( 2 );
	t.insert( 1 );
	t.insert( 11 );
	t.insert( 12 );
	t.insert( 13 );
	t.insert( 14 );
	t.insert( 15 );
	t.insert( 16 );

	testOneTree( t, "T2",
		     "The root of BST T2 has two linked-list children." );

	System.out.println();

	// Third tree

	t = new BST();
	for ( int i = 0; i < 1000; i++ ) {
	    t.insert( generator.nextInt( 50000 ) );
	}

	testOneTree( t, "T3",
		     "BST T3 contains no more than 1000 random integers "
		     + "between 0 and 50,000." );

	System.out.println("\n\n");

	System.out.println( "BST rebalancing program ended normally.\n" );
    } // end main

    // Test an already-constructed tree t:
    //   - print the size and average depth of the original tree
    //   - then rebalance the tree
    //   - print the size and average depth of the rebalanced tree
    //   - test to make
    private static void testOneTree( BST t, String treeName, String treeDescription ) {

	int beforeSize, afterSize;
	float beforeAvgDepth, afterAvgDepth;

	System.out.println( "******************\n\n" + treeDescription );

	beforeSize = t.size();
	System.out.println( "BST " + treeName + " contains " + beforeSize + " nodes." );

	beforeAvgDepth = t.averageDepth();
	System.out.format( "Average depth of BST " + treeName
			   + " BEFORE rebalancing: %7.2f%n%n",
			   beforeAvgDepth );

    System.out.println( "Rebalancing" );

	t.rebalance();

    System.out.println( "Counting rebalanced size" );

	afterSize = t.size();
	System.out.println( "Rebalanced T1 contains " + afterSize + " nodes." );
	afterAvgDepth = t.averageDepth();
	System.out.format( "Average depth of BST " + treeName
			   + " AFTER rebalancing: %7.2f%n",
			   afterAvgDepth );

	if ( afterSize != beforeSize )
	    System.out.println( "\n\n**** ERROR **** Size changed during "
				+ "the rebalancing.\n" );
	else
	    System.out.println( "  Excellent: Size unchanged by rebalancing." );

	if ( !t.satisfiesBSTProperty() )
	    System.out.println( "\n\n**** ERROR **** Rebalanced tree does not satifsy "
				+ " the binary search tree property.\n" );
	else
	    System.out.println( "  Excellent: Rebalanced tree satisfies "
				+ "the binary search tree property." );

	if ( afterAvgDepth > beforeAvgDepth )
	    System.out.println( "\n\n**** ERROR ****  Rebalancing the tree "
				+ "increased the average depth.\n" );
	else
	    System.out.println( "  Excellent: Rebalancing either decreased "
				+ " or didn't change the average depth." );

    } // end testOneTree

} // end class Lab05

/**********************************************************************/
/*  BST --- Binary Search Tree                                        */
/**********************************************************************/

class BST {

    /**********************************************************************/
    /* BSTNode --- a BST node                                                */
    /**********************************************************************/

    private class BSTNode {
	public int item;
	public BSTNode left;
	public BSTNode right;

	public BSTNode( int val ) { // create a leaf (no children)
	    item = val;
	    left = null;
	    right = null;
	} // end BSTNode constructor

    } // end class BSTNode

    /**********************************************************************/
    // BST class continued!
    /**********************************************************************/

    private BSTNode root;

    public BST() { // create an empty tree
	root = null;
    } // end BST constructor

    // insert:  An non-recursive insert with no duplicates allowed.
    //          It silently does nothing if newItem is a duplicate.
    public void insert( int newItem ) {
	BSTNode currParent, curr;
	BSTNode newNode;

	if ( root == null ) { // insert into empty tree

	    root = new BSTNode( newItem );

	} else { // there's at least a root node in the tree

	    // First search for the new item
	    currParent = null;
	    curr = root;
	    while ( ( curr != null ) && ( curr.item != newItem ) ) {
		currParent = curr;
		if ( newItem < curr.item )
		    curr = curr.left;
		else
		    curr = curr.right;
	    } // end while

	    // Second, insert if the new item wasn't found
	    if ( curr == null ) {
		// currParent should be the parent of the new node
		newNode = new BSTNode( newItem );
		if ( newItem < currParent.item )
		    currParent.left = newNode;
		else
		    currParent.right = newNode;
	    } // end if ( curr == null )


	} // end else root != null

    } // end insert

    // averageDepth: Returns the average depth of the nodes in the BST.
    //
    // Recall: depth of a node = length of path from the node to the root,
    //         where length of path = number of nodes on the path, including
    //         the node itself and the root.
    public float averageDepth() {
	float result = 0.0f;

	if ( root != null )
	    result = (1.0f * sumDepths( root, 1 ) ) / ( 1.0f * countNodes( root ) );

	return result;
    } // end averageDepth

    //  sumDepths: Use a preorder traversal to add up
    //             (and return) the depths of curr and
    //             all its descendants.
    //             (Used by averageDepth() to
    //             compute the average depth
    //             of nodes in a BST.)
    //
    // PARAMETER: curr: The node that we're currently at in the preorder traversal.
    // PARAMETER: currDepth: The depth of curr in the overall BST.
    public int sumDepths( BSTNode curr, int currDepth ) {
	int result;
	int currChildDepth = currDepth + 1; // A child is one node further from the root
	                                    // than its parent.

	// Visit curr
	result = currDepth;

	if ( curr.left != null )
	    result += sumDepths( curr.left, currChildDepth );

	if ( curr.right != null )
	    result += sumDepths( curr.right, currChildDepth );

	return result;
    } // end sumDepths


    // size: Returns the number of nodes in the BST.
    public int size() {
	int nodeCount = 0;

	if ( root != null )
	    nodeCount = countNodes( root );

	return nodeCount;
    } // end size

    // countNodes: returns the number of nodes in the
    //             subtree rooted at BSTNode curr
    //             using a preorder traversal.
    //             That is, count curr and
    //             all its descendants.
    private int countNodes( BSTNode curr ) {
	int result = 1; // Count curr itself.

	// Add in nodes in the left child (if there is a left child)
	if ( curr.left != null )
	    result += countNodes( curr.left );

	// Add in nodes in the right child (if there is a right child)
	if ( curr.right != null )
	    result += countNodes( curr.right );

	return result;
    } // end countNodes

    // satisfiesBSTProperty: Returns true if every node
    //    in the BST satisfies the BST property; otherwise,
    //    it returns false.
    // BST property: node n satisfies the BST property if
    // the values in its left child
    //     <
    // the value in node n
    //     <
    // the values in its right child.
    public boolean satisfiesBSTProperty() {
	boolean allOK = true;

	if ( root != null )
	    allOK = satisfiesBSTProperty( root );

	return allOK;
    } // end satisfiesBSTProperty (public driver method)



    // satisfiesBSTProperty:
    //    Returns true if curr and all its descendants satisfy the BST property.
    //    Otherwise, it returns false.
    //
    // IDEA: It does a post-order traversal to test every node in the
    //       subtree rooted at curr.
    private boolean satisfiesBSTProperty( BSTNode curr ) {
	boolean allOK = true;
	BSTNode maxInLeftChild = null, minInRightChild = null;

	// Check the left child subtree
	if ( curr.left != null ) {

	    allOK = satisfiesBSTProperty( curr.left );

	    // Find the left child's largest value for checking curr itself.
	    maxInLeftChild = curr.left;
	    while ( maxInLeftChild.right != null ) {
		maxInLeftChild = maxInLeftChild.right;
	    } // end while
	} // end if

	// Check the right child subtree (don't bother if we found a problem already)
	if ( allOK && curr.right != null ) {

	    allOK = satisfiesBSTProperty( curr.right );

	    // Find the right child's smallest value for checking curr itself.
	    minInRightChild = curr.right;
	    while ( minInRightChild.left != null ) {
		minInRightChild = minInRightChild.left;
	    } // end while
	} // end if

	// Visit curr
	if ( allOK ) {  // Don't bother if we've already found a problem!

      	    // Now make sure curr satisfies the BST property:
	    // If there's a left child, is the left subtree's largest value < curr.item?
	    // If there's a right child, is curr.item < the right subtree's
	    // smallest value?
	    allOK =
		( maxInLeftChild == null || maxInLeftChild.item < curr.item )
		&&
		( minInRightChild == null || curr.item < minInRightChild.item );
	} // end if

	return allOK;
    } // end satisfiesBSTProperty (recursive helper method)

    // fillArray:
    //
    // PURPOSE: This recursive BST method points the entries of nodeArray
    //          at a tree's nodes using an inorder traversal.
    //          In the end, the entries of nodeArray will point
    //          to the nodes in the tree in sorted order.
    //
    // PARAMETERS: "curr" is the node we're currently at in the inorder traversal.
    //             "nextFreeIndex" is the next available position in nodeArray.
    //
    // IDEA: This method's job is to make the next sequence of available
    //       array entries point at curr and all of curr's
    //       descendants in sorted order by data values stored in the nodes.
    //
    // RETURNS: It returns the next available array position after
    //          curr and its descendants have been added to the array.
    public int fillArray( BSTNode curr, BSTNode[] nodeArray, int nextFreeIndex ) {
	int index = nextFreeIndex;

	if ( curr.left != null )
	    index = fillArray( curr.left, nodeArray, index );

	// Visit curr
	nodeArray[index] = curr;
	index++;

	if ( curr.right != null )
	    index = fillArray( curr.right, nodeArray, index );

	return index;
    } // end fillArray


    // rebalance (with NO parameters):
    //     calls your method to rebalance the calling BST.
    //     - first, creates and fills the nodeArray
    //       that your method uses
    public void rebalance(  ) {
	BSTNode[] nodeArray;
	int ignore; // We don't need the value fillArray returns (it's needed internally)

	if ( root != null ) {
	    nodeArray = new BSTNode[ countNodes( root ) ];
	    ignore = fillArray( root, nodeArray, 0 );
	    root = rebalance( nodeArray, 0, nodeArray.length );
	} // end if
    } // end rebalance (the public driver method)

    // rebalance (with 3 parameters):
    //  - a recursive method that reconnects into a short BST all the
    //    nodes stored in the node array in positions start to end-1
    //    (i.e., NOT including position end).
    //  - returns a pointer to the root of the short BST it builds
    //  Parameter nodeArray: contains pointers to BSTNodes
    //    in sorted order (sorted by data values stored in the nodes)
    //  Parameters start and end: array indices telling the method
    //    what array entries to build a short BST out of:
    //    connect the nodes in nodeArray[start] to nodeArray[end-1]
    //    into a short BST.
    //    WARNING: nodeArray[end] is NOT included
    private BSTNode rebalance( BSTNode[] nodeArray, int start, int end ) {

        BSTNode returnNode;
        if( end - start <= 0 ){
            returnNode = null;
        }else if( end - start == 1 ){
            returnNode = nodeArray[ start ];
            returnNode.left = null;
            returnNode.right = null;
        }else{
            int mid = start + (end-1 - start)/2;

            returnNode = nodeArray[ mid ];
            returnNode.left = rebalance( nodeArray, start, mid );
            returnNode.right = rebalance( nodeArray, mid+1, end );
        }

        return returnNode;

    } // end rebalance (the recursive helper method)

} // end class BST

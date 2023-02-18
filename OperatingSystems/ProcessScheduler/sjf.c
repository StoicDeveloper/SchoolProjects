// NAME: Xian Mardiros
// PURPOSE: implementation of SJF policy
// REMARKS: its a tree

#include <stddef.h>
#include <stdlib.h>
#include "sjf.h"

// declarations
Sjf* createSjf();
Node *newNode(void *, int);
void insert(Sjf *, int, void *);
void insertFrom(Node *, int, void *);
void* next(Sjf *);
void* remove_shortest_item(Tree *);
Node *remove_shortest_node(Node **);
void destroy(Sjf *);

// SJF structures
struct TREE
{
	struct NODE *head;
};

struct NODE
{
	struct NODE *right;
	struct NODE *left;
	void *item;
	int num;
};

struct SJF
{
	struct TREE *tree;
};

// functions

// create the SJF
// purpose: perform necessary allocations
Sjf* createSjf()
{
	Sjf *sjf = malloc(sizeof(Sjf));
	sjf->tree = malloc(sizeof(Tree));
	sjf->tree->head = NULL;
	return sjf;
}

// make a new node containing the item
// remarks: the node positions will be later determined by the num argument
Node *newNode(void *item, int num)
{
	Node *newNode = malloc(sizeof(Node));
	newNode->item = item;
	newNode->num = num;
	newNode->left = NULL;
	newNode->right = NULL;
	return newNode;
}

// insert into sjf tree
// purpose: calls the recursive function from the root of the tree, passing the item and num
void insert(Sjf *sjf, int num, void *item)
{
	Tree *tree = sjf->tree;
	if(tree->head)
	{
		insertFrom(tree->head, num, item);
	}else
	{
		tree->head = newNode(item, num);
	}
}

// recursively insert from here
// purpose: starting from the node argument, navigates the tree using num to eventually create
// 					a node containing item, and placing that node in the correct position
void insertFrom(Node *node, int num, void *item)
{
	if(num <= node->num)
	{
		if(node->left)
		{
			insertFrom(node->left, num, item);
		}else
		{
			node->left = newNode(item, num);
		}
	}else
	{
		if(node->right)
		{
			insertFrom(node->right, num, item);
		}else
		{
			node->right = newNode(item, num);
		}
	}
}

// get next item from SJF
void* next(Sjf *sjf)
{
	return remove_shortest_item(sjf->tree);
}

// remove and return the smallest item in the tree
void* remove_shortest_item(Tree *tree)
{
	void *item = NULL;
	if(tree->head != NULL)
	{
		Node *node = remove_shortest_node(&tree->head);
		item = node->item;
		free(node);
	}

	return item;
}

// remove and return the smallest item in the subtree rooted at node
// remarks: because this tree only ever removes the shortest element, the search and rearrangement
// 					process is far simpler than most tree algorithms
Node *remove_shortest_node(Node **node)
{
	Node *shortest;
	if((*node)->left)
	{
		// keep going
		shortest = remove_shortest_node(&(*node)->left);
	}else
	{
		// we're there
		shortest = *node;
		if((*node)->right)
		{
			// rearrangement required
			*node = shortest->right;
		}else
		{
			// ideal case, just return the curr node
			*node = NULL;
		}
	}

	return shortest;
}
	
// destroy sjf
// purpose: deallocates the sjf root node, and the sjf structure itself
// remarks: assumes that the tree is empty, if its not, then the nodes won't be deallocated and this
// 					would be a memory leak, this is reasonable for this assignment, since a non-empty sjf
// 					should not be destroyed, but cannot be generalized.
void destroy(Sjf *sjf)
{
	free(sjf->tree);
	free(sjf);
}

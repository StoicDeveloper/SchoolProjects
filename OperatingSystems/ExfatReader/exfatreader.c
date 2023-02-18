//-----------------------
// NAME: Xian Mardiros
// STUDENT NUMBER: 7862786
// COURSE: COMP3430
// INSTRUCTOR: Franklin Bristow
// ASSIGNMENT: 4
//
// REMARKS:  implementation of an exfat volume reader; provides info, list, and get commands
//-----------------------

#include <execinfo.h>
#include <assert.h>
#include <fcntl.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include "debug.h"
#pragma pack(1)

#define INFO "info"
#define LIST "list"
#define GET "get"
#define EXFAT "EXFAT   "
#define FILE_SYSTEM_NAME_OFFSET 3
#define VOLUME_LENGTH_OFFSET 72
#define VOLUME_FLAGS_OFFSET 106
#define VOLUME_LABEL 0x83
#define END_OF_DIRECTORY 0x00
#define END_OF_FILE -1
#define EMPTY 0
#define FILE_ENTRY 0x85
#define DIRECTORY_ENTRY_SIZE 32
#define MAX_FILENAME_SIZE 255
#define MAX_NAME_SEGMENT_IN_ENTRY 15
#define STREAM_EXTENSION_DIRECTORY_ENTRY 0x85
#define MAX_LABEL_LENGTH 11
#define ALLOCATION_BITMAP 0x81
#define DIRECTORY_ATTRIBUTE 0x0010
#define IS_DIRECTORY 1
#define IS_FILE 0
#define FAT_FIRST_INDEX 2


// Struct definitions
typedef struct DIRECTORY_ENTRY
{
	uint8_t entryType;
	uint8_t customDef[19];
	uint32_t firstCluster;
	uint64_t dataLength;
} DirectoryEntry;

typedef struct FILE_DIRECTORY_ENTRY
{
	uint8_t entryType;
	uint8_t stuff[3];
	uint16_t fileAttributes;
	uint8_t stuff2[26];
} FileEntry;

typedef struct STREAM_EXTENSION_ENTRY
{
	uint8_t entryType;
	uint16_t stuff;
	uint8_t nameLength;
	uint8_t stuff2[16];
	uint32_t firstCluster;
	uint64_t dataLength;
} StreamExt;

typedef struct NAME_ENTRY
{
	uint8_t entryType;
	uint8_t flags;
	uint16_t name[MAX_NAME_SEGMENT_IN_ENTRY];
} NameEntry;

typedef struct DIRECTORY_ENTRY_SET
{
	uint8_t type;
	uint32_t firstCluster;
	uint64_t dataLength;
	char *name;
} DirEntrySet;

typedef struct CLUSTER_LIST
{
	struct CLUSTER_NODE *first;
	int size;
} ClusterList;

typedef struct CLUSTER_NODE
{
	struct CLUSTER_NODE *next;
	int index;
	int64_t offset;
} ClusterNode;

typedef struct CLUSTER_ITERATOR
{
	int fd;
	struct CLUSTER_LIST *list;
	struct CLUSTER_NODE *currNode;
	int64_t currOffset; // the offset within the file image
	uint64_t clusterSize;
	uint64_t	index; // the current place in the iterator, must be less than filesize 
	uint64_t fileSize;
} Iterator;

typedef struct MAIN_BOOT_SECTOR
{
	char fileSystemName[9];
	int64_t volumeLength;
	int fatOffset; //sector offset of first FAT
	int fatLength;
	int clusterHeapOffset;
	int clusterCount;
	int firstClusterOfRootDirectory;
	int volumeSerialNumber;
	int volumeFlags;
	int bytesPerSectorShift;
	int sectorsPerClusterShift;
} MainBootSector;

typedef struct FILESYSTEM_IMAGE
{
	int fd;
	struct MAIN_BOOT_SECTOR *mbs;
} Image;

// function declarations

// basic exfat volume analysis
int verifyExfat(MainBootSector *);
MainBootSector *readBootSector(int);
DirectoryEntry *searchDirForDirEntryType(Image, int, int);
int32_t pow2(unsigned int);

// volume info functions
void printImageInfo(Image);
char *getVolumeLabel(Image);
int calcFreeSpace(Image);
int countZeroBits(int8_t *, int, int);

// list functions
void listImageContents(Image);
void listDirContents(Image, int, int);
void printFile(Image, DirEntrySet *, int);

// get file functions
void getFileFromImage(Image, char *);
void recursiveFileFind(Image, DirEntrySet *, char *);
DirEntrySet *searchDirForFile(Image, int, char *);
void output(Image, DirEntrySet *, char *);

// exfat traversal tools
int bytesPerCluster(Image);
void printClusterHex(Image, int, int, int);
void setOffsetToClusterHeapIndex(Image, int);
int64_t getOffsetOfClusterHeapIndex(Image, int);
DirEntrySet *getNextDirEntrySet(Image, Iterator *);
void freeDirEntrySet(DirEntrySet *set);
int min(int, int);
static char *unicode2ascii( uint16_t *, uint8_t);

// cluster chain abstraction
ClusterList *makeClusterList(Image, int);
int recursiveClusterListAppend(Image, ClusterList *, ClusterNode *, int);
Iterator *makeClusterIterator(Image, int, int64_t);
char *iteratorToString(Iterator *);
int iteratorHasBytes(Iterator *, int);
int8_t *getIteratorBytes(Image, Iterator *, int);
int clusterSpaceRemaining(Iterator *);
void freeIterator(Iterator *);


// global string for debug logging
char msg[100];

// function definitions
int main(int argc, char *argv[])
{
	int fd = open(argv[1], O_RDONLY);
	char *cmd = argv[2];
	char *path;

	if(argc != 3 && argc != 4)
	{
		printf("Volume name and command required");
	}

	if(fd == -1)
	{
		printf("Invalid file image\n");
		exit(1);
	}
	MainBootSector *mbs = readBootSector(fd);
	Image image = {fd, mbs};
	
	if(!verifyExfat(mbs))
	{
		printf("Image is not formatted as ExFat\n");
		exit(1);
	}
	if(!strcmp(cmd, INFO))
	{
		debugLog("Executing command: volume info");
		printImageInfo(image);
	}else if(!strcmp(cmd, LIST))
	{
		listImageContents(image);
	}else if(!strcmp(cmd, GET))
	{
		if(argc > 3)
		{
			path = argv[3];
			getFileFromImage(image, path);
		}else
		{
			printf("Filepath required\n");
			exit(1);
		}
	}else
	{
		printf("Invalid exfat reader command\n");
		exit(1);
	}

	close(fd);
	free(image.mbs);
	printf("End of processing\n");

}

//-----------------------------
// Basic exFAT volume analysis
//----------------------------

//-----------------
// verifyExfat
//
// check that the image is in fact in the ExFat format
//-----------------
int verifyExfat(MainBootSector *mbs)
{
	login("verifying exfat");
	int verified = !strcmp(EXFAT, mbs->fileSystemName);
	logout("exfat verification complete");
	return verified;
}

//-----------------
// readBootSector
//
// read the important boot sector data into a struct to be referenced later
//-----------------
MainBootSector *readBootSector(int fd)
{
	login("Reading boot sector\n");
	MainBootSector *data = malloc(sizeof(MainBootSector));

	lseek(fd, FILE_SYSTEM_NAME_OFFSET, SEEK_SET);
	read(fd, data->fileSystemName, 8);
	data->fileSystemName[8] = '\0';

	lseek(fd, VOLUME_LENGTH_OFFSET, SEEK_SET);
	read(fd, &data->volumeLength, 8);
	read(fd, &data->fatOffset, 4);
	read(fd, &data->fatLength, 4);
	read(fd, &data->clusterHeapOffset, 4);
	read(fd, &data->clusterCount, 4);
	read(fd, &data->firstClusterOfRootDirectory, 4);
	read(fd, &data->volumeSerialNumber, 4);

	lseek(fd, VOLUME_FLAGS_OFFSET, SEEK_SET);
	read(fd, &data->volumeFlags, 2);
	read(fd, &data->bytesPerSectorShift, 1);
	data->bytesPerSectorShift = pow2(data->bytesPerSectorShift);
	read(fd, &data->sectorsPerClusterShift, 1);
	data->sectorsPerClusterShift = pow2(data->sectorsPerClusterShift);

	debugLog("Boot Sector:");
	sprintf(msg, "file system name: %s", data->fileSystemName);
	debugLog(msg);
	sprintf(msg, "len: %ld, clusters: %d, root index: %d, serial: %d", data->volumeLength, data->clusterCount, data->firstClusterOfRootDirectory, data->volumeSerialNumber);
	debugLog(msg);
	sprintf(msg, "fatOffset: %d, bytes per sector: %d, sectors per cluster: %d", data->fatOffset, data->bytesPerSectorShift, data->sectorsPerClusterShift);
	debugLog(msg);
	login("done reading boot sector");

	return data;
}

//-----------------
// searchDirForDirEntryType
//
// return the next DirectoryEntry of the specified type in the directory starting at
// clusterIndex
// returns null if an entry of that type does not exist
//-----------------
DirectoryEntry *searchDirForDirEntryType(Image image, int clusterIndex, int type)
{
	login("searching directory for an entry type");
	Iterator *iter = makeClusterIterator(image, clusterIndex, -1);
	DirectoryEntry *curr = (DirectoryEntry*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
	DirectoryEntry *target = NULL;

	while(curr != NULL && curr->entryType != END_OF_DIRECTORY && target == NULL)
	{
		sprintf(msg, "Current entry type: %X", curr->entryType);
		debugLog(msg);

		if(curr->entryType == type)
		{
			debugLog("Entry of desired type found");
			target = curr;
		}else
		{
			free(curr);
			curr = (DirectoryEntry*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
		}
	}
	if(target == NULL)
	{
		debugLog("Target entry type not found");
		free(curr);
	}
	freeIterator(iter);
	logout("done searching");
	return target;
}

// return 2 raised to the power of the function argument
int32_t pow2(unsigned int exp)
{
	return 1 << exp;
}

//----------------------
// Volume Info Functions
//----------------------

//-----------------
// printImageInfo
//
// print out the volume label, serial number, free space, and other useful info
//-----------------
void printImageInfo(Image image)
{
	debugLog("printing image info");
	char *label = getVolumeLabel(image);
	int serial = image.mbs->volumeSerialNumber;
	int space = calcFreeSpace(image);

	printf("Volume %s:\n", label);
	printf("Serial %d\n", serial);
	printf("Free space %d bytes, %d kilobytes\n", space, space / 1024);
	printf("Cluster Heap offset %d, cluster size %d\n", image.mbs->clusterHeapOffset * image.mbs->bytesPerSectorShift, bytesPerCluster(image));
	free(label);
}

//-----------------
// getVolumeLabel
//
// search the volume root directory for the label entry and convert to ascii
//-----------------
char *getVolumeLabel(Image image)
{
	login("Getting volume label");
	int len;
	char *label = NULL;
	DirectoryEntry *labelEntry = searchDirForDirEntryType(image, image.mbs->firstClusterOfRootDirectory, VOLUME_LABEL);

	if(labelEntry != NULL)
	{
		len = labelEntry->customDef[0];
		label = unicode2ascii((uint16_t *)&labelEntry->customDef[1], len);
		free(labelEntry);
	}

	sprintf(msg, "volume label: %s", label);
	logout(msg);
	return label;
}

//-----------------
// calcFreeSpace
//
// get the allocation bitmap; return the count of its 0 bits, multiplied by cluster size
//-----------------
int calcFreeSpace(Image image)
{
	login("calculating free space");
	DirectoryEntry *allocationBitmapEntry = searchDirForDirEntryType(image, image.mbs->firstClusterOfRootDirectory, ALLOCATION_BITMAP);
	Iterator *iter = makeClusterIterator(image, allocationBitmapEntry->firstCluster, allocationBitmapEntry->dataLength);
	int index = 0;
	int count = 0;
	int increment = 8;
	int8_t *bitmap;
	int bitsOverflow;
	while(index < image.mbs->clusterCount)
	{
		assert(increment % 8 == 0);
		bitmap = getIteratorBytes(image, iter, increment/8);
		index += increment;
		bitsOverflow = index - image.mbs->clusterCount;
		count += countZeroBits(bitmap, increment/8,  bitsOverflow > 0 ? bitsOverflow : 0);
		free(bitmap);
	}

	free(iter);
	login("calculated free space");
	return count * bytesPerCluster(image);
}

//-----------------
// countZeroBits
//
// count the number of zero bits in the byte array, from index start to size-1
//-----------------
int countZeroBits(int8_t *bitmap, int size, int start)
{
	*bitmap = *bitmap >> start;
	int count = 0;
	for(int i = 0; i < size * 8 - start; i++)
	{
		count += (~*bitmap) & 1;
		*bitmap = *bitmap >> 1;
	}
	return count;
}

//--------------------------
// List volume contents functions
//--------------------------

//-----------------
// listImageContents
//
// call the recursive listDirContents on the root directory
// results in detailing the complete contents of the entire volume
//-----------------
void listImageContents(Image image)
{
	listDirContents(image, image.mbs->firstClusterOfRootDirectory, 0);
}

//-----------------
// listDirContents
//
// call printFile() on each file in the directory starting at clusterIndex
// 		- printfFile() will then call this function if the file is a directory
// currDepth keeps track of the recursive depth, for displaying the volume
// contents as a tree structure
//-----------------
void listDirContents(Image image, int clusterIndex, int currDepth)
{
	sprintf(msg, "listing contents of directory starting at cluster %d", clusterIndex);
	login(msg);

	// It may seem odd to write it this way, but later, if it is desired to have the final file in a
	// directory have a different symbol pointing to it, then the following printFile can be replaced
	// with printLastFile(), which remains to be implemented, without changing the structure of the 
	// while loop
	Iterator *iter = makeClusterIterator(image, clusterIndex, -1);
	DirEntrySet *prevSet = getNextDirEntrySet(image, iter);
	DirEntrySet *currSet = getNextDirEntrySet(image, iter);
	while(currSet)
	{
		//printClusterHex(image, prevSet->firstCluster, 4, 512);
		printFile(image, prevSet, currDepth);
		freeDirEntrySet(prevSet);
		prevSet = currSet;
		currSet = getNextDirEntrySet(image, iter);
	}
	//printLastFile(image, prevSet, currDepth);
	
	printFile(image, prevSet, currDepth);
	freeDirEntrySet(prevSet);
	freeIterator(iter);

	logout("listed directory contents");
}

// deallocate all of the memory associated with the DirEntrySet
void freeDirEntrySet(DirEntrySet *set)
{
	free(set->name);
	free(set);
}

//-----------------
// printFile
//
// print the file name, and if it is a directory, all of its contents by
// recursively calling listDirContents
//-----------------
void printFile(Image image, DirEntrySet *set, int currDepth)
{
	login("printing file");
	if(set->type == IS_FILE)
	{
		for(int i = 0; i < currDepth; i++)
		{
			printf("| ");
		}
		printf("|-%s\n", set->name);
	}else if(set->type == IS_DIRECTORY)
	{
		for(int i = 0; i < currDepth; i++)
		{
			printf("| ");
		}
		printf("|-%s\n", set->name);
		listDirContents(image, set->firstCluster, currDepth + 1);
	}
	logout("printed file");
} 

// later on, can be used to print the last file with a different tree symbol
// though also, it might be better to just add an addition "int isLeaf" flag to printFile()
// which will indicate whether the file is a leaf in the tree, and decide the symbol based on that
//void printLastFile(prevSet, currDepth)
//{
//}

//----------------------
// get file functions
//----------------------

//-----------------
// getFileFromImage
//
// break the provided path into directory and file pieces, recursively searching
// each named directory (starting here with the root) for the next 
// directory/file
//-----------------
void getFileFromImage(Image image, char *path)
{
	sprintf(msg, "getting %s from image", path);
	login(msg);

	char *name = strtok(path, "/");
	DirEntrySet *root = malloc(sizeof(DirEntrySet));

	root->firstCluster = image.mbs->firstClusterOfRootDirectory;
	root->name = "root";
	recursiveFileFind(image, root, name);
	free(root);

	logout("search complete");
}

//-----------------
// recursiveFileFind
//
// get the next token of the provided path, representing the file/directory 
// name to search for, and search the provided DirEntrySet for it, that set 
// itself having been obtained from a previous iteration of this function 
// (except for the first iteration)
//
// Four cases:
// 		Case 1: The path provided does not refer to a file in the 
// 						directories specified
// 		Case 2: The path is incorrect, and does not refer to a chain of 
// 						directories that exist in the volume
// 		Case 3: The next directory is found, and there are more to look for
// 						This is the recursive case
// 		Case 4: The file has been found in this directory
// 						call output() to print the data to a file
//-----------------
void recursiveFileFind(Image image, DirEntrySet *file, char *pathTok)
{
	
	sprintf(msg, "recursively searching %s for file %s", file->name, pathTok);
	login(msg);
	assert(file != NULL && pathTok != NULL);

	int firstCluster = file->firstCluster;
	DirEntrySet *nextFile = searchDirForFile(image, firstCluster, pathTok);
	char *name = strtok(NULL, "/");

	if(name == NULL && (nextFile == NULL || nextFile->type == IS_DIRECTORY))
	{
		// the end of path has been reached, and the target was not found, exit recursion
		printf("File does not exist\n");
	}else if(name == NULL)
	{
		// the end of path has been reached, and the target file is found, exit recursion
		output(image, nextFile, pathTok);
	}else if(name != NULL && (nextFile == NULL || nextFile->type == IS_FILE))
	{
		// the path provided was invalid, exit recursion
		printf("Invalid path\n");
	}else if(name != NULL)
	{
		// a valid directory was found at this path midpoint, continue recursion
		recursiveFileFind(image, nextFile, name);
	}else
	{
		// invalid state
		printf("function recursiveFileFind has reached an invalid state\n");
	}

	if(nextFile != NULL)
	{
		freeDirEntrySet(nextFile);
	}
	logout("end recursive search");
}

//-----------------
// searchDirForFile
//
// search the directory starting at clusterIndex for the specified file
// iterates over all the DirEntrySets in the directory, parsing their names
//-----------------
DirEntrySet *searchDirForFile(Image image, int clusterIndex, char *name)
{
	sprintf(msg, "searching dir starting at cluster %d for file %s", clusterIndex, name);
	login(msg);

	Iterator *iter = makeClusterIterator(image, clusterIndex, -1);
	DirEntrySet *currSet = getNextDirEntrySet(image, iter);
	DirEntrySet *target = NULL;

	while(!target && currSet)
	{
		if(!strcmp(currSet->name, name))
		{
			debugLog("target found");
			target = currSet;
		}else{
			free(currSet->name);
			free(currSet);
			currSet = getNextDirEntrySet(image, iter);
		}
	}

	freeIterator(iter);
	logout("search complete");
	return target;
}

//-----------------
// output
//
// write the data contained in the specified in file to a file of the same name
//-----------------
void output(Image image, DirEntrySet *file, char *filename)
{
	FILE *stream = fopen(filename, "w");
	Iterator *iter = makeClusterIterator(image, file->firstCluster, file->dataLength);
	int8_t *data = getIteratorBytes(image, iter, file->dataLength);

	fwrite(data, file->dataLength, 1, stream);
	fclose(stream);

	freeIterator(iter);
	free(data);
}

//--------------------
// exFAT traversal tools
//--------------------

//-----------------
// bytesPerCluster
//
// this could probably just be a field in image.mbs
//-----------------
int bytesPerCluster(Image image)
{
	return image.mbs->sectorsPerClusterShift * image.mbs->bytesPerSectorShift;
}

//-----------------
// printClusterHex
//
// Print the bytes of the specified cluster, lineLen bytes per line, up to maxBytes
//-----------------
void printClusterHex(Image image, int clusterIndex, int lineLen, int maxBytes)
{
	printf("cluster %d\n", clusterIndex);
	setOffsetToClusterHeapIndex(image, clusterIndex);
	unsigned int byte = 0;
	for(int num = 0; num < maxBytes; num++)
	{
		read(image.fd, &byte, 1);

		if((num) % lineLen == 0)
		{
			printf("\n");
		}

		if(num % 32 == 0)
		{
			printf("\n");
		}
		printf("%X ", byte);
	}
}

//-----------------
// setOffsetToClusterHeapIndex
//
// I can never remember the order of lseek's parameters
//-----------------
void setOffsetToClusterHeapIndex(Image image, int index)
{
	assert(index > 1);
	lseek(image.fd, getOffsetOfClusterHeapIndex(image, index), SEEK_SET);
}

//-----------------
// getOffsetOfClusterHeapIndex
//
// saves on math, pretty self-explanatory
//-----------------
int64_t getOffsetOfClusterHeapIndex(Image image, int index)
{
	return image.mbs->clusterHeapOffset * image.mbs->bytesPerSectorShift + (index - FAT_FIRST_INDEX ) * bytesPerCluster(image);
}

//-----------------
// getNextDirEntrySet
//
// assuming that the iterator is aligned with the 32 bit directory entries,
// creates a struct of the data in the next sequence of File, Stream Extension, 
// and File Name Directory Entries in the iterator
//-----------------
DirEntrySet *getNextDirEntrySet(Image image, Iterator *iter)
{
	// iterator must be aligned with 32 byte DirectoryEntries, if for example the iterator starts in
	// the middle of a directory entry, then this function will fail to find any DirectoryEntries
	login("getting next directory entry set");
	FileEntry *currEntry = (FileEntry*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
	StreamExt *streamExt = NULL;
	NameEntry *currNameEntry = NULL;
	DirEntrySet *nextSet = NULL;
	char *filename;
	char *asciiName;

	// find the next file entry
	while(currEntry != NULL && currEntry->entryType != FILE_ENTRY && currEntry->entryType != END_OF_DIRECTORY)
	{
		free(currEntry);
		currEntry = (FileEntry*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
	}

	// if a file entry was found, then construct the DirEntrySet from it and the subsequence stream
	// extension and file name entries
	if(currEntry != NULL)
	{
		if(currEntry->entryType == FILE_ENTRY)
		{
			debugLog("file entry found");
			filename = calloc(MAX_FILENAME_SIZE + 1, sizeof(int8_t));
			filename[0] = '\0';

			streamExt = (StreamExt*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
			sprintf(msg, "stream ext first cluster %d", streamExt->firstCluster);
			debugLog(msg);

			for(int i = streamExt->nameLength; i > 0; i -= MAX_NAME_SEGMENT_IN_ENTRY )
			{
				currNameEntry = (NameEntry*)getIteratorBytes(image, iter, DIRECTORY_ENTRY_SIZE);
				asciiName = unicode2ascii(&currNameEntry->name[0], min(i, MAX_NAME_SEGMENT_IN_ENTRY ));
				strcat(filename, asciiName);

				free(currNameEntry);
				free(asciiName);
			}

			nextSet = malloc(sizeof(DirEntrySet));
			nextSet->type = currEntry->fileAttributes & DIRECTORY_ATTRIBUTE ? IS_DIRECTORY : IS_FILE;
			nextSet->firstCluster = streamExt->firstCluster;
			nextSet->dataLength = streamExt->dataLength;
			nextSet->name = realloc(filename, streamExt->nameLength + 1);

			free(streamExt);
			sprintf(msg, "directory set: type %s, first cluster %d, name %s", nextSet->type == IS_FILE ? "file" : "directory", nextSet->firstCluster, nextSet->name);

		}else if(currEntry->entryType == END_OF_DIRECTORY)
		{
			sprintf(msg, "end of directory reached");
		}
		free(currEntry);
	}else
	{
		sprintf(msg, "no more space in directory clusters");
	}
		
	logout(msg);
	return nextSet;
}

// which is smaller?
int min(int a, int b)
{
	return a < b ? a : b;
}

/**
 * Convert a Unicode-formatted string containing only ASCII characters
 * into a regular ASCII-formatted string (16 bit chars to 8 bit
 * chars).
 *
 * NOTE: this function does a heap allocation for the string it
 *       returns, caller is responsible for `free`-ing the allocation
 *       when necessary.
 *
 * uint16_t *unicode_string: the Unicode-formatted string to be
 *                           converted.
 * uint8_t   length: the length of the Unicode-formatted string (in
 *                   characters).
 *
 * returns: a heap allocated ASCII-formatted string.
 */
static char *unicode2ascii( uint16_t *unicode_string, uint8_t length )
{
	assert( unicode_string != NULL );
	assert( length > 0 );

	char *ascii_string = NULL;

	if ( unicode_string != NULL && length > 0 )
	{
		// +1 for a NULL terminator
		ascii_string = calloc( sizeof(char), length + 1);

		if ( ascii_string )
		{
			// strip the top 8 bits from every character in the
			// unicode string
			for ( uint8_t i = 0 ; i < length; i++ )
			{
				ascii_string[i] = (char) unicode_string[i];
			}
			// stick a null terminator at the end of the string.
			ascii_string[length] = '\0';
		}
	}

	return ascii_string;
}

//-------------------
// Cluster Chain Abstraction
//-------------------

//-----------------
// makeClusterList
//
// recursively make a linked list out of the clusters referenced by the 
// cluster chain starting with firstClusterIndex in the volume's FAT
//-----------------
ClusterList *makeClusterList(Image image, int firstClusterIndex)
{
	login("making cluster list");
	ClusterList *list = malloc(sizeof(ClusterList));
	list->size = recursiveClusterListAppend(image, list, NULL, firstClusterIndex);

	logout("made cluster list");
	return list;
}

//-----------------
// recursiveClusterListAppend
//
// make the linked list nodes, appending each cluster node to the last node
// in the list
//-----------------
int recursiveClusterListAppend(Image image, ClusterList *list, ClusterNode *curr, int clusterIndex)
{
	assert((clusterIndex >= 2 && clusterIndex <= image.mbs->clusterCount + 1) || clusterIndex == 0 || clusterIndex == -1);
	sprintf(msg, "appending cluster %d", clusterIndex);
	login(msg);

	int32_t nextIndex;
	int64_t indexOffset;

	if(clusterIndex == END_OF_FILE || clusterIndex == EMPTY)
	{
		// end of cluster chain reached, end recursion
		assert(curr != NULL);

		curr->next = NULL;
		sprintf(msg, "End of list, cluster %d referenced", clusterIndex);
		logout(msg);
		return 0;
	}else
	{
		// recursive case
		ClusterNode *node = malloc(sizeof(ClusterNode));
		node->index = clusterIndex;
		node->offset = getOffsetOfClusterHeapIndex(image, clusterIndex);

		if(curr == NULL)
		{
			list->first = node;
		}else
		{
			curr->next = node;
		}

		indexOffset = image.mbs->fatOffset * image.mbs->bytesPerSectorShift + clusterIndex * sizeof(int32_t);
		lseek(image.fd, indexOffset, SEEK_SET);
		read(image.fd, &nextIndex, sizeof(int32_t));

		sprintf(msg, "Recursive case, moving to append cluster %d", nextIndex);
		logout(msg);

		return 1 + recursiveClusterListAppend(image, list, node, nextIndex);
	}
}

//-----------------
// makeClusterIterator
//
// make an iterator to simplify interactions with cluster chains
// iterator is an abstraction that allows us to pretend that the data in a
// file is contiguous rather than potentialy fragmented
//-----------------
Iterator *makeClusterIterator(Image image, int firstClusterIndex, int64_t dataLength)
{
	login("making cluster iterator");

	Iterator *iter = malloc(sizeof(Iterator));
	iter->list = makeClusterList(image, firstClusterIndex);
	iter->fd = image.fd;
	assert(iter->list->first != NULL);
	iter->currNode = iter->list->first;
	iter->currOffset = iter->currNode->offset;
	iter->clusterSize = bytesPerCluster(image);

	if(dataLength == -1)
	{
		// certain files (such as the root directory) will not have a predefined length
		iter->fileSize = iter->clusterSize * iter->list->size;
	}else
	{
		iter->fileSize = dataLength;
	}
	iter->index = 0;

	logout("made cluster iterator");
	return iter;
}

//-----------------
// iteratorToString
//
// states the current state of the iterator
//-----------------
char *iteratorToString(Iterator *iter)
{
	char *string = calloc(100, sizeof(char));
	sprintf(string, 
			"Iterator: cluster %d, offset %ld out of %ld, total clusters %d", 
			iter->currNode->index, iter->index, iter->fileSize, iter->list->size);
	return string;
}

//-----------------
// iteratorHasBytes
//
// check if the iterator has the desired amount of bytes left
//-----------------
int iteratorHasBytes(Iterator *iter, int bytes)
{
	sprintf(msg, "checking iter bytes: filesize %ld, bytes %d vs current index %ld", iter->fileSize, bytes, iter->index);
	debugLog(msg);
	return iter->fileSize - bytes >= iter->index;
}

//-----------------
// getIteratorBytes
//
// returns an array of bytes from the cluster iterator, or null of there are
// not enough bytes left to be consumed
//
// works by tracking the current file cluster, and offset within that file
// it will return the asked for amount of bytes from within that cluster, or
// wrapping data from the current and next cluster (if there is one), 
// updating the iterator's state as needed
//-----------------
int8_t *getIteratorBytes(Image image, Iterator *iter, int bytes)
{
	//login("getting iterator bytes");
	//char *string iteratorToString(iter);
	//debugLog(string);
	//free(string);
	int8_t *data = NULL;
	int index;
	int remaining;
	int min;
	if(iteratorHasBytes(iter, bytes))
	{
		data = calloc(bytes, sizeof(int8_t));
		index = 0;
		remaining = clusterSpaceRemaining(iter);

		while(bytes > 0)
		{
			min = bytes > remaining ? remaining : bytes;

			lseek(image.fd, iter->currOffset, SEEK_SET);
			read(image.fd, &data[index], min);

			bytes -= min;
			remaining -= min;
			index += min;
			iter->index += min;
			iter->currOffset += min;

			if(remaining == 0)
			{
				iter->currNode = iter->currNode->next;

				if(iter->currNode)
				{
					iter->currOffset = iter->currNode->offset;
					remaining += iter->clusterSize;
				}else
				{
					iter->currOffset = -1;
				}
			}
		}
	}

	//logout("got iterator bytes");
	return data;
}

//---------------------
// clusterSpaceRemaining
//
// how much space remains in the current cluster which the iterator has not
// already consumed?
//---------------------
int clusterSpaceRemaining(Iterator *iter)
{
	return iter->clusterSize - (iter->currOffset - iter->currNode->offset);
}

// free all allocated space associated with the iterator
void freeIterator(Iterator *iter)
{
	ClusterNode *node = iter->list->first;
	ClusterNode *nextNode;

	while(node != NULL)
	{
		nextNode = node->next;
		free(node);
		node = nextNode;
	}

	free(iter->list);
	free(iter);
}


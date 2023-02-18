CC=clang++
CFLAGS=--std=c++11

objects = EndAssembly.o Event.o ListItem.o Node.o OrderedItem.o Part.o PartArrival.o PriorityQueue.o ProductArrival.o Queue.o Simulation.o StartAssembly.o

all: A2main.o $(objects)
	$(CC) $(CFLAGS) -o A2 $< $(objects)

#all: test.o $(objects)
#	$(CC) $(CFLAGS) -o test $< $(objects)

#test.o: catch.hpp test.cpp $(objects)
#	$(CC) -c test.cpp

EndAssembly.o: EndAssembly.cpp EndAssembly.h
	$(CC) -c EndAssembly.cpp
	
Event.o: Event.cpp Event.h
	$(CC) -c Event.cpp
	
ListItem.o: ListItem.cpp ListItem.h
	$(CC) -c ListItem.cpp
	
Node.o: Node.cpp Node.h
	$(CC) -c Node.cpp
	
OrderedItem.o: OrderedItem.cpp OrderedItem.h
	$(CC) -c OrderedItem.cpp
	
Part.o: Part.cpp Part.h
	$(CC) -c Part.cpp
	
PartArrival.o: PartArrival.cpp PartArrival.h
	$(CC) -c PartArrival.cpp
	
PriorityQueue.o: PriorityQueue.cpp PriorityQueue.h
	$(CC) -c PriorityQueue.cpp

ProductArrival.o: ProductArrival.cpp ProductArrival.h
	$(CC) -c ProductArrival.cpp
	
Queue.o: Queue.cpp Queue.h
	$(CC) -c Queue.cpp
	
Simulation.o: Simulation.cpp Simulation.h
	$(CC) -c Simulation.cpp
	
StartAssembly.o: StartAssembly.cpp StartAssembly.h
	$(CC) -c StartAssembly.cpp

clean :
	rm A2main.o EndAssembly.o Event.o ListItem.o Node.o OrderedItem.o Part.o PartArrival.o PriorityQueue.o ProductArrival.o Queue.o Simulation.o StartAssembly.o

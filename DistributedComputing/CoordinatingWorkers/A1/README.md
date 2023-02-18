
# Distributed Computing Assignment 1
Xian Mardiros
7862786

To run the program, just run python3 test4.py <port>
alternatively, run separetly the workers client and coordinator
python3 coordinator.py <port>
python3 worker.py
python3 test_cli.py <port>

The workers must be specified after the coordinator. The intention was to fully complete teh bonus, but I didn't have time.

test4 runs a multithreaded process generator the tests the validity of each operation and of the databases.

## Protocol

### Worker Commands
SET-LOCK Key, Value
returns YES or NO
YES if there is no pre-existing lock for Key, NO if there is

SET-COMMIT Key
returns ACK
Sets Key to the stored value

SET-ABORT Key
returns ACK
deletes the lock for Key


COMMIT 
commits the previous set command

ABORT
aborts the previous set command

GET-DB
returns the entire database

GET
returns the value of the key

## STAGE 1

create a server that can connect to one client
the client sends commands to the coordinator, and the coordinator prints them out

## STAGE 2

create a server that can connect to one client and one worker
the client sends a set request to the coordinator, which send it to the worker, which prints it out

## STAGE 3

client connects to coordinator, which sends client commands to the worker, which prints them out

## STAGE 4

client connects to coordinator, worker stores state, which is returned to the client

## STAGE 5

implement 2 phase commit with a single worker

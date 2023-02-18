# COMP3010 Distributed Computing Assignment 2
Xian Mardiros
March 21, 2021

### Run instructions
To start the server:
`firefox localhost:50002/ & python3 ~/Documents/'Current Term'/COMP3010/A2/server.py`

To run the scraper:
With the server running
`make && ./scraper 1 testname testnote`

NOTE: The deletion assertion will fail if a memo if working with a memo name and note that are already in the database, as it will do a regex search for that info, and though one copy will have been deleted, the other copy will still be there.
This script should only be run with new inputs.


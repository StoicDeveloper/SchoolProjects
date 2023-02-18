# COMP-3430 Assignment 3
Xian Mardiros
7862786
July 13, 2021

## Run instructions

To run a single simulation:
$ make
$ ./schedSim <cpuNum> <scheduler> [-f <filename>] [-m]
example 1
$ ./schedSim 4 sjf -f fewtasks.txt -m
This command would execute the tasks in fewtasks.txt on 4 cpus with the sjf scheduler, producing minimal output.
example 2
$ ./schedSim 2 mlfq
This command would run the default tasks.txt on 2 cpus with the mlfq scheduler.

To run many simulations and print the results:
$ ./runSims
This command will do 5 runs each on 1, 2, 4, and 8 cpus, using mlfq and then sjf schedulers, and printing out the average turnaround and response times for each setting. These settings are configurable in runSims.c.

## Policy Results

### initial results
| CPUs | Type | MLFQ Turnaround Avg | MLFQ Response Avg | SJF Turnaround Avg | SJF Response Avg |
|------+------+---------------------+-------------------+--------------------+------------------|
| 1    | 0    | 35229               | 740               | 582                | 500              |
| 1    | 1    | 86997               | 632               | 1874               | 1802             |
| 1    | 2    | 279900              | 728               | 3290               | 3226             |
| 1    | 3    | 244807              | 847               | 3154               | 3106             |
| 2    | 0    | 32557               | 373               | 546                | 471              |
| 2    | 1    | 83657               | 312               | 1857               | 1782             |
| 2    | 2    | 280788              | 359               | 3066               | 3017             |
| 2    | 3    | 245298              | 432               | 2906               | 2864             |
| 4    | 0    | 32931               | 443               | 468                | 402              |
| 4    | 1    | 81394               | 376               | 1470               | 1409             |
| 4    | 2    | 276183              | 436               | 2624               | 2504             |
| 4    | 3    | 241602              | 510               | 2520               | 2409             |
| 8    | 0    | 33810               | 424               | 595                | 513              |
| 8    | 1    | 84261               | 357               | 1818               | 1703             |
| 8    | 2    | 273175              | 414               | 2789               | 2597             |
| 8    | 3    | 238436              | 491               | 2670               | 2505             |

### results after refactor to improve efficiency

| CPUs | Type | MLFQ Turnaround Avg | MLFQ Response Avg | SJF Turnaround Avg | SJF Response Avg |
|------|------|---------------------|-------------------|--------------------|------------------|
| 1    | 0    | 33763               | 631               | 340                | 295              |
| 1    | 1    | 86250               | 529               | 1073               | 1033             |
| 1    | 2    | 297615              | 611               | 1829               | 1797             |
| 1    | 3    | 259630              | 727               | 1754               | 1730             |
| 2    | 0    | 31306               | 365               | 459                | 397              |
| 2    | 1    | 81093               | 304               | 1490               | 1431             |
| 2    | 2    | 280562              | 353               | 2577               | 2526             |
| 2    | 3    | 244742              | 424               | 2475               | 2437             |
| 4    | 0    | 8626                | 486               | 525                | 450              |
| 4    | 1    | 19324               | 398               | 1724               | 1652             |
| 4    | 2    | 61213               | 461               | 2784               | 2706             |
| 4    | 3    | 53427               | 565               | 2654               | 2587             |
| 8    | 0    | 7947                | 587               | 462                | 382              |
| 8    | 1    | 17056               | 499               | 1023               | 940              |
| 8    | 2    | 53614               | 578               | 1425               | 1295             |
| 8    | 3    | 46795               | 676               | 1374               | 1263             |

## Report

Is the difference in turnaround time and response time for each policy what you expected to see?
Why or why not?

The difference in turnaround time and response time for each policy is as expected. Because the MLFQ policy prioritizes response time by cycling through all tasks, running each for at most SLICE time before moving them to the back of their current queue, and moving tasks to a lower priority queue once they've run for ALLOTMENT time, it would be expected that its response time would be very low, since the response time for each task does not significantly depend on its own length or on the other tasks. However, this will also make the turnaround time very high, as tasks that might otherwise finish faster, by spending less time waiting, must instead wait for the equal treatment of slower tasks. The SJF policy would be expected to have a higher response time, but much lower turnaround time, since SJF will always run the shortest job first (obviously), so that shorter jobs don't have to wait for longer jobs to partially complete. This reduces turnaround by causing many jobs to complete faster, but increases response time, since the longer jobs have to wait before running for the first time. The actual results align with these expectations precisely.

How does adjusting the number of CPUs in the system affect the turnaround time or response time
for tasks? Does it appear to be highly correlated?

For both mlfq and sjf, increasing the number of cpus appears to have a slight reducing effect on turnaround and response time. For mlfq, the effect was more significant on response time, but had diminishing returns. I would have expected the difference to be more significant, since having more CPUs available would allow tasks to be addressed sooner, reducing response time for both sjf and mlfq, and reducing the difference in time from arrival to completion. This latter effect would be smaller if waiting time was small compared to running time, and so it makes sense that the reduction in turnaround time would be less significant. The lack of a pronounced effect of adding cpus could be a result of the inefficiency (though greater reliability) caused by having condition variables and locking, if cpus were often having to wait for each other and the scheduler. Another impelementation which reduced waiting time could possible have a more pronounced cpu effect. 
NOTE: after refactoring to improve efficiency the effects of increasing the number of cpus became much more significant, reducing MLFQ turnaround by nearly an order of magnitude from 1-8 cpus, though differences in response time was negligible, and differences for each statistic of the SJF policy was still minor, though more significant than the previous implementation (the improvements mostly occurred in the MLFQ algorithm).

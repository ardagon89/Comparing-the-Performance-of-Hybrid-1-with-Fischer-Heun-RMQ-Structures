README:
======

Short Project # 10.b: Fischer-Heun RMQ structure for Minimum Range Query with Fully-Preprocessed BlockRMQ at the bottom and Block-Minima RMQ at the top.


Authors :
------
1) Shariq Ali SXA190016
2) Bhushan Vasisht BSV180000


How to compile and run the code:
-------------------------------
The files FischerHeunRMQ.java, HybridRMQ.java & Timer.java should be placed inside the folder named as 'sxa190016' which is the package name.
Run the below commands sequentially to execute the program

1) The command prompt path should be in "sxa190016" directory
2) javac Timer.java
2) javac HybridRMQ.java
3) javac FischerHeunRMQ.java
3) java -Xmx16g HybridRMQ
3) java -Xmx16g FischerHeunRMQ
	
Note: Only Integers are valid as input values.
Note: Integer.MAX_VALUE denotes incorrect operation.


Methods in Code:
-------------------
The following methods are written for FischerHeunRMQ class:

FischerHeunRMQ(int [] arr) 		- Constructor to initialize the FischerHeunRMQ class

fillMinArr() 				- Helper method to find the minimum element of each block and populate the minArray

fillFullPreprocessArr(int start,int end)- Helper method to assign an index to each BlockRMQ based on it's Cartesian Encoding and calculate the minima for each pair of (i, j) if not done previously for this Cartesian Encoding

fillSparseArr() 			- Helper method to fill the sparseArray in a DP fashion

queryMinRMQ(int i, int j) 		- Helper method to query the Block-Minima RMQ (Sparse Array) structure in constant time

query(int i, int j) 			- The main method to query the Hybrid RMQ structure

min(int [] arr, int start, int end) 	- Helper method to find the minimum element in any array in a given range

queryFullPreprocTbl(int block,int start, int end) - Method to find the minimum element in a given range in Block RMQ

getCartesianEncoding(int start,int end) - Get the Cartesian Encoding for array elements in a given range

getFullMinArray(int start, int end)	- To calculate the Fully-Preprocessed table for a given range

main(String args []) 			- Main method to test the program

printSparseArr() 			- Helper method to print the Sparse Array in a readable format

print2DArr(int [][] arr)		- Helper method to print any 2D Array in a readable format


The main function:
-------------------
When you run the main function, it will
1. Initialize and array of desired size
2. Initialize scanner with a file containing randomly generated numbers
3. Populate the array
4. Set the timer
5. Create an object of the FischerHeunRMQ class
6. Calculate average pre-processing time
7. Print the pre-processing time
8. Uncomment to check correctness of the implementation
9. Calculate average query time on 100000 random ranges
10. Query 10% range in the FischerHeunRMQ and print the average time of execution
11. Query 20% range in the FischerHeunRMQ and print the average time of execution
12. Query 30% range in the FischerHeunRMQ and print the average time of execution
13. Query 40% range in the FischerHeunRMQ and print the average time of execution
14. Query 50% range in the FischerHeunRMQ and print the average time of execution
15. Query 60% range in the FischerHeunRMQ and print the average time of execution
16. Query 70% range in the FischerHeunRMQ and print the average time of execution
17. Query 80% range in the FischerHeunRMQ and print the average time of execution
18. Query 90% range in the FischerHeunRMQ and print the average time of execution
19. Query 100% range in the FischerHeunRMQ and print the average time of execution


Report:
-------------------
Note: 
# All the values are in milli-seconds
# M - million operations


					Avg. Preprocessing Time
ArraySize:		128M			256M			512M
HybridRMQ:		2667.3 msec.		5392.8 msec.		11927.1 msec.
FischerHeunRMQ:		14342.2 msec.		29323.0 msec.		66873.1 msec.


					Avg. Query Time
ArraySize:		128M			256M			512M
HybridRMQ:		2.4E-4 msec.		4.0E-4 msec.		8.0E-4 msec.
FischerHeunRMQ:		2.8E-4 msec.		4.0E-4 msec.		0.0036 msec.


				Effect of Range on Query Time of HybridRMQ
ArraySize:		128M			256M			512M
10% Range: 		1.6E-4 msec.		2.0E-4 msec.		1.8E-4 msec.
20% Range: 		1.2E-4 msec.		8.0E-5 msec.		1.6E-4 msec.
30% Range: 		8.0E-5 msec.		1.2E-4 msec.		1.2E-4 msec.
40% Range: 		8.0E-5 msec.		8.0E-5 msec.		1.2E-4 msec.
50% Range: 		1.2E-4 msec.		1.2E-4 msec.		1.6E-4 msec.
60% Range: 		1.2E-4 msec.		1.6E-4 msec.		2.0E-4 msec.
70% Range: 		1.2E-4 msec.		1.2E-4 msec.		1.6E-4 msec.
80% Range: 		1.2E-4 msec.		1.6E-4 msec.		2.8E-4 msec.
90% Range: 		1.6E-4 msec.		1.6E-4 msec.		2.0E-4 msec.
100% Range: 		1.2E-4 msec.		1.2E-4 msec.		2.0E-4 msec.


				Effect of Range on Query Time of FischerHeunRMQ
ArraySize:		128M			256M			512M
10% Range: 		1.2E-4 msec.		1.1E-4 msec.		9.0E-5 msec.
20% Range: 		1.6E-4 msec.		1.2E-4 msec.		1.6E-4 msec.
30% Range: 		8.0E-5 msec.		1.2E-4 msec.		1.5E-4 msec.
40% Range: 		8.0E-5 msec.		1.2E-4 msec.		1.6E-4 msec.
50% Range: 		1.2E-4 msec.		1.6E-4 msec.		1.6E-4 msec.
60% Range: 		1.2E-4 msec.		1.2E-4 msec.		1.5E-4 msec.
70% Range: 		4.0E-5 msec.		1.6E-4 msec.		1.5E-4 msec.
80% Range: 		1.5E-4 msec.		1.6E-4 msec.		2.9E-4 msec.
90% Range: 		1.6E-4 msec.		1.6E-4 msec.		1.6E-4 msec.
100% Range: 		1.6E-4 msec.		1.2E-4 msec.		1.6E-4 msec.


Summary:
-------------------
FischerHeunRMQ has the same or better time complexity for both pre-processing and querying times as compared to HybridRMQ but this times comlexity hides some pretty large constants. So even though it is a much better algorithm theoretically, but due to complex processing, HybridRMQ performs better in real life. 

The pre-processing time of FischerHeunRMQ is much higher than HybridRMQ. This is to be expected as it does full pre-processing for BlockRMQ where as HybridRMQ does no pre-processing for BlockRMQ. The pre-processing time of FischerHeunRMQ increases almost lineraly with the increase in input size.

The average query time of both algorithms is very similar, except for the last test case. This is a surprise as the query time of FischerHeunRMQ is O(1) where as that of HybridRMQ is O(log n), so the former should be faster. But this can be attributed to multiple memory look-ups required for querying in FischerHeunRMQ, which add up to the total query time. The average query time of both algorithms also increases with increase in input size.

Finally, by looking at the last 2 tables we can clearly see the effect of range on HybridRMQ and FischerHeunRMQ. Neither of the algorithms are effected by querying on small or large ranges. Most of the average query time is scattered around a median value with minor deviations for individual inputs. So we can say that the size of the query range doesn't effect the query time.
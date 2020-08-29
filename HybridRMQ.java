package sxa190016;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import sxa190016.Timer;

/**
 * @author sxa190016
 * @author bsv180000
 * @version 1.0 Hybrid 1 RMQ: Short project 10
 * 				Hybrid 1 RMQ structure for Minimum Range Query
 * 				with BlockRMQ at the bottom and Block-Minima RMQ at the top.
 */
public class HybridRMQ {
	/**
	 * Store the size of the input array.
	 */
	private int n;

	/**
	 * The input array
	 */
	private int [] arr;

	/**
	 * The minimum array populated with the minimum of each block level RMQ
	 */
	private int [] minArr;

	/**
	 * Sparse array to allow constant time search
	 */
	private int [][] sparseArr;

	/**
	 * Block size of the BlockRMQ
	 */
	private int blockSize;

	/**
	 * Set to 1 to print all the intermediate steps
	 */
	public int VERBOSE;

	/**
	 * Constructor to initialize the HybridRMQ class
	 * 
	 * @param arr	The input array to be queried
	 */
	public HybridRMQ(int [] arr)
	{
		this.VERBOSE = 0;
		this.n = arr.length;
		if(this.n>0)
		{
			this.arr = arr;
			
			//Set optimal block size to log2(n) 
			this.blockSize = (int) Math.round(Math.log(this.n)/Math.log(2));
			this.minArr = new int [(int) Math.ceil(this.n*1.0/this.blockSize)];
			this.sparseArr = new int [(int) (Math.ceil(Math.log(this.minArr.length)/Math.log(2))+1)][];
			
			//Fill the minArray containing the minimum elements of each block
			this.fillMinArr();
			
			//Fill the Sparse Table in a DP fashion
			this.fillSparseArr();
			if(this.VERBOSE>0)
			{
				System.out.println("n: "+this.n);
				System.out.println("blockSize: "+this.blockSize);
				System.out.println("minArr.length: "+this.minArr.length);
				System.out.println("SparseArr.length:"+this.sparseArr.length);
			}
		}		
	}

	/**
	 * Helper method to find the minimum element of each block and populate the minArray
	 */
	public void fillMinArr()
	{
		for(int i=0; i<this.minArr.length-1; i++)
		{
			this.minArr[i] = this.min(this.arr, i*this.blockSize, (i+1)*this.blockSize);
		}
		this.minArr[this.minArr.length-1] = this.min(this.arr, (this.minArr.length-1)*this.blockSize, this.arr.length);
	}

	/**
	 * Helper method to fill the sparseArray in a DP fashion
	 */
	public void fillSparseArr()
	{
		this.sparseArr[0] = new int [this.minArr.length];
		System.arraycopy(this.minArr, 0, this.sparseArr[0], 0, this.minArr.length);
		for(int i=1; i<this.sparseArr.length; i++)
		{
			this.sparseArr[i] = new int [Math.max(this.minArr.length-(int)Math.pow(2, i)+1, 1)];
			if(this.VERBOSE>0)
			{
				System.out.println(i+"th array size:"+this.sparseArr[i].length);
			}
			for(int j=0; j<this.sparseArr[i].length; j++)
			{
				this.sparseArr[i][j] = this.sparseArr[i-1][j]<this.sparseArr[i-1][Math.min(j+(int)Math.pow(2, i-1), this.sparseArr[i-1].length-1)]?this.sparseArr[i-1][j]:this.sparseArr[i-1][Math.min(j+(int)Math.pow(2, i-1), this.sparseArr[i-1].length-1)];
			}
		}
	}

	/**
	 * Helper method to query the Block-Minima RMQ (Sparse Array) structure in constant time
	 * 
	 * @param i		Start index of the range
	 * @param j		End of the range
	 * @return		The minimum element in the range
	 */
	public int queryMinRMQ(int i, int j)
	{
		int k = (int) (Math.log(j-i+1)/Math.log(2));
		return this.sparseArr[k][i]<this.sparseArr[k][j-(int)Math.pow(2, k)+1]?this.sparseArr[k][i]:this.sparseArr[k][j-(int)Math.pow(2, k)+1];	
	}

	/**
	 * The main method to query the Hybrid RMQ structure
	 * 
	 * @param i		The start index of the range
	 * @param j		The end index of the range
	 * @return		The minimum element in the given range
	 */
	public int query(int i, int j)
	{
		if(this.n>0 && i>=0 && i<=j && j<this.n)
		{
			//Find the start index of the minArray
			int k = i%this.blockSize==0?i/this.blockSize:((i/this.blockSize)+1);
			
			//Find the end index of the minArray
			int l = (j+1)%this.blockSize==0?j/this.blockSize:(j/this.blockSize)-1;
			
			//Split the range into 3 queries : starting BlockLevel RMQ + TopLevel Block-Minima RMQ + ending BlockLevel RMQ
			if(k<=l)
			{
				if(i<k*this.blockSize)
				{
					if(j>=(l+1)*this.blockSize)
					{
						if(this.VERBOSE>0)
						{
							System.out.println("BlockRMQ:"+i+"-"+(k*this.blockSize-1)+" BlockRMQ:"+(l+1)*this.blockSize+"-"+j+" MinRMQ:"+k+"-"+l);
						}
						return Math.min(Math.min(this.min(this.arr, i, k*this.blockSize), this.min(this.arr, (l+1)*this.blockSize, j+1)), this.queryMinRMQ(k, l));
					}
					else
					{
						if(this.VERBOSE>0)
						{
							System.out.println("BlockRMQ:"+i+"-"+(k*this.blockSize-1)+" MinRMQ:"+k+"-"+l);
						}
						return Math.min(this.min(this.arr, i, k*this.blockSize), this.queryMinRMQ(k, l));
					}
				}
				else
				{
					if(j>=(l+1)*this.blockSize)
					{
						if(this.VERBOSE>0)
						{
							System.out.println("BlockRMQ:"+(l+1)*this.blockSize+"-"+j+" MinRMQ:"+k+"-"+l);
						}
						return Math.min(this.min(this.arr, (l+1)*this.blockSize, j+1), this.queryMinRMQ(k, l));
					}
					else
					{
						if(this.VERBOSE>0)
						{
							System.out.println("MinRMQ:"+k+"-"+l);
						}
						return this.queryMinRMQ(k, l);
					}
				}
			}
			else
			{
				if(this.VERBOSE>0)
				{
					System.out.println("BlockRMQ:"+i+"-"+j);
				}
				return this.min(this.arr, i, j+1);
			}
		}
		else
		{
			//Return max value if input is not in the correct format
			return Integer.MAX_VALUE;
		}

	}

	/**
	 * Helper method to find the minimum element in any array in a given range
	 * 
	 * @param arr		The array to be queried
	 * @param start		The start index of the range
	 * @param end		The end index of the range
	 * @return			The minimum element in the range in the given array
	 */
	public int min(int [] arr, int start, int end)
	{
		int result = arr[start];
		for(int i=start+1; i<end; i++)
		{
			if(arr[i]<result)
			{
				result = arr[i];
			}
		}
		return result;
	}

	/**
	 * Main method to test the program
	 * 
	 * @param args		To pass command line arguments
	 * @throws FileNotFoundException 
	 */
	public static void main(String args []) throws FileNotFoundException
	{
		//Initialize and array of desired size
		int [] arr = new int [128000000];
		
		//Initialize scanner with a file containing randomly generated numbers
		Scanner sc = new Scanner(new File("C:\\Users\\shari\\Downloads\\128M.txt"));
		
		//Populate the array
		for(int i=0; i<arr.length; i++)
		{
			arr[i] = sc.nextInt();
		}
		sc.close();
		
		int y=0;
		
		//Set the timer
		Timer t = new Timer();
		
		//Create an object of the HybridRMQ class
		HybridRMQ hr = null;
		for(y=0; y<10; y++)
		{
			hr = new HybridRMQ(arr);
		}
		t.end();
		System.out.println("Preprocessing Time:"+t.elapsedTime*1.0/y);
		
		Random rand = new Random();
		
		//Uncomment to check correctness of the implementation
//		for(int x=0; x<10000; x++)
//		{
//			int i = rand.nextInt(arr.length);
//			int j = rand.nextInt(arr.length);
//			int result = hr.query(i, j);
//			if(result!=Integer.MAX_VALUE && result!=hr.min(hr.arr, i, j+1))
//			{
//				System.out.println("Incorrect result! Min in range ("+i+","+j+") is not "+result);
//			}
//		}
		
		//Calculating average query time on 10000 random ranges
		int [][] testCases = new int[10000][2];
		for(int x=0; x<10000; x++)
		{
			testCases[x][0] = rand.nextInt(arr.length);
			testCases[x][1] = rand.nextInt(arr.length);			
		}
		
		y=0;
		t.start();
		for(int[] x : testCases)
		{
			hr.query(x[0], x[1]);
			y++;
		}
		t.end();
		System.out.println("Query Time:"+t.elapsedTime*1.0/y);
		
		//Re-initialize the timer
		t.start();
		int j = 0;		
		//Query 10% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*45), (arr.length/100*55));
			j++;
		}
		t.end();
		System.out.println("10% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 20% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*40), (arr.length/100*60));
			j++;
		}
		t.end();
		System.out.println("20% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 30% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*35), (arr.length/100*65));
			j++;
		}
		t.end();
		System.out.println("30% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 40% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*30), (arr.length/100*70));
			j++;
		}
		t.end();
		System.out.println("40% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 50% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*25), (arr.length/100*75));
			j++;
		}
		t.end();
		System.out.println("50% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 60% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*20), (arr.length/100*80));
			j++;
		}
		t.end();
		System.out.println("60% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 70% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*15), (arr.length/100*85));
			j++;
		}
		t.end();
		System.out.println("70% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 80% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*10), (arr.length/100*90));
			j++;
		}
		t.end();
		System.out.println("80% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 90% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*5), (arr.length/100*95));
			j++;
		}
		t.end();
		System.out.println("90% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 100% range in the HybridRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*0), (arr.length/100*100)-1);
			j++;
		}
		t.end();
		System.out.println("100% : "+t.elapsedTime*1.0/j+" msec.");
	}

	/**
	 * Helper method to print the Sparse Array in a readable format
	 */
	public void printSparseArr()
	{
		for(int[] x : this.sparseArr)
		{
			for(int y : x)
			{
				System.out.print(y+" ");
			}
			System.out.println();
		}
	}
}

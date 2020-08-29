package sxa190016;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import sxa190016.Timer;

/**
 * @author sxa190016
 * @author bsv180000
 * @version 1.0 Fischer-Heun RMQ: Short project 10
 * 				Fischer-Heun RMQ structure for Minimum Range Query
 * 				with Fully-Preprocessed BlockRMQ at the bottom and Block-Minima RMQ at the top.
 */
public class FischerHeunRMQ {
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
	 * Full preprocessing array to allow constant time search
	 */
	private int [][][] fullPreProcessArr;

	/**
	 * Block RMQ to Full-Preprocess array index mapping
	 */
	private int [] blockToPreprocessMap;

	/**
	 * Block size of the BlockRMQ
	 */
	private int blockSize;

	/**
	 * Set to 1 to print all the intermediate steps
	 */
	public int VERBOSE;

	/**
	 * Constructor to initialize the FischerHeunRMQ class
	 * 
	 * @param arr	The input array to be queried
	 */
	public FischerHeunRMQ(int [] arr)
	{
		this.VERBOSE = 0;
		this.n = arr.length;
		if(this.n>0)
		{
			this.arr = arr;

			//Set optimal block size to log4(n)/2
			this.blockSize = (int) Math.round((Math.log(this.n)/Math.log(4))/2);
			this.minArr = new int [(int) Math.ceil(this.n*1.0/this.blockSize)];
			this.sparseArr = new int [(int) (Math.ceil(Math.log(this.minArr.length)/Math.log(2))+1)][];
			this.fullPreProcessArr = new int[(int) Math.pow(4, this.blockSize)][][];
			this.blockToPreprocessMap = new int[this.minArr.length];
			if(this.VERBOSE>0)
			{
				System.out.println("n: "+this.n);
				System.out.println("blockSize: "+this.blockSize);
				System.out.println("minArr.length: "+this.minArr.length);
				System.out.println("SparseArr.length:"+this.sparseArr.length);
				System.out.println("fullPreProcessArr.length:"+this.fullPreProcessArr.length);
				System.out.println("blockToPreprocessMap.length:"+this.blockToPreprocessMap.length);
			}

			//Fill the minArray containing the minimum elements of each block
			this.fillMinArr();

			//Fill the Sparse Table in a DP fashion
			this.fillSparseArr();
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
			this.blockToPreprocessMap[i] = this.fillFullPreprocessArr(i*this.blockSize, (i+1)*this.blockSize-1);
		}
		this.minArr[this.minArr.length-1] = this.min(this.arr, (this.minArr.length-1)*this.blockSize, this.arr.length);
		this.blockToPreprocessMap[this.minArr.length-1] = this.fillFullPreprocessArr((this.minArr.length-1)*this.blockSize, this.arr.length-1);
	}

	/**
	 * Helper method to assign an index to each BlockRMQ based on it's Cartesian Encoding
	 * and calculate the minima for each pair of (i, j) if not done previously for this Cartesian Encoding
	 * 
	 * @param start	The Start index of the Block
	 * @param end	The end index of the block
	 * @return		The index at which fully-preprocessed table is stored for this Cartesian Encoding
	 */
	public int fillFullPreprocessArr(int start, int end)
	{
		String encoding = this.getCartesianEncoding(start, end);
		int index = Integer.parseInt(encoding,2);
		if(this.fullPreProcessArr[index] == null)
		{
			this.fullPreProcessArr[index] = this.getFullMinArray(start, end);
		}
		return index;
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
						return Math.min(Math.min(this.queryFullPreprocTbl(k-1, i, k*this.blockSize-1), this.queryFullPreprocTbl(l+1, (l+1)*this.blockSize, j)), this.queryMinRMQ(k, l));
					}
					else
					{
						if(this.VERBOSE>0)
						{
							System.out.println("BlockRMQ:"+i+"-"+(k*this.blockSize-1)+" MinRMQ:"+k+"-"+l);
						}
						return Math.min(this.queryFullPreprocTbl(k-1, i, k*this.blockSize-1), this.queryMinRMQ(k, l));
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
						return Math.min(this.queryFullPreprocTbl(l+1, (l+1)*this.blockSize, j), this.queryMinRMQ(k, l));
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
				if(i/this.blockSize==j/this.blockSize)
				{
					return this.queryFullPreprocTbl(i/this.blockSize, i, j);
				}
				else
				{
					return Math.min(this.queryFullPreprocTbl(i/this.blockSize, i, (j/this.blockSize)*this.blockSize-1), this.queryFullPreprocTbl(j/this.blockSize, (j/this.blockSize)*this.blockSize, j));
				}
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
	 * @param end		The last+1 index of the range
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
	 * Method to find the minimum element in a given range in Block RMQ
	 * 
	 * @param block	The index of the block RMQ
	 * @param start	The start index of the block
	 * @param end	The last index of the block
	 * @return		The minimum element in a given range in Block RMQ
	 */
	public int queryFullPreprocTbl(int block,int start, int end)
	{
		int i = start-(block*this.blockSize);
		int j = end-i-(block*this.blockSize);
		int minIndex = this.fullPreProcessArr[this.blockToPreprocessMap[block]][i][j];
		return this.arr[(block*this.blockSize)+minIndex];
	}

	/**
	 * Get the Cartesian Encoding for array elements in a given range
	 * 
	 * @param start	The start index of the block RMQ
	 * @param end	The last index of the block
	 * @return		The Cartesian Encoding for array elements in the given range
	 */
	public String getCartesianEncoding(int start, int end)
	{
		Stack<Node> stack = new Stack<Node>();
		StringBuilder sb = new StringBuilder("");

		for(int i=start; i<=end; i++)
		{
			Node newNode = new Node(this.arr[i], null, null);
			Node oldNode = null;
			while(!stack.isEmpty() && stack.peek().val>newNode.val)
			{
				oldNode = stack.pop();
				sb.append("0");
			}
			newNode.left = oldNode;
			if(!stack.isEmpty())
			{
				stack.peek().right = newNode;
			}
			stack.push(newNode);
			sb.append("1");
		}
		while(!stack.isEmpty())
		{
			stack.pop();
			sb.append("0");
		}

		return sb.toString();
	}

	/**
	 * To calculate the Fully-Preprocessed table for a given range
	 * 
	 * @param start	The start index of the Block RMQ
	 * @param end	The last index of the block
	 * @return		The Full-Preprocessed table for the given range
	 */
	public int[][] getFullMinArray(int start, int end)
	{
		int[][] arr = new int[this.blockSize][];
		for(int i=0; i<=end-start; i++)
		{
			arr[i] = new int[end-start-i+1];
			for(int j=0; j<=end-start-i; j++)
			{
				if(j==0)
				{
					arr[i][j]=i;
				}
				else
				{
					arr[i][j]=this.arr[start+i+j]<this.arr[start+i+j-1]?i+j:arr[i][j-1];
				}
			}
		}		
		return arr;
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
		
		//Create an object of the FischerHeunRMQ class
		FischerHeunRMQ hr = null;
		
		//Calculate average pre-processing time
		for(y=0; y<10; y++)
		{
			hr = new FischerHeunRMQ(arr);
		}
		t.end();
		
		//Print the pre-processing time
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

		//Calculate average query time on 100000 random ranges
		int [][] testCases = new int[100000][2];
		for(int x=0; x<100000; x++)
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
		
		//Print average query time
		System.out.println("Query Time:"+t.elapsedTime*1.0/y);

		//Re-initialize the timer
		t.start();
		int j = 0;		
		//Query 10% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*45), (arr.length/100*55));
			j++;
		}
		t.end();
		System.out.println("10% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 20% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*40), (arr.length/100*60));
			j++;
		}
		t.end();
		System.out.println("20% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 30% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*35), (arr.length/100*65));
			j++;
		}
		t.end();
		System.out.println("30% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 40% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*30), (arr.length/100*70));
			j++;
		}
		t.end();
		System.out.println("40% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 50% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*25), (arr.length/100*75));
			j++;
		}
		t.end();
		System.out.println("50% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 60% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*20), (arr.length/100*80));
			j++;
		}
		t.end();
		System.out.println("60% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 70% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*15), (arr.length/100*85));
			j++;
		}
		t.end();
		System.out.println("70% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 80% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*10), (arr.length/100*90));
			j++;
		}
		t.end();
		System.out.println("80% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 90% range in the FischerHeunRMQ and print the average time of execution
		for(int i=0; i<100000; i++)
		{
			hr.query((arr.length/100*5), (arr.length/100*95));
			j++;
		}
		t.end();
		System.out.println("90% : "+t.elapsedTime*1.0/j+" msec.");

		t.start();
		j = 0;
		//Query 100% range in the FischerHeunRMQ and print the average time of execution
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

	/**
	 * Helper method to print any 2D Array in a readable format
	 */
	public void print2DArr(int [][] arr)
	{
		for(int[] x : arr)
		{
			for(int y : x)
			{
				System.out.print(y+" ");
			}
			System.out.println();
		}
	}

	/**
	 * @author sxa190016
	 * @author bsv180000
	 * @version 1.0 Fischer-Heun RMQ: Short project 10
	 * 				Node class to construct the Cartesian tree during the calculation of 
	 * 				Cartesian encoding.
	 */
	static class Node {
		public int val;
		public Node left;
		public Node right;

		public Node(int val, Node leftChild, Node rightChild)
		{
			this.val = val;
			this.left = leftChild;
			this.right = rightChild;
		}
	}
}
